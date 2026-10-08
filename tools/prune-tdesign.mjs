// TDesign 闭包裁剪 V2（微信开发者工具「构建 npm」之后必须重跑：npm run prune-npm）
//
// 原理：组件目录自包含（wxss 只 @import common/style/**，跨组件依赖只走 .json 的
// usingComponents 相对引用），按「项目全部 json 的 usingComponents（含分包）→ 包内
// json 闭包展开」算出在用组件集合，删除未用目录并改写包入口 index.js。
// 保护对象：common/（样式基座）、mixins/、嵌套 miniprogram_npm 中在用的包（tslib）。
// 删除前做四重存在性校验（usingComponents/@import/require/index.js 导出），删除后复检。
// 附带清理：.wechatide.ib.json（IDE 生成物）、全部 *.map、嵌套 npm 未引用包。
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const NPM = path.join(ROOT, 'miniprogram_npm');
const TD = path.join(NPM, 'tdesign-miniprogram');
const APPLY = process.argv.includes('--apply');

const SKIP = new Set(['node_modules', 'miniprogram_npm', 'server', 'admin', 'docs', 'tools',
  '.zcode', '.zcode-tmp', '.git', 'dist', '.playwright-mcp']);

function walk(dir, cb) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (SKIP.has(e.name)) continue;
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, cb);
    else cb(p);
  }
}
function dirSize(d) {
  let n = 0;
  (function w(x) {
    for (const f of fs.readdirSync(x, { withFileTypes: true })) {
      const fp = path.join(x, f.name);
      if (f.isDirectory()) w(fp); else n += fs.statSync(fp).size;
    }
  })(d);
  return n;
}
function readJson(p) {
  try { return JSON.parse(fs.readFileSync(p, 'utf8')); } catch (e) { return null; }
}

// ---------- 1. 项目侧 usingComponents 收集 ----------
// 引用风格三种：npm 包名（tdesign-miniprogram/icon/icon，经 miniprogram_npm 解析）、
// 相对（./x、../x）、根绝对（/components/x）
const tdRoot = 'tdesign-miniprogram' + path.sep;
function resolveRef(fromDir, ref) {
  if (ref.startsWith('.')) return path.resolve(fromDir, ref);
  if (ref.startsWith('/')) return path.join(ROOT, ref);
  return path.join(NPM, ref);
}
function tdesignCompOf(abs) {
  const rel = path.relative(NPM, abs);
  if (!rel.startsWith(tdRoot) || rel === tdRoot) return null;
  return rel.slice(tdRoot.length).split(path.sep)[0];
}
const usedComps = new Set();
walk(ROOT, (p) => {
  if (!p.endsWith('.json')) return;
  const j = readJson(p);
  if (!j || !j.usingComponents) return;
  for (const ref of Object.values(j.usingComponents)) {
    const comp = tdesignCompOf(resolveRef(path.dirname(p), ref));
    if (comp) usedComps.add(comp);
  }
});

// ---------- 2. 包内闭包展开 ----------
// 基础设施目录不是组件：common（样式基座）、mixins、miniprogram_npm（嵌套 npm，另算）
const INFRA = new Set(['common', 'mixins', 'miniprogram_npm']);
const compDirs = fs.readdirSync(TD, { withFileTypes: true })
  .filter((e) => e.isDirectory() && !INFRA.has(e.name)).map((e) => e.name);
const queue = [...usedComps];
while (queue.length) {
  const comp = queue.pop();
  const dir = path.join(TD, comp);
  if (!fs.existsSync(dir)) continue;
  walk(dir, (p) => {
    if (!p.endsWith('.json')) return;
    const j = readJson(p);
    if (!j || !j.usingComponents) return;
    for (const ref of Object.values(j.usingComponents)) {
      const abs = path.resolve(path.dirname(p), ref);
      const rel = path.relative(TD, abs);
      if (!rel.startsWith('..')) {
        const c2 = rel.split(path.sep)[0];
        if (compDirs.includes(c2) && !usedComps.has(c2)) { usedComps.add(c2); queue.push(c2); }
      }
    }
  });
}

// ---------- 3. 删除清单 ----------
const removeList = [];
for (const comp of compDirs) {
  if (usedComps.has(comp)) continue;
  const p = path.join(TD, comp);
  removeList.push(['tdesign-miniprogram/' + comp + '/', dirSize(p), p]);
}
const ib = path.join(TD, '.wechatide.ib.json');
if (fs.existsSync(ib)) removeList.push(['tdesign-miniprogram/.wechatide.ib.json', fs.statSync(ib).size, ib]);
walk(NPM, (p) => { if (p.endsWith('.map')) removeList.push([path.relative(NPM, p), fs.statSync(p).size, p]); });
// 嵌套 npm：只在保留文件里扫引用
const usedNested = new Set(['tslib']);
for (const comp of usedComps) walk(path.join(TD, comp), (p) => {
  if (!/\.(js|wxml)$/.test(p)) return;
  const s = fs.readFileSync(p, 'utf8');
  for (const m of s.matchAll(/miniprogram_npm\/([a-z0-9._-]+)/g)) usedNested.add(m[1]);
});
const nestedDir = path.join(TD, 'miniprogram_npm');
if (fs.existsSync(nestedDir)) {
  for (const e of fs.readdirSync(nestedDir, { withFileTypes: true })) {
    if (e.isDirectory() && !usedNested.has(e.name)) {
      const p = path.join(nestedDir, e.name);
      removeList.push(['tdesign-miniprogram/miniprogram_npm/' + e.name + '/', dirSize(p), p]);
    }
  }
}

