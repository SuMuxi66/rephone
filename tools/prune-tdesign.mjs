// TDesign 闭包裁剪 V3（微信开发者工具「构建 npm」之后必须重跑：npm run prune-npm）
//
// 原理：以「项目全部 json 的 usingComponents 引用的组件四件套」为根，在包内做文件级
// 可达性遍历。边类型：ESM import/export-from、require（相对路径 + 裸包名→嵌套/根 npm）、
// json usingComponents、wxss @import、wxml include/import/wxs src。
// 不可达文件全部删除（含未用组件整目录、common 内联后遗留的样式源文件、type.js、
// index.js 转出口、.d.ts 等）；tslib 因裸包名解析被两侧保留。
// 附带：.wechatide.ib.json、全部 *.map 一并清理。
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
function isFile(p) {
  try { return fs.statSync(p).isFile(); } catch (e) { return false; }
}

// ---------- 1. 项目侧引用收集（usingComponents 的 npm 包名/相对/根绝对三种风格） ----------
const NPM_PREFIX = 'tdesign-miniprogram' + path.sep;
function resolveCompRef(fromDir, ref) {
  if (ref.startsWith('.')) return path.resolve(fromDir, ref);
  if (ref.startsWith('/')) return path.join(ROOT, ref);
  return path.join(NPM, ref); // npm 包名风格
}
const rootBundles = new Set();
walk(ROOT, (p) => {
  if (!p.endsWith('.json')) return;
  const j = readJson(p);
  if (!j || !j.usingComponents) return;
  for (const ref of Object.values(j.usingComponents)) {
    const idxJs = resolveCompRef(path.dirname(p), ref) + '.js';
    if (!isFile(idxJs) || !idxJs.startsWith(NPM + path.sep)) continue;
    const base = idxJs.slice(0, idxJs.length - 3);
    for (const s of ['.js', '.json', '.wxml', '.wxss']) {
      const f = base + s;
      if (isFile(f)) rootBundles.add(f);
    }
  }
});
console.log('项目引用的包内入口四件套', rootBundles.size, '个文件');

// ---------- 2. 包内文件级可达性 ----------
const EDGES = [
  /import\s*[^"']*?from\s*["']([^"']+)["']/g,
  /import\s*["']([^"']+)["']/g,
  /require\(\s*["']([^"']+)["']\s*\)/g,
  /export\s*[^"']*?from\s*["']([^"']+)["']/g,
  /@import\s+["']([^"']+)["']/g,
  /<(?:include|import|wxs)\b[^>]*?src\s*=\s*["']([^"']+)["']/g,
];
function resolveSpec(fromFile, spec) {
  const out = [];
  if (spec.startsWith('.')) {
    const base = path.resolve(path.dirname(fromFile), spec);
    out.push(base, base + '.js', base + '.json', base + '.wxml', base + '.wxss', path.join(base, 'index.js'));
  } else {
    // 裸包名：编译器实际解析到包内嵌套 npm（实测根副本 moduleId=null 不入模块图）；
    // 嵌套不存在时才回退根 npm
    const nested = path.join(TD, 'miniprogram_npm', spec);
    const nestedHits = [nested, nested + '.js', path.join(nested, 'index.js')].filter(isFile);
    if (nestedHits.length) return nestedHits;
    const root = path.join(NPM, spec);
    return [root, root + '.js', path.join(root, 'index.js')].filter(isFile);
  }
  return out.filter(isFile);
}
const reachable = new Set(rootBundles);
const queue = [...rootBundles];
while (queue.length) {
  const f = queue.pop();
  if (!/\.(js|json|wxml|wxss)$/.test(f)) continue;
  let s = '';
  try { s = fs.readFileSync(f, 'utf8'); } catch (e) { continue; }
  const specs = [];
  for (const re of EDGES) {
    for (const m of s.matchAll(re)) specs.push(m[1]);
  }
  if (/\.json$/.test(f)) {
    const j = readJson(f);
    if (j && j.usingComponents) specs.push(...Object.values(j.usingComponents));
  }
  for (const spec of specs) {
    for (const t of resolveSpec(f, spec)) {
      if (!reachable.has(t)) { reachable.add(t); queue.push(t); }
    }
  }
}

