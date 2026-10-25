#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""ZOL 爬虫二次补救：对主跑 not_found 的机型换变体关键词重试。

关键词策略（依次尝试，命中即停）：
  1) 品牌+核心名去空格（'iQOONeo5活力版'）
  2) 核心名去空格（'Neo5活力版'）
  3) 完整机型名去空格
成功记录追加到 out/<品牌>-zol.json 与 zol_progress.jsonl（带 retry: true）。
用法: python tools/fetch-zol-specs-retry.py [--limit N] [--brand X]
"""
import argparse, importlib.util, io, json, os, re, sys

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
spec = importlib.util.spec_from_file_location('fz', os.path.join(ROOT, 'tools', 'fetch-zol-specs.py'))
fz = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fz)

ap = argparse.ArgumentParser()
ap.add_argument('--limit', type=int, default=0)
ap.add_argument('--brand', default=None)
args = ap.parse_args()

prog_path = os.path.join(ROOT, '.tmp/model-specs/zol_progress.jsonl')
done, misses = set(), []
for line in io.open(prog_path, encoding='utf-8'):
    try:
        r = json.loads(line)
        done.add((r['brand'], r['name']))
        if r['status'] != 'ok' and (args.brand is None or r['brand'] == args.brand):
            misses.append((r['brand'], r['name']))
    except Exception:
        pass
# out 文件里已有的也算完成
brand_out = {}
for b in fz.BRAND_DISPLAY:
    p = os.path.join(ROOT, f'.tmp/model-specs/out/zol-{b}.json')
    if os.path.exists(p):
        try:
            brand_out[b] = json.load(io.open(p, encoding='utf-8'))
        except Exception:
            brand_out[b] = []
    else:
        brand_out[b] = []
    for r in brand_out[b]:
        done.add((b, r['name']))
todo = [(b, n) for b, n in misses if (b, n) not in {(rb, rn) for rb, rn in done if any(r['name'] == rn and r['brand'] == rb and r['status'] == 'ok' for r in brand_out.get(rb, []))}]
todo = list(dict.fromkeys(todo))
if args.limit:
    todo = todo[:args.limit]
print(f'待补救 {len(todo)} 款', flush=True)


EN_BRAND = {'小米': 'Xiaomi', '华为': 'HUAWEI', '荣耀': 'HONOR', '红米': 'Redmi',
            '真我': 'realme', '努比亚': 'nubia', '一加': 'OnePlus', '红魔': 'RedMagic', '三星': 'SAMSUNG'}


def variants(brand, name):
    displays = fz.BRAND_DISPLAY.get(brand, [brand])
    core = name
    for p in fz.PREFIX:
        if core.lower().startswith(p.lower()) and len(core) > len(p):
            core = core[len(p):]
            break
    core = re.split(r'[/、]', core)[0].strip()
    toks = core.split(' ')
    en = EN_BRAND.get(brand)
    cands = []
    for d in displays[:1]:
        cands.append(f'{d}{core}')                 # 无空格拼接
        cands.append(f'{d} {core}')                # 空格+完整型号
        if en:
            cands.append(f'{d}{en} {core}')        # 中文厂牌+英文厂牌+型号
        cands.append(f'{d} {core.replace(" ", "")}')
        for k in range(len(toks) - 1, 1, -1):
            cands.append(f'{d} ' + ' '.join(toks[:k]))
            if en:
                cands.append(f'{d}{en} ' + ' '.join(toks[:k]))
    return list(dict.fromkeys(k for k in cands if k.strip()))[:8]


ok = 0
prog = io.open(prog_path, 'a', encoding='utf-8', newline='\n')
for i, (brand, name) in enumerate(todo):
    rec = {'brand': brand, 'name': name, 'status': 'failed', 'retry': True}
    try:
        pid = title = None
        for kw in variants(brand, name)[:4]:
            pairs = fz.search_ids(kw)
            fz.sleep()
            pid, title = fz.match_id(pairs, name)
            if pid:
                break
        if not pid:
            rec['status'] = 'not_found'
        else:
            detail = fz.fetch(f'https://detail.zol.com.cn/cell_phone/index{pid}.shtml')
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
                rec['zol_title'] = title
    except Exception as e:
        rec['error'] = f'{type(e).__name__}: {e}'[:200]
    prog.write(json.dumps(rec, ensure_ascii=False) + '\n')
    prog.flush()
    if rec['status'] == 'ok':
        brand_out.setdefault(brand, []).append(rec)
        ok += 1
        path = os.path.join(ROOT, f'.tmp/model-specs/out/zol-{brand}.json')
        with io.open(path, 'w', encoding='utf-8', newline='\n') as f:
            json.dump(brand_out[brand], f, ensure_ascii=False, indent=1)
    if (i + 1) % 20 == 0:
        print(f'[{i+1}/{len(todo)}] {name} -> {rec["status"]}', flush=True)
print(f'补救成功 {ok}/{len(todo)}', flush=True)
