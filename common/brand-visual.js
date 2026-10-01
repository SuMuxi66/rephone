/**
 * 机型库占位视觉。
 *
 * 机型库有 1400+ 个机型、几乎没有真图。原来的占位是一模一样的灰色手机轮廓，
 * 1458 个格子长得完全一样，等于没有视觉信息。这里改成「品牌识别色 + 机型短名」：
 * 用户靠颜色 + 短名就能快速扫到目标，不依赖任何图片。
 *
 * 说明：
 * - 这些是**便于区分的识别色**，不是厂商官方品牌色，不涉及商标图形使用；
 * - 未收录的品牌按名字哈希取一个稳定色，新增品牌也不会退化成同一个颜色；
 * - 仅用于占位块着色，不影响 --brand 等设计令牌。
 */

const BRAND_COLORS = {
  Apple: '#1D1D1F',
  华为: '#C8102E',
  小米: '#F26A1B',
  OPPO: '#1B9E77',
  vivo: '#3B6EF6',
  荣耀: '#2F6BFF',
  三星: '#1428A0',
  Realme: '#D98A00',
  iQOO: '#E8890C',
  Redmi: '#E8452B',
  努比亚: '#D0021B',
  红魔: '#B00020',
};

/** 未收录品牌的兜底色板，按名字哈希稳定选取 */
const FALLBACK_COLORS = [
  '#0E5A47', '#2A6FD6', '#B86A0E', '#7A4FBF',
  '#C0392B', '#0F7B8A', '#8A6D3B', '#A8577A',
];

/** 机型名里可以安全去掉的产品线前缀（去掉后剩下的部分才最有辨识度） */
const LINE_PREFIXES = [
  'iphone', 'galaxy', 'huawei', 'honor', 'redmi', 'xiaomi', 'nubia',
  'realme', 'oppo', 'vivo', 'iqoo', 'samsung', 'oneplus', 'apple',
];

function hashOf(text) {
  let h = 0;
  for (let i = 0; i < text.length; i += 1) {
    h = (h * 31 + text.charCodeAt(i)) | 0;
  }
  return Math.abs(h);
}

/** 品牌识别色 */
export function brandColorOf(brandName) {
  const name = String(brandName || '').trim();
  if (BRAND_COLORS[name]) return BRAND_COLORS[name];
  if (!name) return FALLBACK_COLORS[0];
  return FALLBACK_COLORS[hashOf(name) % FALLBACK_COLORS.length];
}

/** 把识别色调成浅底（向白色混 90%），用于占位块背景 */
export function softTint(hex, ratio = 0.9) {
  const value = parseInt(String(hex).replace('#', ''), 16);
  if (Number.isNaN(value)) return '#F2F2F3';
  const mix = (c) => Math.round(c + (255 - c) * ratio);
  const out = [(value >> 16) & 255, (value >> 8) & 255, value & 255].map((c) => {
    const v = mix(c).toString(16);
    return v.length === 1 ? `0${v}` : v;
  });
  return `#${out.join('')}`;
}

/**
 * 机型短名：去掉品牌名与产品线前缀，保留最有辨识度的部分。
 * "iPhone 15 Pro Max" -> "15 Pro Max"；"HUAWEI Mate 60 Pro" -> "Mate 60 Pro"。
 * 去掉后若为空则回退原名，绝不返回空串。
 */
export function shortModelName(modelName, brandName) {
  const original = String(modelName || '').trim();
  if (!original) return '';
  let out = original;
  const brand = String(brandName || '').trim();
  if (brand && out.toLowerCase().startsWith(brand.toLowerCase())) {
    out = out.slice(brand.length).trim();
  }
  const lower = out.toLowerCase();
  for (const prefix of LINE_PREFIXES) {
    if (lower.startsWith(prefix)) {
      const rest = out.slice(prefix.length).trim();
      if (rest) {
        out = rest;
      }
      break;
    }
  }
  return out || original;
}