// ---------- 3. 删除清单 ----------
const tdFiles = [];
(function w(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) w(p); else tdFiles.push(p);
  }
})(TD);
const removeList = [];
for (const p of tdFiles) {
  if (!reachable.has(p)) removeList.push([path.relative(NPM, p), fs.statSync(p).size, p]);
}
const ib = path.join(TD, '.wechatide.ib.json');
if (fs.existsSync(ib) && !reachable.has(ib)) removeList.push(['tdesign-miniprogram/.wechatide.ib.json', fs.statSync(ib).size, ib]);
walk(NPM, (p) => {
  if (p.endsWith('.map') && !reachable.has(p)) removeList.push([path.relative(NPM, p), fs.statSync(p).size, p]);
});
// 去重（ib 可能已在 tdFiles 里）
const seen = new Set();
const removeList2 = removeList.filter(([n, s, p]) => (seen.has(p) ? false : (seen.add(p), true)));
const total = removeList2.reduce((a, [, s]) => a + s, 0);
const removePaths = removeList2.map(([, , p]) => p);
const willBeRemoved = (abs) => removePaths.some((p) => abs === p || abs.startsWith(p + path.sep));
console.log('--- 删除清单', removeList2.length, '项，合计', (total / 1024).toFixed(0) + 'K ---');
removeList2.sort((a, b) => b[1] - a[1]).slice(0, 15).forEach(([n, s]) => console.log(' ', (s / 1024).toFixed(0) + 'K', n));

// ---------- 4. 校验（删除后视角：可达文件的所有出边目标必须存在且可达/在包外） ----------
function validate() {
  const errs = [];
  const gone = (abs) => !fs.existsSync(abs) || willBeRemoved(abs);
  for (const f of reachable) {
    if (gone(f) || !/\.(js|json|wxml|wxss)$/.test(f)) continue;
    let s = '';
    try { s = fs.readFileSync(f, 'utf8'); } catch (e) { continue; }
    const specs = [];
    for (const re of EDGES) {
      for (const m of s.matchAll(re)) specs.push(m[1]);
    }
    if (/\.json$/.test(f)) {
      const j = readJson(f);
      if (j && j.usingComponents) specs.push(...Object.values(j.usingComponents));
    }
    for (const spec of specs) {
      const targets = resolveSpec(f, spec);
      if (!targets.length) continue; // 包外或 npm 包名无法解析，跳过
      if (targets.every((t) => t.startsWith(TD) && gone(t))) {
        errs.push(path.relative(NPM, f) + ' -> ' + spec);
      }
    }
  }
  // 项目侧 usingComponents 目标必须存在且可达
  walk(ROOT, (p) => {
    if (!p.endsWith('.json')) return;
    const j = readJson(p);
    if (!j || !j.usingComponents) return;
    for (const ref of Object.values(j.usingComponents)) {
      const idxJs = resolveCompRef(path.dirname(p), ref) + '.js';
      if (!isFile(idxJs) || !idxJs.startsWith(NPM + path.sep)) continue;
      const base = idxJs.slice(0, idxJs.length - 3) + '.json';
      if (gone(base)) errs.push(path.relative(ROOT, p) + ' -> ' + ref);
    }
  });
  return errs;
}
const errs = validate();
console.log('--- 校验（删除后视角） ---');
console.log(errs.length ? 'FAIL ' + errs.length + ' 项:' : 'PASS（可达文件的出边在删除后全部完整）');
errs.slice(0, 20).forEach((e) => console.log('  !', e));

if (APPLY) {
  if (errs.length) {
    console.log('校验未过，拒绝执行删除');
    process.exit(1);
  }
  for (const [, , p] of removeList2) fs.rmSync(p, { recursive: true, force: true });
  console.log('APPLIED: 已删除', removeList2.length, '项，释放', (total / 1024).toFixed(0) + 'K');
  const errs2 = validate();
  console.log(errs2.length ? '删除后复检 FAIL: ' + errs2.slice(0, 10).join(' | ') : '删除后复检 PASS');
} else {
  console.log('DRY-RUN：加 --apply 执行删除');
}
