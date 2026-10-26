#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""ZOL 品牌专区目录爬虫：绕开 search.zol.com.cn 搜索（命中率天花板 ~40%），
直接抓 mobile.zol.com.cn/manu_<id>.shtml 专区页（全量产品目录，单页 300+ 款），
本地归一化匹配库内缺失机型后抓参数页。

专区映射（mobile.zol.com.cn 首页 2026-10-26）：
  华为 613 / vivo 1795 / OPPO 1673 / 小米 34645 / 苹果 544 / 荣耀 50840 / 三星 98 / 一加 35579 / 努比亚 35005(含红魔)

用法:
    python tools/fetch-zol-catalog-specs.py --dry-run   # 只建目录+匹配统计
    python tools/fetch-zol-catalog-specs.py             # 目录匹配后抓参数
"""
import argparse, importlib.util, io, json, os, re, sys, time, urllib.request

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
spec = importlib.util.spec_from_file_location('fz', os.path.join(ROOT, 'tools', 'fetch-zol-specs.py'))
fz = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fz)

MANU = {'华为': 613, 'vivo': 1795, 'OPPO': 1673, '小米': 34645, 'Apple': 544,
        '荣耀': 50840, '三星': 98, '一加': 35579, '努比亚': 35005, '红魔': 35005}
UA = fz.UA
CATALOG_CACHE = os.path.join(ROOT, '.tmp', 'model-specs', 'zol_catalog.json')


def fetch(url, timeout=30):
    raw = urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=timeout).read()
    try:
        return raw.decode('gbk')
    except UnicodeDecodeError:
        return raw.decode('utf-8', 'replace')


def parse_products(html):
    """专区页产品名:主列表 <a ... class="product-title">名称</a>(与 pic a 同一个产品 ID),
    精选模块 <a ... title ="名称">。"""
    out = {}
    for m in re.finditer(r'href="//detail\.zol\.com\.cn/cell_phone/index(\d+)\.shtml"[^>]*class="product-title"\s*>([^<]{2,80})<', html):
        out[m.group(1)] = m.group(2).strip()
    if len(out) < 40:
        for m in re.finditer(r'href="//detail\.zol\.com\.cn/cell_phone/index(\d+)\.shtml"[^>]*title\s*=\s*"([^"]{2,80})"', html):
            out.setdefault(m.group(1), m.group(2).strip())
    return out


def build_catalog():
    catalog = {}
    brands = {}
    for brand, manu in MANU.items():
        h = fetch(f'https://mobile.zol.com.cn/manu_{manu}.shtml')
        prods = parse_products(h)
        for pid, name in prods.items():
            catalog[pid] = {'name': name, 'brand': brand}
            brands.setdefault(pid, set()).add(brand)
        print(f'{brand}(manu_{manu}): 解析 {len(prods)} 款', flush=True)
        time.sleep(1.2)
    with io.open(CATALOG_CACHE, 'w', encoding='utf-8', newline='\n') as f:
        json.dump(catalog, f, ensure_ascii=False, indent=1)
    print(f'目录合计 {len(catalog)} 款,缓存 {CATALOG_CACHE}', flush=True)
    return catalog


NAME_BRAND = [('Redmi', 'Redmi'), ('REDMI', 'Redmi'), ('红米', 'Redmi'), ('iQOO', 'iQOO'),
              ('红魔', '红魔'), ('努比亚', '努比亚'), ('nubia', '努比亚'), ('真我', 'realme'),
              ('realme', 'realme'), ('HONOR', '荣耀'), ('荣耀', '荣耀'), ('HUAWEI', '华为'),
              ('华为', '华为'), ('iPhone', 'Apple'), ('苹果', 'Apple'), ('三星', '三星'),
              ('SAMSUNG', '三星'), ('一加', '一加'), ('OnePlus', '一加'), ('Xiaomi', '小米'),
              ('小米', '小米'), ('OPPO', 'OPPO'), ('vivo', 'vivo')]


def infer_brand(name):
    s = str(name).strip()
    for kw, brand in NAME_BRAND:
        if s.lower().startswith(kw.lower()):
            return brand
    return None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--dry-run', action='store_true')
    args = ap.parse_args()

    if os.path.exists(CATALOG_CACHE):
        catalog = json.load(io.open(CATALOG_CACHE, encoding='utf-8'))
        print(f'用缓存目录 {len(catalog)} 款')
    else:
        catalog = build_catalog()

    # 全局归一化索引: normkey -> [(pid, name, inferred_brand)]
    idx = {}
    for pid, ent in catalog.items():
        name = ent['name']
        n = fz.title_norm(name)
        if n:
            idx.setdefault(n, []).append((pid, name, infer_brand(name) or ent['brand']))

    todo = fz.pending_list()
    print(f'库内待补机型 {len(todo)} 款', flush=True)

    matched, missed = {}, []
    for brand, name in todo:
        hit = None
        for variant in re.split(r'[/、]', name):
            k = fz.title_norm(variant)
            if not k:
                continue
            cands = [c for c in idx.get(k, []) if c[2] == brand]
            if cands:
                hit = cands[0]
                break
        if hit:
            matched[(brand, name)] = hit
        else:
            missed.append((brand, name))
    print(f'目录匹配 {len(matched)} / 未匹配 {len(missed)}', flush=True)
    import collections
    print('未匹配按品牌:', dict(collections.Counter(b for b, _ in missed)))
    if args.dry_run:
        with io.open(os.path.join(ROOT, '.tmp', 'model-specs', 'catalog_misses.json'), 'w', encoding='utf-8', newline='\n') as f:
            json.dump([[b, n] for b, n in missed], f, ensure_ascii=False, indent=1)
        return

    # 抓参数（跳过已 ok 的）
    done = set()
    prog_path = os.path.join(ROOT, '.tmp/model-specs/zol_progress.jsonl')
    if os.path.exists(prog_path):
        for line in io.open(prog_path, encoding='utf-8'):
            try:
                r = json.loads(line)
                if r['status'] == 'ok':
                    done.add((r['brand'], r['name']))
            except Exception:
                pass

    brand_out = {}
    for b in MANU:
        p = os.path.join(ROOT, f'.tmp/model-specs/out/zol-{b}.json')
        if os.path.exists(p):
            try:
                brand_out[b] = json.load(io.open(p, encoding='utf-8'))
            except Exception:
                brand_out[b] = []

    ok = 0
    prog = io.open(prog_path, 'a', encoding='utf-8', newline='\n')
    items = [(bn, hit) for bn, hit in matched.items() if bn not in done]
    print(f'待抓参数 {len(items)} 款', flush=True)
    for i, ((brand, name), (pid, _cname, _b)) in enumerate(items):
        rec = {'brand': brand, 'name': name, 'status': 'failed', 'via': 'catalog'}
        try:
            detail = fetch(f'https://detail.zol.com.cn/cell_phone/index{pid}.shtml')
            fz.sleep()
            mh = re.search(rf'href="/(\d+)/{pid}/param\.shtml"', detail)
            if not mh:
                rec['status'] = 'no_param_page'
                rec['zol_id'] = pid
            else:
                rec.update(fz.extract_param(fz.fetch(f'https://detail.zol.com.cn/{mh.group(1)}/{pid}/param.shtml')))
                fz.sleep()
                rec['status'] = 'ok'
                rec['zol_id'] = pid
                rec['zol_title'] = _cname
        except Exception as e:
            rec['error'] = f'{type(e).__name__}: {e}'[:200]
        prog.write(json.dumps(rec, ensure_ascii=False) + '\n')
        prog.flush()
        if rec['status'] == 'ok':
            brand_out.setdefault(brand, []).append(rec)
            ok += 1
            if ok % 5 == 0:
                path = os.path.join(ROOT, f'.tmp/model-specs/out/zol-{brand}.json')
                with io.open(path, 'w', encoding='utf-8', newline='\n') as f:
                    json.dump(brand_out.get(brand, []), f, ensure_ascii=False, indent=1)
        if (i + 1) % 20 == 0:
            print(f'[{i+1}/{len(items)}] {name} -> {rec["status"]}', flush=True)
    for brand in brand_out:
        path = os.path.join(ROOT, f'.tmp/model-specs/out/zol-{brand}.json')
        with io.open(path, 'w', encoding='utf-8', newline='\n') as f:
            json.dump(brand_out[brand], f, ensure_ascii=False, indent=1)
    print(f'目录抓取完成: OK {ok}/{len(items)}', flush=True)


if __name__ == '__main__':
    main()
