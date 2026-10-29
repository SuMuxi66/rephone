/**
 * 协议签署 E2E（官方 miniprogram-automator 入口）
 *
 * 运行前安装 SDK（不入库）：cd tools/e2e && npm i miniprogram-automator
 * 两种连接方式：
 *   A) 自动拉起（Windows 下 Node spawn .bat 可能失败，失败就走 B）：
 *      node agreement-e2e.js
 *   B) 先手动开自动化端口再 connect（已验证可用）：
 *      "E:\weixinkaifagongju\cli.bat" auto --project <项目绝对路径> --auto-port 9420
 *      WX_AUTO_WS=ws://127.0.0.1:9420 node agreement-e2e.js
 * 环境变量：WECHATIDE_CLI（cli.bat 路径）、WX_PROJECT（项目路径，默认仓库根）、
 *           WX_AUTO_PORT（默认 9420）、WX_AUTO_WS（设置则走 connect）
 * 注意：脚本会清空模拟器的协议签署账本后重跑全流程，收尾再清一次。
 */
const automator = require('miniprogram-automator');
const path = require('path');

const CLI = process.env.WECHATIDE_CLI || 'E:\\weixinkaifagongju\\cli.bat';
const PROJECT = process.env.WX_PROJECT || path.resolve(__dirname, '..', '..');
const PORT = Number(process.env.WX_AUTO_PORT || 9420);
const results = [];
const check = (name, ok, detail) => {
  results.push({ name, ok });
  console.log((ok ? 'PASS' : 'FAIL') + ' | ' + name + (detail !== undefined ? ' | ' + JSON.stringify(detail) : ''));
};
const cls = async (el) => String((await el.attribute('class')) || '');

