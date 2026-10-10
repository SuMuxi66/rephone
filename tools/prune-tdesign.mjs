// TDesign 闭包裁剪 V4（微信开发者工具「构建 npm」之后必须重跑：npm run prune-npm）
//
// 原理：以「项目侧全部引用」解析到 miniprogram_npm 的目标为根，在包内做文件级可达性遍历。
//   项目侧引用来源：JS 的 import/export-from/require、json 的 usingComponents、
//   wxss 的 @import、wxml 的 include/import/wxs src。
//
// V3 → V4 修复（v1.0.1 发布阻塞项）：
//   V3 的种子只来自 json 的 usingComponents，应用 JS 用裸包名导入的模块
//   （如 import Toast from "tdesign-miniprogram/toast/index"）不在种子里 ——
//   于是即使 5 个页面都在用，toast/index.js 仍被判为不可达并删除，运行时报
//   "module ... toast/index.js is not defined"，分包页面整页白屏。
//   V3 的自检只校验「已判可达文件的出边」，看不到种子遗漏，因此输出虚假 PASS。
//   V4：种子 = 项目侧全部引用；自检 = 项目侧全部引用在删除后仍可解析（种子遗漏即 FAIL）。
//
// 分包 npm 解析规则（实测）：微信小程序对分包的裸包名解析优先分包内
//   <subpackageRoot>/miniprogram_npm/<spec>，不存在时回退主包根的 miniprogram_npm/<spec>。
//   本项目 packages/retail-template 下没有 miniprogram_npm，因此分包页面的
//   tdesign-miniprogram/* 实际都落到主包根 —— 本脚本按同一规则解析，与运行时一致。
//
// 不可达文件全部删除（含未用组件整目录、common 内联后遗留的样式源文件、type.js、
// index.js 转出口、.d.ts 等）；附带清理 .wechatide.ib.json 与全部 *.map。
//
// 用法：node tools/prune-tdesign.mjs            # dry-run，只打印删除清单与校验
//       node tools/prune-tdesign.mjs --apply    # 校验通过才执行删除
import fs from 'node:fs';
import path from 'node:path';
import { builtinModules } from 'node:module';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const NPM = path.join(ROOT, 'miniprogram_npm');
const TD = path.join(NPM, 'tdesign-miniprogram');
const APPLY = process.argv.includes('--apply');

const SKIP = new Set(['node_modules', 'miniprogram_npm', 'server', 'admin', 'docs', 'tools',
  'plugins', '.zcode', '.zcode-tmp', '.git', 'dist', '.playwright-mcp']);

function walk(dir, cb) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    if (SKIP.has(e.name)) continue;
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, cb);
    else cb(p);
  }
}
function readJson(p) {
  try { return JSON.parse(fs.readFileSync(p, 'utf8')); } catch (e) { return null; }
}
function isFile(p) {
  try { return fs.statSync(p).isFile(); } catch (e) { return false; }
}
function rel(base, p) { return path.relative(base, p).split(path.sep).join("/"); }

// ---------- 0. 分包根（用于 npm 解析路径判定） ----------
const APP_JSON = readJson(path.join(ROOT, 'app.json')) || {};
const SUBPACKAGE_ROOTS = (((APP_JSON.subpackages || APP_JSON.subPackages) || [])
  .map((s) => (s && s.root ? path.join(ROOT, s.root) : null))
  .filter(Boolean));

