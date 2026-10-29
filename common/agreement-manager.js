import { post } from '../services/request';
import { AGREEMENT_DOCS, AGREEMENT_SCENES } from './agreement-docs';

/**
 * 协议签署状态管理。
 * - 本地缓存为主存储：key=agreement.signatures.v1，值形如 { "<docKey>@<version>": { time, scene } }；
 *   同一文档按 key+version 记账，协议版本升级后旧签署自动失效、重新弹窗。
 * - 后端留痕接口（表结构与契约）见 docs/superpowers/specs/2026-10-29-agreement-signing.md；
 *   server 未上线前 REMOTE_SYNC_ENABLED=false，本地签署即生效，门禁在前端执行。
 */
const STORAGE_KEY = 'agreement.signatures.v1';
const REMOTE_SYNC_ENABLED = false;

function readStore() {
  return wx.getStorageSync(STORAGE_KEY) || {};
}

export function isSigned(docKey, version) {
  return !!readStore()[docKey + '@' + version];
}

/** 场景下尚未签署的文档（保持注册表顺序）；空数组表示全部已签、门禁直接放行 */
export function getUnsignedDocs(scene) {
  const conf = AGREEMENT_SCENES[scene];
  if (!conf) return [];
  return conf.docs
    .map((key) => AGREEMENT_DOCS[key])
    .filter((doc) => doc && !isSigned(doc.key, doc.version));
}

/** 组件确认时落账：本地立即生效；后端留痕异步补发，失败不回滚本地（避免用户重复签署） */
export function recordSignatures(docs, scene) {
  const map = readStore();
  const now = Date.now();
  docs.forEach((doc) => {
    map[doc.key + '@' + doc.version] = { time: now, scene };
  });
  wx.setStorageSync(STORAGE_KEY, map);
  reportToServer(docs, scene);
}

function reportToServer(docs, scene) {
  if (!REMOTE_SYNC_ENABLED) return;
  post('/api/agreements/signatures', {
    scene,
    items: docs.map((doc) => ({ key: doc.key, version: doc.version })),
  }).catch((e) => {
    console.warn('[agreement] 签署留痕上报失败（本地已生效）:', e && e.message);
  });
}

/**
 * 业务门禁：在提交/继续类动作前 await 调用。
 * - 全部已签：resolve(true)；
 * - 有未签：弹出组件走签署流程，签完 resolve(true)，拒绝/关闭 resolve(false)，由页面阻断业务；
 * - 页面未放置组件：按未同意处理（fail-closed）并 console.error，开发期即可暴露。
 * 注意：本函数内部有 await。若后续紧跟 wx.requestSubscribeMessage 等手势敏感调用
 * （如回收下单），请先用 getUnsignedDocs(scene) 做同步预判，仅在确需弹窗时才进入本函数。
 */
export async function ensureAgreements(page, scene) {
  const modal = page.selectComponent('#agreement-modal');
  if (!modal) {
    console.error('[agreement] 页面未放置 <agreement-modal id="agreement-modal" />，门禁按未同意阻断');
    return false;
  }
  const unsigned = getUnsignedDocs(scene);
  if (!unsigned.length) {
    return true;
  }
  const conf = AGREEMENT_SCENES[scene] || {};
  try {
    await modal.open({ scene, mode: conf.mode || 'read', docs: unsigned, maskClosable: !conf.lock });
    return true;
  } catch (e) {
    return false;
  }
}
