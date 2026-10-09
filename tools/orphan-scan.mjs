// 全项目孤儿代码文件扫描（DevTools「无依赖文件」对齐版）
// 引用图边类型：json usingComponents（npm 包名/相对/根绝对三种风格）、ESM import/export-from、
// require（相对 + 裸包名→miniprogram_npm）、wxss @import、wxml include/import/wxs src。
// 传递性剥离：孤儿文件的引用不算数，循环到不动点。报告尊重 project.config.json packOptions.ignore。
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const NPM = path.join(ROOT, 'miniprogram_npm');
const SKIP = new Set(['node_modules', 'server', 'admin', 'docs', 'tools',
  '.zcode', '.zcode-tmp', '.git', 'dist', '.playwright-mcp', '.mimosa', '.idea', '.vscode', 'mock']);
const CODE_EXT = new Set(['.js', '.json', '.wxml', '.wxss']);
const SIBLINGS = ['.js', '.json', '.wxml', '.wxss'];

const files = [];
(function walk(dir) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (SKIP.has(e.name)) continue;
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else files.push(p);
  }
})(ROOT);

// packOptions.ignore（DevTools 同款豁免；路径统一正斜杠比较）
const pc = JSON.parse(fs.readFileSync(path.join(ROOT, 'project.config.json'), 'utf8'));
const norm = (p) => p.split(path.sep).join('/');
const ignored = (abs) => {
  const rel = norm(path.relative(ROOT, abs));
  return (pc.packOptions && pc.packOptions.ignore || []).some(({ type, value }) => {
    const v = norm(value);
    if (type === 'folder') return rel === v || rel.startsWith(v + '/');
    if (type === 'file') {
      if (v.includes('*')) return new RegExp('^' + v.replace(/\*/g, '.*') + '$').test(path.basename(rel)) || rel === v;
      return rel === v;
    }
    if (type === 'suffix') return rel.endsWith(v);
    return false;
  });
};
const entries = new Set(['app.js', 'app.json', 'app.wxss', 'sitemap.json', 'project.config.json',
  'project.private.config.json', 'package.json', 'package-lock.json']);
const reportable = (f) => {
  const rel = path.relative(ROOT, f);
  if (rel.startsWith('custom-tab-bar' + path.sep)) return false; // 框架约定
  if (entries.has(rel) || !CODE_EXT.has(path.extname(f))) return false;
  return !ignored(f);
};

const EDGES = [
  /import\s*[^"']*?from\s*["']([^"']+)["']/g,
  /import\s*["']([^"']+)["']/g,
  /require\(\s*["']([^"']+)["']\s*\)/g,
  /export\s*[^"']*?from\s*["']([^"']+)["']/g,
  /@import\s+["']([^"']+)["']/g,
  /<(?:include|import|wxs)\b[^>]*?src\s*=\s*["']([^"']+)["']/g,
];
function isFile(p) {
  try { return fs.statSync(p).isFile(); } catch (e) { return false; }
}
function resolveFileSpec(fromFile, spec) {
  const out = [];
  if (spec.startsWith('.')) {
    const base = path.resolve(path.dirname(fromFile), spec);
    out.push(base, base + '.js', base + '.json', base + '.wxml', base + '.wxss', path.join(base, 'index.js'));
  } else {
    // 裸包名：包内嵌套 npm 与根 npm 都算（宁多勿缺）
    for (const b of [path.join(NPM, 'tdesign-miniprogram', 'miniprogram_npm', spec), path.join(NPM, spec)]) {
      out.push(b, b + '.js', path.join(b, 'index.js'));
    }
  }
  return out.filter(isFile);
}
function resolveCompRef(fromDir, ref) {
  if (ref.startsWith('.')) return path.resolve(fromDir, ref);
  if (ref.startsWith('/')) return path.join(ROOT, ref);
  return path.join(NPM, ref);
}
function bundleOf(idxJs, set) {
  const base = idxJs.slice(0, idxJs.length - path.extname(idxJs).length);
  for (const s of SIBLINGS) {
    const f = base + s;
    if (isFile(f)) set.add(path.resolve(f));
  }
}
function outEdges(f) {
  const specs = [];
  let s = '';
  try { s = fs.readFileSync(f, 'utf8'); } catch (e) { return specs; }
  for (const re of EDGES) {
    for (const m of s.matchAll(re)) specs.push(m[1]);
  }
  if (/\.json$/.test(f)) {
    const j = (() => { try { return JSON.parse(s); } catch (e) { return null; } })();
    if (j && j.usingComponents) specs.push(...Object.values(j.usingComponents));
  }
  return specs;
}
function targetsOf(f, spec) {
  if (f.endsWith('.json')) {
    const idxJs = resolveCompRef(path.dirname(f), spec) + '.js';
    return isFile(idxJs) ? [idxJs] : [];
  }
  if (f.endsWith('.wxss') || f.endsWith('.wxml')) {
    // wxss/wxml 的引用永远是文件相对路径（允许省略 ./），没有 npm 概念
    const base = path.resolve(path.dirname(f), spec);
    const ext = f.endsWith('.wxss') ? '.wxss' : '.wxml';
    return [base, base + ext].filter(isFile);
  }
  return resolveFileSpec(f, spec); // js：相对路径 + 裸包名→miniprogram_npm
}

// 传递性剥离
const orphans = new Set();
for (;;) {
  const ref = new Set();
  for (const f of files) {
    if (orphans.has(f)) continue;
    if (path.basename(f) === 'app.json') {
      let j = null;
      try { j = JSON.parse(fs.readFileSync(f, 'utf8')); } catch (e) { continue; }
      for (const pg of j.pages || []) bundleOf(path.join(ROOT, pg + '.js'), ref);
      for (const sp of j.subPackages || j.subpackages || []) {
        for (const pg of sp.pages || []) bundleOf(path.join(ROOT, sp.root, pg + '.js'), ref);
      }
      for (const tb of (j.tabBar && j.tabBar.list) || []) bundleOf(path.join(ROOT, tb.pagePath + '.js'), ref);
      for (const spec of Object.values(j.usingComponents || {})) {
        for (const t of targetsOf(f, spec)) {
          if (path.extname(t) === '.js') bundleOf(t, ref); else ref.add(path.resolve(t));
        }
      }
      continue;
    }
    for (const spec of outEdges(f)) {
      for (const t of targetsOf(f, spec)) {
        if (path.extname(t) === '.js') bundleOf(t, ref); else ref.add(path.resolve(t));
      }
    }
  }
  let grew = false;
  for (const f of files) {
    if (orphans.has(f) || !reportable(f)) continue;
    if (!ref.has(path.resolve(f))) { orphans.add(f); grew = true; }
  }
  if (!grew) break;
}
const list = [...orphans].sort();
const totalK = list.reduce((a, f) => a + fs.statSync(f).size, 0) / 1024;
console.log('全项目孤儿代码文件', list.length, '个，合计', totalK.toFixed(0) + 'K：');
const byTop = {};
for (const f of list) {
  const rel = path.relative(ROOT, f);
  const d = rel.split(path.sep).slice(0, 3).join('/');
  (byTop[d] = byTop[d] || []).push(rel);
}
for (const [d, xs] of Object.entries(byTop).sort((a, b) => b[1].length - a[1].length)) {
  console.log(' ', d, 'x' + xs.length);
  xs.forEach((x) => console.log('    ', x));
}
