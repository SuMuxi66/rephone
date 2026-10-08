// TDesign 组件按需裁剪（微信开发者工具「构建 npm」之后运行一次）
// 用法：npm run prune-npm   （或 node tools/prune-tdesign.mjs，加 --apply 才真删，默认 dry-run）
// 原理：收集全仓 usingComponents 引用的 tdesign 组件 → 沿组件内部相对引用做依赖闭包 →
//       删除 miniprogram_npm/tdesign-miniprogram 下闭包外的组件、.wechatide.ib.json、*.map、
//       未被引用的嵌套 npm 包（dayjs/tinycolor2 等）。
// 注意：构建 npm 会全量恢复，删完的下次构建后需再跑一次。
import fs from 'node:fs';
import path from 'node:path';

const root = path.resolve(path.dirname(new URL(import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1')), '..');
const npmDir = path.join(root, 'miniprogram_npm');
const tdDir = path.join(npmDir, 'tdesign-miniprogram');
const apply = process.argv.includes('--apply');

// 1. 收集全仓页面/组件 json 里的 tdesign 组件引用（含分包）
const used = new Set();
(function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    if (['node_modules', '.git', 'server', 'admin', 'docs', 'tools', '.zcode', '.playwright-mcp', 'mp-quarantine'].includes(e.name)) continue;
    const p = path.join(d, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name.endsWith('.json')) {
      const s = fs.readFileSync(p, 'utf8');
      for (const m of s.matchAll(/tdesign-miniprogram\/([a-z0-9-]+)\/\1(?=["])/g)) used.add(m[1]);
    }
  }
})(root);

// 2. 依赖闭包：保留组件目录内所有文件对同级目录的相对引用（../name/...）
const keep = new Set(used);
const queue = [...used].map((n) => path.join(tdDir, n));
const REF_RE = /\.\.\/([a-z0-9._-]+)(?=\/|["'])/g;
while (queue.length) {
  const dir = queue.shift();
  if (!fs.existsSync(dir)) continue;
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { queue.push(p); continue; }
    if (!/\.(js|wxml|wxss|json)$/.test(e.name)) continue;
    const s = fs.readFileSync(p, 'utf8');
    for (const m of s.matchAll(REF_RE)) {
      const target = path.join(tdDir, m[1]);
      if (fs.existsSync(target) && fs.statSync(target).isDirectory() && !keep.has(m[1])) {
        keep.add(m[1]);
        queue.push(target);
      }
    }
  }
}

// 3. 共享目录白名单 + 嵌套 npm 按引用保留
for (const s of ['common', 'style', 'shared', '_common']) keep.add(s);
const keepNpm = new Set(['tslib']);
for (const name of keep) {
  if (['common', 'style', 'shared', '_common'].includes(name)) continue;
  const dir = path.join(tdDir, name);
  if (!fs.existsSync(dir)) continue;
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (e.isDirectory() || !/\.(js|wxml)$/.test(e.name)) continue;
    const s = fs.readFileSync(path.join(dir, e.name), 'utf8');
    for (const m of s.matchAll(/miniprogram_npm\/([a-z0-9._-]+)/g)) keepNpm.add(m[1]);
  }
}

// 4. 计算删除清单
const removeList = [];
let removeSize = 0;
const dirSize = (d) => { let n = 0; (function sz(x) { for (const e of fs.readdirSync(x, { withFileTypes: true })) { const xp = path.join(x, e.name); e.isDirectory() ? sz(xp) : (n += fs.statSync(xp).size); } })(d); return n; };
for (const e of fs.readdirSync(tdDir, { withFileTypes: true })) {
  const p = path.join(tdDir, e.name);
  if (e.isDirectory()) {
    if (keep.has(e.name)) continue;
    const size = dirSize(p);
    removeList.push([`${e.name}/`, size, p]);
    removeSize += size;
  } else if (e.name === '.wechatide.ib.json' || e.name.endsWith('.map')) {
    removeList.push([e.name, fs.statSync(p).size, p]);
    removeSize += fs.statSync(p).size;
  }
}
const nestedDir = path.join(tdDir, 'miniprogram_npm');
if (fs.existsSync(nestedDir)) {
  for (const e of fs.readdirSync(nestedDir, { withFileTypes: true })) {
    if (!e.isDirectory() || keepNpm.has(e.name)) continue;
    const p = path.join(nestedDir, e.name);
    const size = dirSize(p);
    removeList.push([`miniprogram_npm/${e.name}/`, size, p]);
    removeSize += size;
  }
}
(function findMap(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) findMap(p);
    else if (e.name.endsWith('.map')) { removeList.push([path.relative(tdDir, p), fs.statSync(p).size, p]); removeSize += fs.statSync(p).size; }
  }
})(tdDir);

console.log('使用组件(', used.size, '):', [...used].sort().join(', '));
console.log('闭包保留(', keep.size, '):', [...keep].sort().join(', '));
console.log('嵌套 npm 保留:', [...keepNpm].sort().join(', '));
console.log('--- 删除清单', removeList.length, '项，合计', (removeSize / 1024).toFixed(0) + 'K ---');
removeList.sort((a, b) => b[1] - a[1]).slice(0, 25).forEach(([n, s]) => console.log(' ', (s / 1024).toFixed(0) + 'K', n));

if (apply) {
  for (const [, , p] of removeList) fs.rmSync(p, { recursive: true, force: true });
  console.log('APPLIED: 已删除', removeList.length, '项');
} else {
  console.log('DRY-RUN：加 --apply 执行删除');
}
