// TDesign npm 安全清理（微信开发者工具「构建 npm」之后可选运行）
// 用法：npm run prune-npm   （默认 dry-run，加 --apply 才真删）
//
// ⚠️ 只做三件确认安全的事：
//   1. 删 .wechatide.ib.json（IDE 插件生成物，84KB）
//   2. 删全部 *.js.map（sourcemap，运行时不需要）
//   3. 删嵌套 npm 中未被任何组件引用的包（dayjs/tinycolor2 等，theme 汇总链不引用）
//
// ⚠️ 不要删 tdesign-miniprogram 下的组件目录！common/style/theme/_components.wxss 与
//    _index.wxss 汇总 @import 了全部组件的 wxss（主题变量链），删任何组件都会导致
//    编译期 ENOENT、页面白屏（V1.2.1 实测）。「无依赖文件」由微信上传时自动剔除，不影响线上包。
import fs from 'node:fs';
import path from 'node:path';

const npmRoot = path.join(path.resolve(path.dirname(new URL(import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1')), '..'), 'miniprogram_npm');
const tdDir = path.join(npmRoot, 'tdesign-miniprogram');
const apply = process.argv.includes('--apply');

if (!fs.existsSync(tdDir)) {
  console.log('未找到', tdDir, '——请先在开发者工具执行「构建 npm」');
  process.exit(0);
}

const removeList = [];
let removeSize = 0;

// 1. IDE 插件生成物
const ib = path.join(tdDir, '.wechatide.ib.json');
if (fs.existsSync(ib)) {
  removeList.push(['tdesign-miniprogram/.wechatide.ib.json', fs.statSync(ib).size, ib]);
  removeSize += fs.statSync(ib).size;
}

// 2. 全部 sourcemap
(function findMap(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) findMap(p);
    else if (e.name.endsWith('.map')) {
      removeList.push([path.relative(npmRoot, p), fs.statSync(p).size, p]);
      removeSize += fs.statSync(p).size;
    }
  }
})(npmRoot);

// 3. 嵌套 npm：扫描 tdesign 组件的 js/wxml 实际 import 的包，未引用的删除（tslib 必被引用）
const usedNpm = new Set(['tslib']);
(function scan(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) scan(p);
    else if (/\.(js|wxml)$/.test(e.name)) {
      const s = fs.readFileSync(p, 'utf8');
      for (const m of s.matchAll(/miniprogram_npm\/([a-z0-9._-]+)/g)) usedNpm.add(m[1]);
    }
  }
})(tdDir);
const nested = path.join(tdDir, 'miniprogram_npm');
if (fs.existsSync(nested)) {
  for (const e of fs.readdirSync(nested, { withFileTypes: true })) {
    if (!e.isDirectory() || usedNpm.has(e.name)) continue;
    const p = path.join(nested, e.name);
    const size = (function sz(d) { let n = 0; (function w(x) { for (const f of fs.readdirSync(x, { withFileTypes: true })) { const fp = path.join(x, f.name); f.isDirectory() ? w(fp) : (n += fs.statSync(fp).size); } })(d); return n; })(p);
    removeList.push([`tdesign-miniprogram/miniprogram_npm/${e.name}/`, size, p]);
    removeSize += size;
  }
}

console.log('嵌套 npm 实际引用:', [...usedNpm].sort().join(', '));
console.log('--- 删除清单', removeList.length, '项，合计', (removeSize / 1024).toFixed(0) + 'K ---');
removeList.forEach(([n, s]) => console.log(' ', (s / 1024).toFixed(0) + 'K', n));

if (apply) {
  for (const [, , p] of removeList) fs.rmSync(p, { recursive: true, force: true });
  console.log('APPLIED: 已删除', removeList.length, '项');
} else {
  console.log('DRY-RUN：加 --apply 执行删除');
}
