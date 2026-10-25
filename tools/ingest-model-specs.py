#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把子代理收集的机型参数 JSON 追加进 data-model-specs.sql（guarded UPDATE）。

用法:
    python tools/ingest-model-specs.py <out.json> <brand> <来源标签>
    python tools/ingest-model-specs.py .tmp/model-specs/out/Apple-1.json Apple "联网搜索,2026-10-25"

规则:
- JSON 为数组,元素 name 必须与 .tmp/model-specs/tasks/<brand>-*.input.json 中机型名逐字一致;
- battery_mah/charge_w/wireless_w 只接受 int 或 null(wireless_w 可为 0=不支持),违规字段整条拒收;
- 全空条目(关键字段全 null)跳过;
- 生成 UPDATE ... WHERE chip IS NULL AND name=... AND brand_id=(SELECT id FROM brand ...),幂等且不覆盖人工改值;
- 追加到 data-model-specs.sql 末尾,带来源分段注释;同 (brand,name) 重复输入只取第一条。
"""
import json, re, sys, io, os, glob

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TASKDIR = os.path.join(ROOT, '.tmp', 'model-specs', 'tasks')
OUTSQL = os.path.join(ROOT, 'server', 'start', 'src', 'main', 'resources', 'db', 'data-model-specs.sql')
INT_FIELDS = ('battery_mah', 'charge_w', 'wireless_w')
STR_FIELDS = ('series', 'chip', 'ram', 'storage', 'screen_size', 'resolution', 'refresh_rate', 'rear_camera')
YEAR_MONTH = ('release_year', 'release_month')

def main():
    if len(sys.argv) != 4:
        print(__doc__); sys.exit(2)
    src, brand, provenance = sys.argv[1], sys.argv[2], sys.argv[3]

    # 任务清单:该品牌所有块的机型名集合
    names = set()
    for tf in glob.glob(os.path.join(TASKDIR, f'{brand}-*.input.json')):
        for m in json.load(io.open(tf, encoding='utf-8'))['models']:
            names.add(m)
    if not names:
        print(f'ERR 未找到品牌 {brand} 的任务文件'); sys.exit(1)

    data = json.load(io.open(src, encoding='utf-8'))
    if not isinstance(data, list):
        print('ERR JSON 顶层数组'); sys.exit(1)

    accepted, rejected, empty = [], [], []
    seen = set()
    for ent in data:
        name = ent.get('name')
        if name not in names or name in seen:
            rejected.append((name, '名称不匹配/重复')); continue
        seen.add(name)
        vals = {}
        bad = False
        for f in INT_FIELDS:
            v = ent.get(f)
            if v is not None and (not isinstance(v, int) or isinstance(v, bool)):
                rejected.append((name, f'{f} 非整数: {v!r}')); bad = True; break
            vals[f] = v
        if bad: continue
        for f in YEAR_MONTH:
            v = ent.get(f)
            if v is not None and (not isinstance(v, int) or not (0 <= v <= (12 if f == 'release_month' else 2100))):
                rejected.append((name, f'{f} 非法: {v!r}')); bad = True; break
            vals[f] = v
        if bad: continue
        for f in STR_FIELDS:
            v = ent.get(f)
            vals[f] = str(v).strip() if v is not None else None
        if all(vals[f] is None for f in INT_FIELDS + YEAR_MONTH + STR_FIELDS):
            empty.append(name); continue
        accepted.append((name, vals))

    def q(v):
        return 'NULL' if v is None else "'" + str(v).replace("'", "''") + "'"

    lines = [f'', f'-- ===== 子代理收集·{brand}（{len(accepted)} 款，来源: {provenance}）=====']
    for name, vals in accepted:
        sets = ', '.join(f'`{f}`={q(vals[f])}' for f in YEAR_MONTH + INT_FIELDS + STR_FIELDS)
        lines.append(
            f'UPDATE `phone_model` SET {sets}\n'
            f'WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`={q(name)}\n'
            f'  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`={q(brand)} AND `tenant_id`=0);')

    with io.open(OUTSQL, 'a', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(lines) + '\n')

    print(f'OK {brand}: 收 {len(accepted)} / 空 {len(empty)} / 拒 {len(rejected)}')
    for name, why in rejected[:10]:
        print(f'  拒收: {name} — {why}')

if __name__ == '__main__':
    main()