(async () => {
  let mp;
  try {
    if (process.env.WX_AUTO_WS) {
      mp = await automator.connect({ wsEndpoint: process.env.WX_AUTO_WS });
    } else {
      mp = await automator.launch({ cliPath: CLI, projectPath: PROJECT, port: PORT, timeout: 120000 });
    }

    // ===== 准备：清空签署账本 =====
    await mp.callWxMethod('removeStorageSync', 'agreement.signatures.v1');

    // ===== S1 全局弹窗：真实点击全选 + 真实双击确认 =====
    let page = await mp.reLaunch('/pages/home/home');
    await page.waitFor(1200);
    let comp = await page.$('#agreement-modal');
    check('S1 弹窗渲染', !!(await comp.$('.agree-mask')));
    await mp.screenshot({ path: path.join(__dirname, 's1-global.png') });

    await (await comp.$('.agree-consent')).tap();
    await page.waitFor(200);
    let btn = await comp.$('.agree-foot .is-primary');
    check('S1 全选后确认键解锁', !(await cls(btn)).includes('is-disabled'), { btnCls: await cls(btn) });

    await btn.tap();
    await btn.tap();
    await page.waitFor(400);
    check('S1 连点只关一次', !(await comp.$('.agree-mask')));
    const ledger = (await mp.callWxMethod('getStorageSync', 'agreement.signatures.v1')) || {};
    check('S1 账本恰好 3 条', Object.keys(ledger).length === 3, Object.keys(ledger));

    // ===== S1b 防重复弹窗 =====
    page = await mp.reLaunch('/pages/home/home');
    await page.waitFor(1200);
    comp = await page.$('#agreement-modal');
    check('S1b 重进首页不再弹', !(await comp.$('.agree-mask')));

    // ===== S2 估价：拒绝阻断 + 签署放行 =====
    page = await mp.switchTab('/pages/recycle/estimate/index');
    await page.waitFor(600);
    await page.setData({
      form: { brandId: 1, brandName: 'Apple', modelId: 9, modelName: 'iPhone 15 Pro', storage: '256GB', condition: 'COND_99', screen: 'SCR_OK', issues: [] },
      stepCurrent: 2,
    });
    await page.callMethod('onSubmit');
    await page.waitFor(500);
    comp = await page.$('#agreement-modal');
    let title = await (await comp.$('.agree-title')).text();
    check('S2 弹《回收估价用户须知》', title === '回收估价用户须知', { title });

    await (await comp.$('.is-ghost')).tap();
    await page.waitFor(400);
    let pageData = await page.data();
    let l2 = (await mp.callWxMethod('getStorageSync', 'agreement.signatures.v1')) || {};
    check('S2 拒绝后阻断（未发请求未记账）', !(await comp.$('.agree-mask')) && pageData.submitting === false && !l2['estimate-notice@V1.0']);

    await page.callMethod('onSubmit');
    await page.waitFor(500);
    await (await comp.$('.agree-consent')).tap();
    await page.waitFor(200);
    btn = await comp.$('.agree-foot .is-primary');
    check('S2 估价场景无需滑底即可确认', !(await cls(btn)).includes('is-disabled'));
    await btn.tap();
    await page.waitFor(900);
    const l3 = (await mp.callWxMethod('getStorageSync', 'agreement.signatures.v1')) || {};
    check('S2 签署放行且入账', !!l3['estimate-notice@V1.0'], { route: (await mp.currentPage()).path });

    // ===== S3 回收三连签：requireRead 门禁 + 滑到底 =====
    await mp.callWxMethod('setStorageSync', 'recycle.quoteResult', { modelId: 9, storage: '256GB', condition: 'COND_99', screen: 'SCR_OK', issueKeys: [], priceFen: 199900 });
    page = await mp.navigateTo('/pages/recycle/create/index');
    await page.waitFor(900);
    await page.setData({ form: { pickupType: 10, name: '走查测试', phone: '13800000000', region: '福建省 福州市 鼓楼区', detail: '测试详细地址', remark: '' } });
    await page.callMethod('onSubmit');
    await page.waitFor(600);
    comp = await page.$('#agreement-modal');
    title = await (await comp.$('.agree-title')).text();
    btn = await comp.$('.agree-foot .is-primary');
    check('S3 回收队列首篇=维修知情用户须知', title === '维修知情用户须知', { title });
    check('S3 requireRead 初始确认键置灰', (await cls(btn)).includes('is-disabled'));

    await (await comp.$('.agree-consent')).tap();
    await page.waitFor(200);
    btn = await comp.$('.agree-foot .is-primary');
    check('S3 勾选但未滑底仍置灰', (await cls(btn)).includes('is-disabled'));

    // 滑到底：优先真实 touch 手势驱动 scroll-view，失败退回 trigger(scrolltolower)
    const scrollEl = await comp.$('.doc-scroll');
    const size = await scrollEl.size();
    const touch = (y) => ({ identifier: 0, pageX: size.width / 2, clientX: size.width / 2, pageY: Math.round(y), clientY: Math.round(y) });
    const startY = Math.min(size.height - 20, 480);
    await scrollEl.touchstart({ touches: [touch(startY)], changedTouches: [touch(startY)] });
    for (let y = startY; y > 10; y -= 90) {
      await scrollEl.touchmove({ touches: [touch(y)], changedTouches: [touch(y)] });
    }
    await scrollEl.touchend({ touches: [], changedTouches: [touch(10)] });
    await page.waitFor(600);
    btn = await comp.$('.agree-foot .is-primary');
    let unlocked = !(await cls(btn)).includes('is-disabled');
    let unlockVia = 'touch 手势';
    if (!unlocked) {
      await scrollEl.trigger('scrolltolower');
      await page.waitFor(300);
      btn = await comp.$('.agree-foot .is-primary');
      unlocked = !(await cls(btn)).includes('is-disabled');
      unlockVia = 'trigger(scrolltolower) 兜底';
    }
    check('S3 滑到底后解锁（' + unlockVia + '）', unlocked);
    await btn.tap();
    await page.waitFor(400);

    // 第二篇：状态复位 + scrollTop 回顶（视觉截图）
    title = await (await comp.$('.agree-title')).text();
    const progress = await (await comp.$('.agree-progress')).text();
    const consentCls = await cls(await comp.$('.agree-consent .agree-check'));
    await mp.screenshot({ path: path.join(__dirname, 's3-doc2-top.png') });
    check('S3 翻篇到设备来源承诺书', title === '设备来源合法承诺书', { title, progress });
    check('S3 勾选态与已读态复位', !consentCls.includes('is-on'), { consentCls });

    // 第二篇走完，第三篇拒绝收尾（避免真实下单）
    await (await comp.$('.doc-scroll')).trigger('scrolltolower');
    await page.waitFor(200);
    await (await comp.$('.agree-consent')).tap();
    await page.waitFor(200);
    await (await comp.$('.agree-foot .is-primary')).tap();
    await page.waitFor(400);
    title = await (await comp.$('.agree-title')).text();
    check('S3 翻到第三篇用户承诺书', title === '用户承诺书', { title });
    await (await comp.$('.is-ghost')).tap();
    await page.waitFor(400);
    const l4 = (await mp.callWxMethod('getStorageSync', 'agreement.signatures.v1')) || {};
    pageData = await page.data();
    check('S3 第三篇拒绝：队列关闭、未下单、未记第三篇', !(await comp.$('.agree-mask')) && pageData.submitting === false && !l4['user-commitment@V1.0'] && !!l4['repair-notice@V1.0'] && !!l4['device-source@V1.0'], Object.keys(l4));

    // ===== 收尾：清理测试数据 =====
    await mp.callWxMethod('removeStorageSync', 'recycle.quoteResult');
    await mp.callWxMethod('removeStorageSync', 'agreement.signatures.v1');
  } catch (e) {
    check('脚本异常', false, { message: e && e.message });
  } finally {
    const pass = results.filter((r) => r.ok).length;
    console.log('==== SUMMARY: ' + pass + '/' + results.length + ' PASS ====');
    if (mp) await mp.disconnect();
    process.exit(pass === results.length ? 0 : 1);
  }
})();
