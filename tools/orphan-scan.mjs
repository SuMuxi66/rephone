// 主包孤儿文件扫描 V2：引用收集覆盖全仓（含分包 packages/），组件引用标记四件套，
// 报告仅主包代码文件并尊重 project.config.json packOptions.ignore
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const SKIP = new Set(['node_modules', 'miniprogram_npm', 'server', 'admin', 'docs', 'tools',
  '.zcode', '.zcode-tmp', '.git', 'dist', '.playwright-mcp', '.mimosa', '.idea', '.vscode', 'mock']);
const CODE_EXT = new Set(['.js', '.json', '.wxml', '.wxss']);
const SIBLINGS = ['.js', '.json', '.wxml', '.wxss'];

const files = [];
(function walk(dir, out) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (SKIP.has(e.name)) continue;
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, out);
    else out.push(p);
  }
})(ROOT, files);

const referenced = new Set();
const mark = (abs) => referenced.add(path.resolve(abs));
const markBundle = (idxJs) => {
  const ext = path.extname(idxJs);
  const base = idxJs.slice(0, idxJs.length - ext.length);
  for (const s of SIBLINGS) mark(base + s);
};
function resolveRef(fromDir, ref) {
  if (ref.startsWith('/')) return path.join(ROOT, ref);
  return path.resolve(fromDir, ref);
}

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

for (const f of files) {
  const dir = path.dirname(f);
  if (f.endsWith('app.json')) {
    const j = JSON.parse(fs.readFileSync(f, 'utf8'));
    for (const pg of j.pages || []) markBundle(path.join(ROOT, pg + '.js'));
    for (const sp of j.subPackages || j.subpackages || []) {
      for (const pg of sp.pages || []) markBundle(path.join(ROOT, sp.root, pg + '.js'));
    }
    for (const tb of (j.tabBar && j.tabBar.list) || []) markBundle(path.join(ROOT, tb.pagePath + '.js'));
    for (const ref of Object.values(j.usingComponents || {})) markBundle(resolveRef(dir, ref) + '.js');
    continue;
  }
  if (path.extname(f) === '.json') {
    const j = (() => { try { return JSON.parse(fs.readFileSync(f, 'utf8')); } catch (e) { return null; } })();
    if (j && j.usingComponents) for (const ref of Object.values(j.usingComponents)) markBundle(resolveRef(dir, ref) + '.js');
  } else if (path.extname(f) === '.js') {
    const s = fs.readFileSync(f, 'utf8');
    for (const m of s.matchAll(/require\(\s*["'](\.[^"']+)["']\s*\)/g)) mark(resolveRef(dir, m[1]) + '.js');
    for (const m of s.matchAll(/import\s+[^"']*["'](\.[^"']+)["']/g)) mark(resolveRef(dir, m[1]) + '.js');
  } else if (path.extname(f) === '.wxss') {
    const s = fs.readFileSync(f, 'utf8');
    for (const m of s.matchAll(/@import\s+["']([^"']+)["']/g)) mark(resolveRef(dir, m[1]));
  } else if (path.extname(f) === '.wxml') {
    const s = fs.readFileSync(f, 'utf8');
    for (const m of s.matchAll(/<(?:include|import)\s+src\s*=\s*["']([^"']+)["']/g)) mark(resolveRef(dir, m[1]));
  }
}

const entries = new Set(['app.js', 'app.json', 'app.wxss', 'sitemap.json', 'project.config.json',
  'project.private.config.json', 'package.json', 'package-lock.json']);
const orphans = files.filter((f) => {
  const rel = path.relative(ROOT, f);
  if (rel.startsWith('packages' + path.sep)) return false;      // 只报告主包
  if (rel.startsWith('custom-tab-bar' + path.sep)) return false; // 框架约定
  if (entries.has(rel) || !CODE_EXT.has(path.extname(f))) return false;
  if (ignored(f)) return false;
  return !referenced.has(path.resolve(f));
});
console.log('主包孤儿代码文件', orphans.length, '个：');
orphans.forEach((f) => console.log('  ', path.relative(ROOT, f)));