// ---------- 1. 项目侧引用收集（全部文件类型，不只 usingComponents） ----------
const JS_EDGES = [
  /import\s*[^"\']*?from\s*["\']([^"\']+)["\']/g,
  /import\s*["\']([^"\']+)["\']/g,
  /require\(\s*["\']([^"\']+)["\']\s*\)/g,
  /export\s*[^"\']*?from\s*["\']([^"\']+)["\']/g,
];
const WXSS_EDGE = /@import\s+["\']([^"\']+)["\']/g;
const WXML_EDGE = /<(?:include|import|wxs)\b[^>]*?src\s*=\s*["\']([^"\']+)["\']/g;

const PROJECT_REFS = [];
const refSeen = new Set();
function addRef(file, spec, kind) {
  if (typeof spec !== "string" || !spec) return;
  const key = file + "\u0000" + spec + "\u0000" + kind;
  if (refSeen.has(key)) return;
  refSeen.add(key);
  PROJECT_REFS.push({ file, spec, kind });
}

function collectRefs(p) {
  let s = "";
  try { s = fs.readFileSync(p, "utf8"); } catch (e) { return; }
  if (p.endsWith(".js")) {
    for (const re of JS_EDGES) for (const m of s.matchAll(re)) addRef(p, m[1], "js");
  } else if (p.endsWith(".json")) {
    const j = readJson(p);
    if (j && j.usingComponents) for (const v of Object.values(j.usingComponents)) addRef(p, String(v), "component");
  } else if (p.endsWith(".wxss")) {
    for (const m of s.matchAll(WXSS_EDGE)) addRef(p, m[1], "wxss");
  } else if (p.endsWith(".wxml")) {
    for (const m of s.matchAll(WXML_EDGE)) addRef(p, m[1], "wxml");
  }
}
walk(ROOT, collectRefs);
console.log("项目侧引用收集:", PROJECT_REFS.length, "条");

// ---------- 2. 引用解析 ----------
function extCandidates(base) {
  const out = [base];
  for (const e of [".js", ".json", ".wxml", ".wxss"]) out.push(base + e);
  for (const e of ["js", "json", "wxml", "wxss"]) out.push(path.join(base, "index." + e));
  return out;
}
// 项目侧裸包名：先分包内 miniprogram_npm，再回退主包根 miniprogram_npm（与运行时一致）
function resolveProjectRef(fromFile, spec, kind) {
  // WXSS/WXML 的路径可以是不带 "./" 的相对路径（如 app.wxss 里 @import "style/theme.wxss"），
  // 不能一律当裸包名处理 —— 先按相对路径解析，命中即用。
  if (kind === "wxss" || kind === "wxml") {
    const relHits = extCandidates(path.resolve(path.dirname(fromFile), spec)).filter(isFile);
    if (relHits.length) return relHits;
    const absHits = extCandidates(path.join(ROOT, spec)).filter(isFile);
    if (absHits.length) return absHits;
  }
  if (spec.startsWith(".")) return extCandidates(path.resolve(path.dirname(fromFile), spec)).filter(isFile);
  if (spec.startsWith("/")) return extCandidates(path.join(ROOT, spec)).filter(isFile);
  const out = [];
  for (const sp of SUBPACKAGE_ROOTS) {
    if (fromFile.startsWith(sp + path.sep)) out.push(...extCandidates(path.join(sp, "miniprogram_npm", spec)));
  }
  out.push(...extCandidates(path.join(NPM, spec)));
  return out.filter(isFile);
}
// 包内文件：裸包名优先命中 tdesign 自带的嵌套 npm，再回退主包根
function resolveInPackage(fromFile, spec) {
  if (spec.startsWith(".")) return extCandidates(path.resolve(path.dirname(fromFile), spec)).filter(isFile);
  const nested = extCandidates(path.join(TD, "miniprogram_npm", spec)).filter(isFile);
  if (nested.length) return nested;
  return extCandidates(path.join(NPM, spec)).filter(isFile);
}
function refsInFile(p) {
  const out = [];
  let s = "";
  try { s = fs.readFileSync(p, "utf8"); } catch (e) { return out; }
  if (p.endsWith(".js")) {
    for (const re of JS_EDGES) for (const m of s.matchAll(re)) out.push(m[1]);
  } else if (p.endsWith(".json")) {
    const j = readJson(p);
    if (j && j.usingComponents) out.push(...Object.values(j.usingComponents).map(String));
  } else if (p.endsWith(".wxss")) {
    for (const m of s.matchAll(WXSS_EDGE)) out.push(m[1]);
  } else if (p.endsWith(".wxml")) {
    for (const m of s.matchAll(WXML_EDGE)) out.push(m[1]);
  }
  return out;
}

function kindOf(p) {
  if (p.endsWith(".js")) return "js";
  if (p.endsWith(".json")) return "component";
  if (p.endsWith(".wxss")) return "wxss";
  if (p.endsWith(".wxml")) return "wxml";
  return null;
}
function allRoutes() {
  const out = [...(APP_JSON.pages || [])];
  for (const sp of ((APP_JSON.subpackages || APP_JSON.subPackages) || [])) {
    for (const pg of (sp.pages || [])) out.push(sp.root + "/" + pg);
  }
  return out;
}
// 应用模块图：从 app.js / app.wxss / 全部注册页面 / custom-tab-bar 出发，
// 只沿「项目内部」引用（相对、根绝对、wxss/wxml 的无前缀路径）遍历。
// 用途：把「小程序运行时真的会执行的代码」与 Node 工程脚本（如 config/eslintCheck.js）区分开，
// 后者 import 的 eslint/chalk 不该被当成小程序依赖。
function buildAppGraph() {
  const seen = new Set();
  const q = [];
  const push = (p) => { if (isFile(p) && !seen.has(p)) { seen.add(p); q.push(p); } };
  push(path.join(ROOT, "app.js"));
  push(path.join(ROOT, "app.wxss"));
  for (const rt of allRoutes()) for (const e of ["js", "json", "wxml", "wxss"]) push(path.join(ROOT, rt + "." + e));
  for (const e of ["js", "json", "wxml", "wxss"]) push(path.join(ROOT, "custom-tab-bar", "index." + e));
  while (q.length) {
    const f = q.pop();
    const kind = kindOf(f);
    for (const spec of refsInFile(f)) {
      if (!spec.startsWith(".") && !spec.startsWith("/") && kind !== "wxss" && kind !== "wxml") continue;
      for (const t of resolveProjectRef(f, spec, kind)) {
        if (!t.startsWith(NPM + path.sep)) push(t);
      }
    }
  }
  return seen;
}
const APP_GRAPH = buildAppGraph();
console.log("应用模块图文件数:", APP_GRAPH.size);

// ---------- 3. 种子 = 项目侧全部引用命中的包内文件 ----------
const reachable = new Set();
const queue = [];
function addReach(f) { if (!reachable.has(f)) { reachable.add(f); queue.push(f); } }
for (const { file, spec, kind } of PROJECT_REFS) {
  for (const t of resolveProjectRef(file, spec, kind)) {
    if (t.startsWith(NPM + path.sep)) addReach(t);
  }
}
console.log("项目侧引用命中包内入口:", reachable.size, "个文件（V3 仅统计四件套，且只来自 usingComponents）");

// ---------- 4. 包内文件级可达性 ----------
while (queue.length) {
  const f = queue.pop();
  for (const spec of refsInFile(f)) {
    for (const t of resolveInPackage(f, spec)) addReach(t);
  }
}

console.log("包内可达文件数（种子 + 包内遍历）:", reachable.size);

function npmStats() {
  let n = 0, size = 0;
  (function w(d) {
    for (const e of fs.readdirSync(d, { withFileTypes: true })) {
      const q = path.join(d, e.name);
      if (e.isDirectory()) w(q); else { n++; size += fs.statSync(q).size; }
    }
  })(NPM);
  return { n, size };
}

// ---------- 5. 删除清单 ----------
const tdFiles = [];
(function w(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const q = path.join(d, e.name);
    if (e.isDirectory()) w(q); else tdFiles.push(q);
  }
})(TD);

const removeList = [];
for (const q of tdFiles) if (!reachable.has(q)) removeList.push([rel(NPM, q), fs.statSync(q).size, q]);
// *.map 一律清理（含 tdesign 自带的嵌套 npm），与可达性无关
for (const q of tdFiles) {
  if (q.endsWith(".map") && !reachable.has(q)) removeList.push([rel(NPM, q), fs.statSync(q).size, q]);
}
const ib = path.join(TD, ".wechatide.ib.json");
if (fs.existsSync(ib) && !reachable.has(ib)) removeList.push(["tdesign-miniprogram/.wechatide.ib.json", fs.statSync(ib).size, ib]);

const seen = new Set();
const removeList2 = removeList.filter(([, , q]) => (seen.has(q) ? false : (seen.add(q), true)));
const total = removeList2.reduce((a, [, s]) => a + s, 0);
const removePaths = removeList2.map(([, , q]) => q);
const willBeRemoved = (abs) => removePaths.some((q) => abs === q || abs.startsWith(q + path.sep));
console.log("--- 删除清单", removeList2.length, "项，合计", (total / 1024).toFixed(0) + "K ---");
removeList2.sort((a, b) => b[1] - a[1]).slice(0, 12).forEach(([n, s]) => console.log(" ", (s / 1024).toFixed(0) + "K", n));

// ---------- 6. 校验 ----------
// A. 项目侧全部引用（V4 关键：种子一旦遗漏，这里必 FAIL）
// B. 包内可达文件的出边
// C. 项目侧无法解析的裸包名（排除 Node 内置与工程依赖，如 config/eslintCheck.js 用的 eslint/chalk）
const PKG = readJson(path.join(ROOT, "package.json")) || {};
const KNOWN_EXTERNAL = new Set([
  ...builtinModules, ...builtinModules.map((m) => "node:" + m),
  ...Object.keys(PKG.dependencies || {}), ...Object.keys(PKG.devDependencies || {}),
]);
function topName(spec) { return spec.startsWith("@") ? spec.split("/").slice(0, 2).join("/") : spec.split("/")[0]; }

function validate() {
  const errs = [];
  const gone = (abs) => !fs.existsSync(abs) || willBeRemoved(abs);
  for (const { file, spec, kind } of PROJECT_REFS) {
    const targets = resolveProjectRef(file, spec, kind);
    const inNpm = targets.filter((t) => t.startsWith(NPM + path.sep));
    if (!inNpm.length) continue;
    if (inNpm.every(gone)) errs.push("项目引用将失效 [" + kind + "] " + rel(ROOT, file) + " -> " + spec);
  }
  for (const f of reachable) {
    if (gone(f)) continue;
    for (const spec of refsInFile(f)) {
      const targets = resolveInPackage(f, spec);
      if (!targets.length) continue;
      if (targets.every((t) => t.startsWith(TD) && gone(t))) errs.push("包内引用将失效 " + rel(NPM, f) + " -> " + spec);
    }
  }
  return errs;
}
function unresolvedBare() {
  const out = new Map();
  for (const { file, spec, kind } of PROJECT_REFS) {
    if (spec.startsWith(".") || spec.startsWith("/")) continue;
    if (resolveProjectRef(file, spec, kind).length) continue;
    if (KNOWN_EXTERNAL.has(topName(spec))) continue;
    if (spec.startsWith("plugin://")) continue;
    // 只对「应用模块图内」的文件报错：小程序运行时真的会执行这些文件。
    // 图外的文件（config/eslintCheck.js 等 Node 工程脚本）import 的 eslint/chalk 与运行时无关。
    if (!APP_GRAPH.has(file)) continue;
    if (!out.has(spec)) out.set(spec, rel(ROOT, file) + " [" + kind + "]");
  }
  return out;
}

const errs = validate();
console.log("--- 校验（删除后视角） ---");
console.log(errs.length ? "FAIL " + errs.length + " 项:" : "PASS（项目侧全部引用 + 包内可达出边在删除后均可解析）");
errs.slice(0, 20).forEach((e) => console.log("  !", e));
const bare = unresolvedBare();
if (bare.size) {
  console.log("--- 应用侧无法解析的裸包名（运行时会报 module is not defined） ---");
  for (const [k, v] of bare) console.log("  !", k, "<-", v);
}

if (!APPLY) {
  console.log("DRY-RUN：加 --apply 执行删除");
  process.exit(errs.length || bare.size ? 1 : 0);
}
if (errs.length || bare.size) {
  console.log("校验未过，拒绝执行删除");
  process.exit(1);
}
const before = npmStats();
for (const [, , q] of removeList2) fs.rmSync(q, { recursive: true, force: true });
const after = npmStats();
console.log("APPLIED: 已删除", removeList2.length, "项，释放", (total / 1024).toFixed(0) + "K");
console.log("包内文件数:", before.n, "->", after.n, "  大小:", (before.size / 1024).toFixed(0) + "K ->", (after.size / 1024).toFixed(0) + "K");
const errs2 = validate();
const bare2 = unresolvedBare();
if (errs2.length || bare2.size) {
  console.log("删除后复检 FAIL");
  errs2.slice(0, 10).forEach((e) => console.log("  !", e));
  for (const [k, v] of bare2) console.log("  !", k, "<-", v);
  process.exit(1);
}
console.log("删除后复检 PASS");
