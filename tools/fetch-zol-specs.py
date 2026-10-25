#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""中关村在线(ZOL)机型参数爬虫。

链路: search.zol.com.cn 搜索(标题归一化精确匹配) -> detail.zol.com.cn 详情页取参数页路径
      -> /<catalog>/<id>/param.shtml 解析 newPmName/newPmVal 参数表。
输出: .tmp/model-specs/out/zol-<品牌>.json(与 tools/ingest-model-specs.py 对接)。
待收集清单: 从 data-model-models.sql + data-model-specs.sql(已填集合) 推导,排除非手机条目。
限速: 每请求 1.0-1.6s 随机;断点续跑(JSONL 状态文件 .tmp/model-specs/zol_progress.jsonl)。

用法:
    python tools/fetch-zol-specs.py --limit 12          # 小样
    python tools/fetch-zol-specs.py --brand 华为         # 单品牌
    python tools/fetch-zol-specs.py                     # 全量
"""
import argparse, glob, io, json, os, random, re, sys, time, urllib.parse, urllib.request

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SEED = os.path.join(ROOT, 'server/start/src/main/resources/db/data-model-models.sql')
DEMO = os.path.join(ROOT, 'server/start/src/main/resources/db/schema-quote.sql')
SPEC = os.path.join(ROOT, 'server/start/src/main/resources/db/data-model-specs.sql')
OUTDIR = os.path.join(ROOT, '.tmp/model-specs/out')
PROGRESS = os.path.join(ROOT, '.tmp/model-specs/zol_progress.jsonl')
UA = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36',
      'Accept-Language': 'zh-CN,zh;q=0.9'}
NONPHONE = re.compile(r'笔记本|MagicBook|平板|MagicPad|Waterplay|豆包手机助手')

PREFIX = ['真我','realme','Realme','REALME','OnePlus','oneplus','一加','努比亚','nubia','Nubia','腾讯红魔游戏手机','红魔','红米','Redmi','REDMI','redmi','小米','Xiaomi','xiaomi','荣耀','HONOR','华为','HUAWEI','OPPO','vivo','iQOO']
BRAND_DISPLAY = {
    '华为': ['华为'], '小米': ['小米'], 'Redmi': ['Redmi', '红米'], '红米': ['Redmi', '红米'],
    'OPPO': ['OPPO'], 'realme': ['真我', 'realme'], '一加': ['一加'], 'vivo': ['vivo'],
    'iQOO': ['iQOO'], '荣耀': ['荣耀', 'HONOR'], '红魔': ['红魔', '努比亚红魔'],
    '努比亚': ['努比亚', 'nubia'], 'Apple': ['苹果', 'iPhone'], '三星': ['三星'],
}
FIELDS = ['release_year', 'release_month', 'chip', 'ram', 'storage', 'battery_mah',
          'charge_w', 'wireless_w', 'screen_size', 'resolution', 'refresh_rate', 'rear_camera']


def norm(name):
    s = str(name).strip().replace('（', '(').replace('）', ')')
    s = re.sub(r'\s+', '', s)
    for p in PREFIX:
        if s.lower().startswith(p.lower()) and len(s) > len(p):
            s = s[len(p):]
            break
    s = re.sub(r'\(`[a-z0-9_]+`\)', '', s, flags=re.I)
    s = re.sub(r'\([a-z0-9_]{3,}\)$', '', s, flags=re.I)
    s = re.sub(r'5g$', '', s, flags=re.I)
    return s.lower().replace('-', '').replace('_', '').replace('+', 'plus')


def title_norm(title):
    s = re.sub(r'[【\[].*?[】\]]', '', title)
    s = re.sub(r'[（(][^（）()]*?(?:GB|TB|内存|版|英寸|英寸以上)[^（）()]*?[）)]$', '', s)
    return norm(s)


def pending_list():
    row_variants = {}
    for path in (DEMO, SEED):
        for line in io.open(path, encoding='utf-8'):
            m = re.search(r"SELECT 0, b\.id, '([^']+)',.*?WHERE b\.`name`='([^']+)'", line)
            if m:
                row_variants.setdefault((m.group(2), m.group(1)), None)
    demo_brands = {1: 'Apple', 2: '华为', 3: '小米', 4: 'OPPO', 5: 'vivo'}
    for line in io.open(DEMO, encoding='utf-8'):
        m = re.match(r"\((\d+), 0, (\d), '([^']+)',", line.strip())
        if m:
            row_variants.setdefault((demo_brands[int(m.group(2))], m.group(3)), None)
    filled = set()
    for m in re.finditer(r"WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='([^']+)'\s*\n\s*AND `brand_id`=\(SELECT `id` FROM `brand` WHERE `name`='([^']+)'",
                         io.open(SPEC, encoding='utf-8').read()):
        filled.add((m.group(2), m.group(1)))
    return [(b, n) for (b, n) in row_variants if not NONPHONE.search(n) and (b, n) not in filled]


def fetch(url, timeout=25):
    req = urllib.request.Request(url, headers=UA)
    raw = urllib.request.urlopen(req, timeout=timeout).read()
    try:
        return raw.decode('gbk')
    except UnicodeDecodeError:
        return raw.decode('utf-8', 'replace')


def sleep():
    time.sleep(random.uniform(1.0, 1.6))


def search_ids(keyword):
    url = 'https://search.zol.com.cn/s/all.php?keyword=' + urllib.parse.quote(keyword)
    html = fetch(url)
    pairs = []
    for m in re.finditer(r'<a[^>]*href="[^"]*cell_phone/index(\d+)\.shtml"[^>]*>(.*?)</a>', html, re.S):
        title = re.sub(r'<[^>]+>', '', m.group(2)).strip()
        if title:
            pairs.append((m.group(1), title))
    return pairs


def match_id(pairs, name):
    targets = [norm(v) for v in re.split(r'[/、]', name) if v.strip()]
    tset = set(targets)
    for pid, title in pairs:
        if title_norm(title) in tset:
            return pid, title
    return None, None


def extract_param(param_html):
    fields = {}
    for m in re.finditer(r'id="newPmName_(\d+)"[^>]*>([^<]+)</(?:span|a)>.*?id="newPmVal_\1"[^>]*>(.*?)</span>', param_html, re.S):
        key = m.group(2).strip()
        txt = re.sub(r'<[^>]+>', '', m.group(3))
        txt = txt.split('更多')[0].replace('>', '').strip()
        fields.setdefault(key, txt)
    out = {f: None for f in FIELDS}

    def find(*names):
        for n in names:
            if n in fields:
                return fields[n]
        return None

    m = re.search(r'(\d{4})年(?:([\d]{1,2})月)?', find('国内发布时间', '上市日期') or '')
    if m:
        out['release_year'] = int(m.group(1))
        if m.group(2):
            out['release_month'] = int(m.group(2))
    out['chip'] = (find('CPU型号') or None)
    if out['chip']:
        out['chip'] = out['chip'][:120]
    m = re.search(r'(\d+(?:\.\d+)?)\s*(GB|TB)', find('RAM容量') or '', re.I)
    if m:
        out['ram'] = m.group(1) + m.group(2).upper()
    m = re.search(r'(\d+(?:\.\d+)?)\s*(GB|TB)', find('ROM容量') or '', re.I)
    if m:
        out['storage'] = m.group(1) + m.group(2).upper()
    m = re.search(r'(\d{3,5})\s*mAh', find('电池容量') or '', re.I)
    if m and int(m.group(1)) <= 12000:
        out['battery_mah'] = int(m.group(1))
    for k in ('有线充电', '充电功率', '快充'):
        v = find(k)
        if v:
            w = re.search(r'(\d+)\s*w\b', v, re.I)
            if w and int(w.group(1)) <= 300:
                out['charge_w'] = int(w.group(1))
            break
    wv = find('无线充电')
    if wv is None:
        out['wireless_w'] = 0
    elif '不支持' in wv or wv == '—':
        out['wireless_w'] = 0
    else:
        w = re.search(r'(\d+)\s*w\b', wv, re.I)
        out['wireless_w'] = int(w.group(1)) if w and int(w.group(1)) <= 120 else None
    m = re.search(r'([\d.]+)\s*英寸', find('屏幕尺寸') or '')
    if m:
        out['screen_size'] = m.group(1) + '英寸'
    m = re.search(r'(\d{3,5})\s*[x×]\s*(\d{3,5})', find('分辨率') or '')
    if m:
        a, b = int(m.group(1)), int(m.group(2))
        out['resolution'] = f'{max(a,b)}×{min(a,b)}'
    v = find('屏幕刷新率') or ''
    m = (re.search(r'Hz（(\d+)-(\d+)）', v, re.I) or re.search(r'(\d+)-(\d+)\s*Hz', v, re.I))
    if m:
        out['refresh_rate'] = f'{m.group(1)}-{m.group(2)}'
    else:
        m = re.search(r'(\d+)\s*Hz', v, re.I)
        if m:
            out['refresh_rate'] = m.group(1)
    px = find('像素') or ''
    m = re.search(r'后置摄像头1：(\d+)万', px)
    if m:
        out['rear_camera'] = m.group(1) + '万主摄'
    else:
        for k, v in fields.items():
            if k.startswith('后置摄像头'):
                mp = re.search(r'(\d+)万', v)
                if mp:
                    out['rear_camera'] = mp.group(1) + '万主摄'
                break
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--limit', type=int, default=0)
    ap.add_argument('--brand', default=None)
    ap.add_argument('--sleep-max', type=float, default=1.6)
    args = ap.parse_args()

    os.makedirs(OUTDIR, exist_ok=True)
    done = set()
    if os.path.exists(PROGRESS):
        for line in io.open(PROGRESS, encoding='utf-8'):
            try:
                rec = json.loads(line)
                done.add((rec['brand'], rec['name']))
            except Exception:
                pass
    brand_out = {}
    for b in BRAND_DISPLAY:
        path = os.path.join(OUTDIR, f'zol-{b}.json')
        if os.path.exists(path):
            try:
                brand_out[b] = json.load(io.open(path, encoding='utf-8'))
            except Exception:
                brand_out[b] = []

    def flush_brand(brand):
        path = os.path.join(OUTDIR, f'zol-{brand}.json')
        with io.open(path, 'w', encoding='utf-8', newline='\n') as f:
            json.dump(brand_out.get(brand, []), f, ensure_ascii=False, indent=1)
    todo = [(b, n) for b, n in pending_list() if (b, n) not in done and (args.brand is None or b == args.brand)]
    if args.limit:
        todo = todo[:args.limit]
    print(f'待处理 {len(todo)} 款 (已完成 {len(done)})', flush=True)

    prog = io.open(PROGRESS, 'a', encoding='utf-8', newline='\n')
    for i, (brand, name) in enumerate(todo):
        rec = {'brand': brand, 'name': name, 'status': 'failed'}
        try:
            displays = BRAND_DISPLAY.get(brand, [brand])
            core = name
            for p in PREFIX:
                if core.lower().startswith(p.lower()) and len(core) > len(p):
                    core = core[len(p):]
                    break
            core = re.split(r'[/、]', core)[0].strip()
            pid = title = None
            for kw in [f'{d}{core}' for d in displays[:2]] + [core]:
                pairs = search_ids(kw)
                sleep()
                pid, title = match_id(pairs, name)
                if pid:
                    break
            if not pid:
                rec['status'] = 'not_found'
            else:
                detail = fetch(f'https://detail.zol.com.cn/cell_phone/index{pid}.shtml')
                sleep()
                mh = re.search(rf'href="/(\d+)/{pid}/param\.shtml"', detail)
                if not mh:
                    rec['status'] = 'no_param_page'
                    rec['zol_id'] = pid
                    rec['zol_title'] = title
                else:
                    param_html = fetch(f'https://detail.zol.com.cn/{mh.group(1)}/{pid}/param.shtml')
                    sleep()
                    rec.update(extract_param(param_html))
                    rec['status'] = 'ok'
                    rec['zol_id'] = pid
                    rec['zol_title'] = title
        except Exception as e:
            rec['error'] = f'{type(e).__name__}: {e}'[:200]
        prog.write(json.dumps(rec, ensure_ascii=False) + '\n')
        prog.flush()
        if rec['status'] == 'ok':
            brand_out.setdefault(brand, []).append(rec)
            if (i + 1) % 5 == 0:
                flush_brand(brand)
        if (i + 1) % 20 == 0:
            print(f'[{i+1}/{len(todo)}] {name} -> {rec["status"]}', flush=True)

    for brand in list(brand_out):
        flush_brand(brand)
    ok = sum(1 for _ in io.open(PROGRESS, encoding='utf-8'))
    print(f'本轮新增 OK: {sum(len(v) for v in brand_out.values())}, 进度文件累计 {ok} 行', flush=True)


if __name__ == '__main__':
    main()