const total = removeList.reduce((a, [, s]) => a + s, 0);
const removePaths = removeList.map(([, , p]) => p);
function willBeRemoved(abs) {
  return removePaths.some((p) => abs === p || abs.startsWith(p + path.sep));
}
console.log('在用组件', usedComps.size, '个:', [...usedComps].sort().join(', '));
console.log('--- 删除清单', removeList.length, '项，合计', (total / 1024).toFixed(0) + 'K ---');

// ---------- 4. 校验（删除后视角：目标不存在【或将】被删即报错）----------
function validate() {
  const errs = [];
  const gone = (abs) => !fs.existsSync(abs) || willBeRemoved(abs);
  const checkJson = (p) => {
    const j = readJson(p);
    if (!j || !j.usingComponents) return;
    for (const ref of Object.values(j.usingComponents)) {
      const abs = resolveRef(path.dirname(p), ref);
      if (gone(abs + '.json')) errs.push(path.relative(ROOT, p) + ' -> ' + ref);
    }
  };
  walk(ROOT, (p) => { if (p.endsWith('.json')) checkJson(p); });
  for (const comp of usedComps) walk(path.join(TD, comp), (p) => { if (p.endsWith('.json')) checkJson(p); });
  const checkWxss = (base, p) => {
    const s = fs.readFileSync(p, 'utf8');
    for (const m of s.matchAll(/@import\s+["']([^"']+)["']/g)) {
      const abs = path.resolve(path.dirname(p), m[1]);
      if (gone(abs)) errs.push(path.relative(base, p) + ' @import ' + m[1]);
    }
  };
  walk(ROOT, (p) => { if (p.endsWith('.wxss')) checkWxss(ROOT, p); });
  for (const comp of usedComps) walk(path.join(TD, comp), (p) => { if (p.endsWith('.wxss')) checkWxss(TD, p); });
  walk(path.join(TD, 'common'), (p) => { if (p.endsWith('.wxss')) checkWxss(TD, p); });
  const checkJs = (p) => {
    const s = fs.readFileSync(p, 'utf8');
    for (const m of s.matchAll(/require\(\s*["'](\.[^"']+)["']\s*\)/g)) {
      const abs0 = path.resolve(path.dirname(p), m[1]);
      const hit = [abs0, abs0 + '.js', abs0 + '.json', path.join(abs0, 'index.js')]
        .some((cand) => fs.existsSync(cand) && !willBeRemoved(cand));
      if (!hit) errs.push(path.relative(ROOT, p) + ' require ' + m[1]);
    }
  };
  for (const comp of usedComps) walk(path.join(TD, comp), (p) => { if (p.endsWith('.js')) checkJs(p); });
  walk(path.join(TD, 'common'), (p) => { if (p.endsWith('.js')) checkJs(p); });
  const entry = path.join(TD, 'index.js');
  if (fs.existsSync(entry)) {
    const s = fs.readFileSync(entry, 'utf8');
    for (const m of s.matchAll(/from\s*["']\.\/([a-z0-9-]+)\/index["']/g)) {
      if (gone(path.join(TD, m[1], 'index.js'))) errs.push('index.js export ' + m[1] + '（apply 时自动改写入口）');
    }
  }
  return errs;
}
const errs = validate();
console.log('--- 校验（删除后视角） ---');
console.log(errs.length ? 'FAIL ' + errs.length + ' 项:' : 'PASS（保留文件的全部引用目标在删除后仍存在）');
errs.slice(0, 20).forEach((e) => console.log('  !', e));

if (APPLY) {
  if (errs.some((e) => !e.startsWith('index.js export'))) {
    console.log('存在非入口类校验错误，拒绝执行删除');
    process.exit(1);
  }
  // 改写包入口：只保留删除后仍存在的导出（判断须兼容「已删/将删」两种状态）
  const entry = path.join(TD, 'index.js');
  if (fs.existsSync(entry)) {
    const s = fs.readFileSync(entry, 'utf8');
    const kept = s.replace(/export\{[^}]*\}from\s*["']\.\/([a-z0-9-]+)\/index["'];?/g,
      (whole, comp) => {
        const t = path.join(TD, comp, 'index.js');
        return (willBeRemoved(t) || !fs.existsSync(t)) ? '' : whole;
      });
    fs.writeFileSync(entry, kept);
  }
  for (const [, , p] of removeList) fs.rmSync(p, { recursive: true, force: true });
  console.log('APPLIED: 已删除', removeList.length, '项，释放', (total / 1024).toFixed(0) + 'K');
  const errs2 = validate();
  console.log(errs2.length ? '删除后复检 FAIL: ' + errs2.slice(0, 10).join(' | ') : '删除后复检 PASS');
} else {
  console.log('DRY-RUN：加 --apply 执行删除');
}
