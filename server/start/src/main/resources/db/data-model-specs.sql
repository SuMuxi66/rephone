-- 机型参数数据。来源：
--   1)《2020-2026 主力机型参数表》（用户提供，2026-10-25）——「Excel 参数表」节；
--   2) 子代理联网收集结果——按品牌分段追加，每段注明来源与日期。
-- 语义：guarded UPDATE，仅当 chip IS NULL 时填充，幂等且不覆盖后台人工修改。
-- 已挂入 application.yml sql-init 与 P2QuoteFlowTest（H2 验证）。

-- ===== Excel 参数表（304 款，2026-10-25）=====

UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='Mate系列', `chip`='麒麟9030', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5300', `charge_w`='100', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='2832×1280', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 90'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='Mate系列', `chip`='麒麟9035', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='80', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 90 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='Mate系列', `chip`='麒麟9050', `ram`='12GB/16GB', `storage`='512GB/1TB/2TB', `battery_mah`='5700', `charge_w`='100', `wireless_w`='80', `screen_size`='6.9英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 90 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='Mate系列', `chip`='麒麟9020', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5200', `charge_w`='100', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='2832×1280', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 80'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='Mate系列', `chip`='麒麟9025', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5400', `charge_w`='100', `wireless_w`='80', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 80 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='Mate系列', `chip`='麒麟9020A/B', `ram`='12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5100', `charge_w`='66', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2700×1224', `refresh_rate`='90', `rear_camera`='5000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 70 Air'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='Mate系列', `chip`='麒麟9010', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='4900', `charge_w`='66', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='2700×1224', `refresh_rate`='90', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='Mate系列', `chip`='麒麟9010', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='2820×1260', `refresh_rate`='90', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 70 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='Mate系列', `chip`='麒麟9000S', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='4750', `charge_w`='66', `wireless_w`='50', `screen_size`='6.69英寸', `resolution`='2720×1260', `refresh_rate`='90', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='Mate系列', `chip`='麒麟9000S', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='88', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='2720×1260', `refresh_rate`='90', `rear_camera`='4800万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `series`='Mate系列', `chip`='骁龙8+ Gen1 4G', `ram`='8GB', `storage`='128GB/256GB/512GB', `battery_mah`='4460', `charge_w`='66', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='2700×1224', `refresh_rate`='90', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `series`='Mate系列', `chip`='骁龙8+ Gen1 4G', `ram`='8GB', `storage`='256GB/512GB', `battery_mah`='4700', `charge_w`='66', `wireless_w`='50', `screen_size`='6.74英寸', `resolution`='2616×1212', `refresh_rate`='120', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `series`='Mate系列', `chip`='麒麟9000E', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4200', `charge_w`='40', `wireless_w`='40', `screen_size`='6.5英寸', `resolution`='2376×1080', `refresh_rate`='90', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `series`='Mate系列', `chip`='麒麟9000', `ram`='8GB', `storage`='128GB/256GB/512GB', `battery_mah`='4400', `charge_w`='66', `wireless_w`='50', `screen_size`='6.76英寸', `resolution`='2772×1344', `refresh_rate`='90', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 40 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='7', `series`='Pura系列', `chip`='麒麟9030', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.6英寸', `resolution`='2800×1260', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Pura 90'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='4', `series`='Pura系列', `chip`='麒麟9020', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='4900', `charge_w`='88', `wireless_w`='50', `screen_size`='6.6英寸', `resolution`='2720×1224', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Pura 80'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='Pura系列', `chip`='麒麟9000S1', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4900', `charge_w`='66', `wireless_w`='50', `screen_size`='6.6英寸', `resolution`='2700×1224', `refresh_rate`='90', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Pura 70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='Pura系列', `chip`='麒麟9000S1', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='5050', `charge_w`='80', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Pura 70 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='P系列', `chip`='骁龙8+ Gen1 4G', `ram`='8GB', `storage`='128GB/256GB/512GB', `battery_mah`='4815', `charge_w`='66', `wireless_w`='50', `screen_size`='6.67英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='4800万超聚光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='P系列', `chip`='骁龙8+ Gen1 4G', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4815', `charge_w`='88', `wireless_w`='50', `screen_size`='6.67英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='4800万超聚光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='7', `series`='P系列', `chip`='骁龙888 4G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4100', `charge_w`='66', `wireless_w`='50', `screen_size`='6.5英寸', `resolution`='2700×1224', `refresh_rate`='90', `rear_camera`='5000万原色'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='7', `series`='P系列', `chip`='麒麟9000/骁龙888 4G', `ram`='8GB', `storage`='128GB/256GB/512GB', `battery_mah`='4360', `charge_w`='66', `wireless_w`='50', `screen_size`='6.6英寸', `resolution`='2700×1228', `refresh_rate`='120', `rear_camera`='5000万原色'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `series`='P系列', `chip`='麒麟990 5G', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='3800', `charge_w`='22', `wireless_w`='27', `screen_size`='6.1英寸', `resolution`='2340×1080', `refresh_rate`='60', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `series`='P系列', `chip`='麒麟990 5G', `ram`='8GB', `storage`='128GB/256GB/512GB', `battery_mah`='4200', `charge_w`='40', `wireless_w`='27', `screen_size`='6.58英寸', `resolution`='2640×1200', `refresh_rate`='90', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P40 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='6', `series`='nova系列', `chip`='麒麟9010S', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.68英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='12', `series`='nova系列', `chip`='麒麟8020', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1084', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='nova系列', `chip`='麒麟8010', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1084', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='10', `series`='nova系列', `chip`='麒麟8000', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='nova系列', `chip`='麒麟8000/骁龙778G', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1084', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='nova系列', `chip`='麒麟8000/骁龙778G', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1084', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nova 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='4', `series`='nova系列', `chip`='骁龙778G 4G', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='66', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1084', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='7', `series`='nova系列', `chip`='骁龙778G 4G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='66', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='nova系列', `chip`='骁龙778G 4G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4300', `charge_w`='66', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `series`='nova系列', `chip`='麒麟985 5G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='3800', `charge_w`='66', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `series`='nova系列', `chip`='麒麟985 5G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='40', `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='畅享系列', `chip`='麒麟8系', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='40', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为畅享 100'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='8', `series`='畅享系列', `chip`='麒麟7系', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='40', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为畅享 90'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='畅享系列', `chip`='麒麟710A', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.75英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为畅享 70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='畅享系列', `chip`='麒麟710A', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.75英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='畅享 70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='畅享系列', `chip`='麒麟710A', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.75英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为畅享 60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='6', `series`='畅享系列', `chip`='麒麟710A', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.75英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为畅享 50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='折叠屏', `chip`='麒麟9020', `ram`='12GB', `storage`='512GB/1TB', `battery_mah`='4720', `charge_w`='66', `wireless_w`='50', `screen_size`='外6.5/内7.9', `resolution`='外2400×1080', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Pura X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='折叠屏', `chip`='麒麟9020', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5200', `charge_w`='100', `wireless_w`='50', `screen_size`='外6.4/内7.9', `resolution`='外2504×1080', `refresh_rate`='120', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate X6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `series`='折叠屏', `chip`='麒麟9000S', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5060', `charge_w`='66', `wireless_w`='50', `screen_size`='外6.4/内7.85', `resolution`='外2504×1080', `refresh_rate`='120', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate X5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='折叠屏', `chip`='骁龙8+ Gen1 4G', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='4800', `charge_w`='66', `wireless_w`='50', `screen_size`='外6.4/内7.85', `resolution`='外2504×1080', `refresh_rate`='120', `rear_camera`='5000万超光变'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate X3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='2', `series`='折叠屏', `chip`='麒麟9000', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='50', `screen_size`='外6.45/内8', `resolution`='外2700×1160', `refresh_rate`='90', `rear_camera`='5000万超感知'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate X2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `series`='折叠屏', `chip`='麒麟990 5G', `ram`='8GB', `storage`='512GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='27', `screen_size`='8英寸折叠', `resolution`='2480×2200', `refresh_rate`='60', `rear_camera`='4000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate Xs'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite Gen6 Pro', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='8500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.9英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 18 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite Gen6', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='8000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 18 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.67英寸', `resolution`='2800×1260', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 17 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6100', `charge_w`='90', `wireless_w`='50', `screen_size`='6.56英寸', `resolution`='2712×1220', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 16 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4880', `charge_w`='90', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2712×1220', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5100', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 15 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4610', `charge_w`='90', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2670×1200', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4610', `charge_w`='90', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2670×1200', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 14 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14 Pro (`shennong`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5400万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5400万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 13 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='50', `screen_size`='6.28英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4600', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 12 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4600', `charge_w`='55', `wireless_w`='50', `screen_size`='6.81英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='67', `screen_size`='6.81英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万GN2'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 11 Pro (`star`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4780', `charge_w`='30', `wireless_w`='30', `screen_size`='6.67英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='50', `wireless_w`='30', `screen_size`='6.67英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 10 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='2', `series`='Ultra系列', `chip`='骁龙8 Elite Gen5', `ram`='16GB', `storage`='512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸大底'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 17 Ultra / Xiaomi 17 Ultra by Leica'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='2', `series`='Ultra系列', `chip`='骁龙8 Gen3', `ram`='16GB', `storage`='512GB/1TB', `battery_mah`='5300', `charge_w`='90', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸大底'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 15 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='2', `series`='Ultra系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5300', `charge_w`='90', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸大底'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14 Ultra (`aurora`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='4', `series`='Ultra系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='90', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸大底'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 13 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='7', `series`='Ultra系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4860', `charge_w`='67', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1英寸IMX989'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 12S Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='Ultra系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='67', `screen_size`='6.81英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万GN2'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 11 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='3', `series`='Civi系列', `chip`='骁龙8s Gen3', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4700', `charge_w`='67', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi Civi 4 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='Civi系列', `chip`='天玑8200-Ultra', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi Civi 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `series`='Civi系列', `chip`='骁龙7 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi Civi 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='Civi系列', `chip`='骁龙778G', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi Civi'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='MIX系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB', `battery_mah`='4780', `charge_w`='67', `wireless_w`='0', `screen_size`='外6.86/内6.86', `resolution`='2520×1080', `refresh_rate`='120', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi MIX Flip'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='MIX系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='50', `screen_size`='外6.56/内8.03', `resolution`='外2520×1080', `refresh_rate`='120', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi MIX Fold 4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='MIX系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4800', `charge_w`='67', `wireless_w`='50', `screen_size`='外6.56/内8.03', `resolution`='外2520×1080', `refresh_rate`='120', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi MIX Fold 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='8', `series`='MIX系列', `chip`='骁龙8+ Gen1', `ram`='12GB', `storage`='256GB/512GB/1TB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='0', `screen_size`='外6.56/内8.02', `resolution`='外2520×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi MIX Fold 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='3', `series`='K系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI K90'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='K系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6550', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI K80'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='K系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI K80 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='K系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='K系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='K系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='67', `wireless_w`='30', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='K系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5400万IMX800'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='K系列', `chip`='天玑8100', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='K系列', `chip`='天玑9000', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='2', `series`='K系列', `chip`='骁龙870', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4520', `charge_w`='33', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='2', `series`='K系列', `chip`='骁龙888', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4520', `charge_w`='33', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='8', `series`='Note系列', `chip`='骁龙4 Gen4', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='8000', `charge_w`='45', `wireless_w`='0', `screen_size`='6.88英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Note 17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='8', `series`='Note系列', `chip`='骁龙7s Gen4', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='7500', `charge_w`='90', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Note 17 Pro (`iolite`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='Note系列', `chip`='骁龙6 Gen3', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='7000', `charge_w`='45', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Note 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='9', `series`='Note系列', `chip`='骁龙6 Gen3', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='6500', `charge_w`='45', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Note 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `series`='Note系列', `chip`='骁龙7s Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 14 5G / REDMI Note 15R Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `series`='Note系列', `chip`='骁龙7s Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 13 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='10', `series`='Note系列', `chip`='骁龙4 Gen1', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 12 5G / Redmi Note 12R Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='10', `series`='Note系列', `chip`='天玑1080', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 12 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='10', `series`='Note系列', `chip`='天玑810', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.6英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 11 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='Note系列', `chip`='天玑700', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='18', `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 10 5G / Redmi Note 11SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='11', `series`='Note系列', `chip`='天玑800U', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Note 9 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='8', `series`='数字系列', `chip`='骁龙4 Gen4', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='8000', `charge_w`='45', `wireless_w`='0', `screen_size`='6.88英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI 17 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='数字系列', `chip`='骁龙6s Gen3', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='7000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.79英寸', `resolution`='2460×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='数字系列', `chip`='Helio G85', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='18', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi 13C 5G / Redmi 13R 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='数字系列', `chip`='骁龙4 Gen2', `ram`='4GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='18', `wireless_w`='0', `screen_size`='6.79英寸', `resolution`='2460×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi 12 5G (`sky`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Turbo系列', `chip`='天玑9500s', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='9000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.83英寸', `resolution`='2712×1220', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Turbo 5 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `series`='Turbo系列', `chip`='天玑8400-Ultra', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='90', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Turbo 4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='Turbo系列', `chip`='骁龙8s Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='90', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Turbo 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='电竞系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70 电竞版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='电竞系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4700', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50 电竞版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='4', `series`='电竞系列', `chip`='天玑1200', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5065', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40 游戏增强版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Redmi' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='Find系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='Find系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5700', `charge_w`='100', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X8 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='2', `series`='Find系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸LYT-900'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X7 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='Find系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1英寸IMX989'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X6 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='Find系列', `chip`='骁龙8 Gen1/天玑9000', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='3216×1440', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='Find系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4500', `charge_w`='65', `wireless_w`='30', `screen_size`='6.7英寸', `resolution`='3216×1440', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X3 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `series`='Find系列', `chip`='骁龙865', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4260', `charge_w`='65', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='3168×1440', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X2 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='Reno系列', `chip`='天玑8450', `ram`='12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.32英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno16 / OPPO Reno16t'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='Reno系列', `chip`='天玑9300+', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2772×1240', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno16 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Reno系列', `chip`='天玑8350', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Reno系列', `chip`='天玑9200+', `ram`='12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2772×1240', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno15 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `series`='Reno系列', `chip`='天玑8300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='80', `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno13 (`milkyway`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='6', `series`='Reno系列', `chip`='天玑8250', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='80', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2772×1240', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno12 (`omega`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='Reno系列', `chip`='天玑8200', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Reno 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='Reno系列', `chip`='天玑8200', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='Reno系列', `chip`='骁龙778G', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='80', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='Reno系列', `chip`='骁龙778G+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='5', `series`='Reno系列', `chip`='天玑1300', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='11', `series`='Reno系列', `chip`='骁龙778G', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4500', `charge_w`='60', `wireless_w`='0', `screen_size`='6.43英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='5', `series`='Reno系列', `chip`='天玑900', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4300', `charge_w`='65', `wireless_w`='0', `screen_size`='6.43英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `series`='Reno系列', `chip`='骁龙765G', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4300', `charge_w`='65', `wireless_w`='0', `screen_size`='6.43英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='6', `series`='Reno系列', `chip`='骁龙765G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4020', `charge_w`='65', `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='A系列', `chip`='天玑7300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='6000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A5 Pro (`alpha`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='A系列', `chip`='天玑7050', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A3 Pro / OPPO A5 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `series`='A系列', `chip`='天玑7050', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A2 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='A系列', `chip`='骁龙695', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4800', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A1 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='折叠屏', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5400', `charge_w`='100', `wireless_w`='50', `screen_size`='外6.3/内7.82', `resolution`='外2489×1080', `refresh_rate`='120', `rear_camera`='5000万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find N5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `series`='折叠屏', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='4805', `charge_w`='67', `wireless_w`='0', `screen_size`='外6.31/内7.82', `resolution`='外2489×1080', `refresh_rate`='120', `rear_camera`='4800万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find N3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='折叠屏', `chip`='骁龙8+ Gen1', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4520', `charge_w`='67', `wireless_w`='0', `screen_size`='外5.54/内7.1', `resolution`='外1920×1080', `refresh_rate`='120', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find N2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='折叠屏', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='33', `wireless_w`='15', `screen_size`='外5.49/内7.1', `resolution`='外1972×988', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find N'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='GT系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='GT系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5800', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='GT系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5240', `charge_w`='150', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='2772×1240', `refresh_rate`='144', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='GT系列', `chip`='天玑8400-MAX', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB', `battery_mah`='7000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo7 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='GT系列', `chip`='骁龙7+ Gen3', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo6 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='4', `series`='GT系列', `chip`='骁龙7+ Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='2772×1240', `refresh_rate`='144', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo5 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='GT系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4600', `charge_w`='150', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='2772×1240', `refresh_rate`='144', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='GT系列', `chip`='天玑8100', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='GT系列', `chip`='骁龙870', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='65', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT Neo2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='7', `series`='GT系列', `chip`='骁龙870', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='65', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 GT 大师探索版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='数字系列', `chip`='骁龙6 Gen3', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='5000', `charge_w`='45', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2372×1080', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='数字系列', `chip`='骁龙6 Gen3', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.72英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='2', `series`='数字系列', `chip`='骁龙6 Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.72英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='数字系列', `chip`='天玑7050', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='90', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='数字系列', `chip`='天玑700', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='90', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='Q系列', `chip`='骁龙695', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='60', `wireless_w`='0', `screen_size`='6.6英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='4', `series`='Q系列', `chip`='骁龙750G', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='30', `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `series`='Q系列', `chip`='天玑800U', `ram`='4GB/6GB/8GB', `storage`='128GB', `battery_mah`='5000', `charge_w`='30', `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `series`='V系列', `chip`='天玑6300', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='45', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='6', `series`='V系列', `chip`='天玑6100+', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='V系列', `chip`='天玑700', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='V系列', `chip`='骁龙695', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V25'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `series`='V系列', `chip`='天玑800U', `ram`='6GB/8GB', `storage`='128GB', `battery_mah`='4310', `charge_w`='50', `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `series`='V系列', `chip`='天玑720', `ram`='6GB/8GB', `storage`='128GB', `battery_mah`='5000', `charge_w`='30', `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='10', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5400', `charge_w`='100', `wireless_w`='50', `screen_size`='6.82英寸', `resolution`='3168×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万LYT-808'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='1', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='3216×1440', `refresh_rate`='120', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='1', `series`='数字系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='3216×1440', `refresh_rate`='120', `rear_camera`='4800万IMX789'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 10 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='65', `wireless_w`='50', `screen_size`='6.7英寸', `resolution`='3216×1440', `refresh_rate`='120', `rear_camera`='4800万IMX789'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4510', `charge_w`='30', `wireless_w`='30', `screen_size`='6.78英寸', `resolution`='3168×1440', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 8 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4300', `charge_w`='30', `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='10', `series`='Ace系列', `chip`='骁龙8至尊版', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7800', `charge_w`='120', `wireless_w`='0', `screen_size`='6.83英寸', `resolution`='2800×1272', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='10', `series`='Ace系列', `chip`='骁龙8至尊版', `ram`='16GB/24GB', `storage`='512GB/1TB', `battery_mah`='8600', `charge_w`='100', `wireless_w`='0', `screen_size`='6.83英寸', `resolution`='2800×1272', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 6 至尊版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='12', `series`='Ace系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='Ace系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2780×1264', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='Ace系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='2772×1240', `refresh_rate`='120', `rear_camera`='5000万IMX890'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='Ace系列', `chip`='天玑8100-MAX', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='150', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='5000万IMX766'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='5', `series`='Ace系列', `chip`='天玑8100-MAX', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='67', `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2412×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Ace 竞速版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `series`='折叠屏', `chip`='骁龙8 Gen2', `ram`='16GB', `storage`='512GB', `battery_mah`='4805', `charge_w`='67', `wireless_w`='0', `screen_size`='外6.31/内7.82', `resolution`='外2489×1080', `refresh_rate`='120', `rear_camera`='4800万哈苏'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='一加 Open'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='一加' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='3', `series`='X系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.85英寸', `resolution`='3160×1440', `refresh_rate`='1-144 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X500 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='3', `series`='X系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3160×1440', `refresh_rate`='1-144 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X500 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='X系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5700', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3160×1440', `refresh_rate`='1-144 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X300 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='10', `series`='X系列', `chip`='天玑9400', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3160×1440', `refresh_rate`='1-144 LTPO', `rear_camera`='2亿蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X200 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='X系列', `chip`='天玑9300', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5400', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3000×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X100 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='X系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4700', `charge_w`='80', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X90 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='X系列', `chip`='天玑9000/骁龙8 Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4700', `charge_w`='80', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X80 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='X系列', `chip`='骁龙888+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X70 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `series`='X系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4200', `charge_w`='55', `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X60 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `series`='X系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4350', `charge_w`='44', `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X50 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='6', `series`='X系列', `chip`='骁龙765G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4200', `charge_w`='33', `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='S系列', `chip`='天玑8400', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='S系列', `chip`='天玑8300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S50 / vivo S50t / vivo S50t 元气版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='S系列', `chip`='天玑7300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='80', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='S系列', `chip`='天玑7200', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='80', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='S系列', `chip`='骁龙7 Gen3/天玑9200+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='80', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='S18'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='S系列', `chip`='骁龙7 Gen3/天玑9200+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='80', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S18'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='S系列', `chip`='骁龙778G+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='80', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='120', `rear_camera`='5000万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='S系列', `chip`='骁龙870/天玑8200', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4600', `charge_w`='66', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万人像'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='5', `series`='S系列', `chip`='骁龙870/天玑8100', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='66', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='S系列', `chip`='天玑1100', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4200', `charge_w`='44', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='7', `series`='S系列', `chip`='天玑1100', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4050', `charge_w`='44', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='S系列', `chip`='天玑1100', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `series`='S系列', `chip`='骁龙765G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `series`='S系列', `chip`='Exynos 980', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='18', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='Y系列', `chip`='天玑7300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='7000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y600 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='Y系列', `chip`='天玑8300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='7000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y600 Turbo / vivo Y600k Turbo'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='Y系列', `chip`='天玑7200', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='6000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y500 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='Y系列', `chip`='骁龙7 Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y300 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='Y系列', `chip`='骁龙6 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y200'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='Y系列', `chip`='天玑7020', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y100'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='Y系列', `chip`='天玑7020', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Y100'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='4', `series`='Y系列', `chip`='骁龙695', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y78+ / vivo Y78+ (t1)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='7', `series`='Y系列', `chip`='天玑930', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.64英寸', `resolution`='2388×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y77'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='11', `series`='Y系列', `chip`='天玑810', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4100', `charge_w`='44', `wireless_w`='0', `screen_size`='6.58英寸', `resolution`='2408×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y76s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `series`='Y系列', `chip`='天玑720', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4100', `charge_w`='18', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y73s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='5', `series`='Y系列', `chip`='Exynos 880', `ram`='6GB/8GB', `storage`='128GB', `battery_mah`='4500', `charge_w`='18', `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y70s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='3', `series`='折叠屏', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5700', `charge_w`='120', `wireless_w`='50', `screen_size`='外6.6/内8.03', `resolution`='外2800×1260', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X Fold6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='8', `series`='折叠屏', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='50', `screen_size`='外6.53/内8.03', `resolution`='外2800×1260', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X Fold5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='3', `series`='折叠屏', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5700', `charge_w`='120', `wireless_w`='50', `screen_size`='外6.53/内8.03', `resolution`='外2800×1260', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X Fold3 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='4', `series`='折叠屏', `chip`='骁龙8 Gen2', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4800', `charge_w`='120', `wireless_w`='50', `screen_size`='外6.53/内8.03', `resolution`='外2520×1080', `refresh_rate`='120', `rear_camera`='5000万蔡司'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X Fold2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='折叠屏', `chip`='骁龙8 Gen1', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='66', `wireless_w`='0', `screen_size`='外6.53/内8.03', `resolution`='外2520×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X Fold'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='3', `series`='数字系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.85英寸', `resolution`='3120×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3120×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3120×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='7', `series`='数字系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4700', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='1', `series`='数字系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4700', `charge_w`='120', `wireless_w`='50', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='8', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4350', `charge_w`='120', `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `series`='数字系列', `chip`='骁龙865', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4440', `charge_w`='55', `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='Neo系列', `chip`='天玑9400', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6100', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='Neo系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5160', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='Neo系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='10', `series`='Neo系列', `chip`='天玑9000+', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='Neo系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4700', `charge_w`='80', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='Neo系列', `chip`='骁龙870', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4400', `charge_w`='66', `wireless_w`='0', `screen_size`='6.62英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Z系列', `chip`='第五代骁龙8', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7600', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z11 Turbo'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='Z系列', `chip`='骁龙8s Gen3', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z9 Turbo'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='Z系列', `chip`='天玑8200', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.64英寸', `resolution`='2388×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='Z系列', `chip`='骁龙782G', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.64英寸', `resolution`='2388×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='8', `series`='Z系列', `chip`='骁龙778G Plus', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.64英寸', `resolution`='2388×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='Z系列', `chip`='骁龙778G', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='Z系列', `chip`='骁龙768G', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4400', `charge_w`='55', `wireless_w`='0', `screen_size`='6.58英寸', `resolution`='2408×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `series`='Z系列', `chip`='骁龙765G', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2408×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z1x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='5', `series`='Z系列', `chip`='天玑1000+', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='44', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2408×1080', `refresh_rate`='144', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Z1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='2', `series`='Magic系列', `chip`='骁龙8 Elite Gen5', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='100', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic8 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='2', `series`='Magic系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5600', `charge_w`='100', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='2', `series`='Magic系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5600', `charge_w`='80', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='2800×1280', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic6 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='Magic系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB', `battery_mah`='5100', `charge_w`='66', `wireless_w`='50', `screen_size`='6.81英寸', `resolution`='2848×1312', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='Magic系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4600', `charge_w`='100', `wireless_w`='50', `screen_size`='6.81英寸', `resolution`='2848×1312', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic4 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='8', `series`='Magic系列', `chip`='骁龙888+', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4600', `charge_w`='66', `wireless_w`='50', `screen_size`='6.76英寸', `resolution`='2772×1344', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic3 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='11', `series`='数字系列', `chip`='天玑8500', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 700'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='数字系列', `chip`='天玑8400', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='7000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 600'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='5', `series`='数字系列', `chip`='天玑7400', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='6520', `charge_w`='66', `wireless_w`='0', `screen_size`='6.6英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 600e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='数字系列', `chip`='天玑8300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='6000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 500'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='数字系列', `chip`='天玑8200', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='6000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 400'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='5', `series`='数字系列', `chip`='骁龙8s Gen3', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5200', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 200 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 100 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='5', `series`='数字系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='90', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 90 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='数字系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4800', `charge_w`='66', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2700×1220', `refresh_rate`='120', `rear_camera`='1.6亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 80 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='5', `series`='数字系列', `chip`='天玑8000', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4500', `charge_w`='100', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2652×1200', `refresh_rate`='120', `rear_camera`='5400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 70 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='数字系列', `chip`='骁龙778G+', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4800', `charge_w`='66', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2652×1200', `refresh_rate`='120', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='6', `series`='数字系列', `chip`='骁龙778G', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.72英寸', `resolution`='2676×1236', `refresh_rate`='120', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `series`='数字系列', `chip`='麒麟990 5G', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='40', `wireless_w`='27', `screen_size`='6.57英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='4000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 30 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `series`='数字系列', `chip`='麒麟985 5G', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='4000', `charge_w`='40', `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='4000万超感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='12', `series`='X系列', `chip`='天玑7300', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='7000', `charge_w`='35', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='7', `series`='X系列', `chip`='骁龙6 Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='5800', `charge_w`='35', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2652×1200', `refresh_rate`='120', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `series`='X系列', `chip`='骁龙695', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5100', `charge_w`='40', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='12', `series`='X系列', `chip`='骁龙695', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4800', `charge_w`='66', `wireless_w`='0', `screen_size`='6.81英寸', `resolution`='2388×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='8', `series`='X系列', `chip`='天玑900', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4300', `charge_w`='66', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='5', `series`='X系列', `chip`='麒麟820 5G', `ram`='6GB/8GB', `storage`='64GB/128GB', `battery_mah`='4300', `charge_w`='22', `wireless_w`='0', `screen_size`='6.63英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='4000万高感光'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='Play系列', `chip`='天玑6300', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='35', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play9T'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `series`='Play系列', `chip`='天玑6080', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='6000', `charge_w`='35', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play8T'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='Play系列', `chip`='天玑6020', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='6000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='1600×720', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play7T'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='4', `series`='Play系列', `chip`='天玑700', `ram`='6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='22', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='1600×720', `refresh_rate`='90', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play6T'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='5', `series`='Play系列', `chip`='天玑800U', `ram`='8GB', `storage`='128GB/256GB', `battery_mah`='3800', `charge_w`='66', `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='6', `series`='Play系列', `chip`='天玑800', `ram`='6GB/8GB', `storage`='128GB', `battery_mah`='4300', `charge_w`='22', `wireless_w`='0', `screen_size`='6.81英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='7', `series`='折叠屏', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='512GB/1TB', `battery_mah`='5150', `charge_w`='66', `wireless_w`='50', `screen_size`='外6.43/内7.92', `resolution`='外2720×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic V3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='7', `series`='折叠屏', `chip`='骁龙8 Gen2', `ram`='16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='66', `wireless_w`='0', `screen_size`='外6.43/内7.92', `resolution`='外2720×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic V2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='11', `series`='折叠屏', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='5000', `charge_w`='66', `wireless_w`='0', `screen_size`='外6.45/内7.9', `resolution`='外2560×1080', `refresh_rate`='90', `rear_camera`='5400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic Vs'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='1', `series`='折叠屏', `chip`='骁龙8 Gen1', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4750', `charge_w`='66', `wireless_w`='0', `screen_size`='外6.45/内7.9', `resolution`='外2560×1080', `refresh_rate`='90', `rear_camera`='5400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic V'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Power系列', `chip`='天玑8500 Elite', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='10080', `charge_w`='66', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Power2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='16GB', `storage`='512GB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.85英寸', `resolution`='2800×1260', `refresh_rate`='165', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 11 Air'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `series`='数字系列', `chip`='骁龙8 Elite', `ram`='16GB', `storage`='512GB/1TB', `battery_mah`='6500', `charge_w`='165', `wireless_w`='0', `screen_size`='6.85英寸', `resolution`='2800×1260', `refresh_rate`='165', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 10S Pro / 红魔 10S Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='165', `wireless_w`='0', `screen_size`='6.85英寸', `resolution`='2800×1260', `refresh_rate`='165', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 10 Pro / 红魔 10 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6500', `charge_w`='80', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2480×1116', `refresh_rate`='165', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 9 Pro / 红魔 9 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2480×1116', `refresh_rate`='165', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 8 Pro / 红魔 8 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='数字系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='135', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='165', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `series`='数字系列', `chip`='骁龙888+', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='165', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='腾讯红魔游戏手机 6S Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `series`='数字系列', `chip`='骁龙888', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='165', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='腾讯红魔游戏手机 6 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='0', `screen_size`='6.65英寸', `resolution`='2340×1080', `refresh_rate`='144', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 5S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `series`='数字系列', `chip`='骁龙865', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='55', `wireless_w`='0', `screen_size`='6.65英寸', `resolution`='2340×1080', `refresh_rate`='144', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='5', `series`='R系列', `chip`='骁龙888', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4200', `charge_w`='55', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='144', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='腾讯红魔游戏手机 6R'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='7', `series`='R系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='2400×1080', `refresh_rate`='165', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 7S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='9', `series`='旗舰系列', `chip`='骁龙8 Elite Gen5', `ram`='16GB', `storage`='1TB', `battery_mah`='5500', `charge_w`='100', `wireless_w`='50', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia NaviX Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='Z系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z70 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='Z系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z60 Ultra / nubia Z60 Ultra 领先版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `series`='Z系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z50 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='Z系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB/1TB', `battery_mah`='5000', `charge_w`='80', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='144', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z40 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='5', `series`='Z系列', `chip`='骁龙888', `ram`='8GB/12GB', `storage`='256GB/512GB', `battery_mah`='4200', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='144', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z30 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `series`='折叠屏', `chip`='骁龙8s Gen4', `ram`='12GB', `storage`='256GB/512GB', `battery_mah`='4225', `charge_w`='33', `wireless_w`='0', `screen_size`='内6.85/外3.0', `resolution`='内2970×1188', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Flip 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='5', `series`='折叠屏', `chip`='骁龙7 Gen1', `ram`='8GB/12GB', `storage`='256GB', `battery_mah`='4310', `charge_w`='33', `wireless_w`='0', `screen_size`='内6.9/外2.7', `resolution`='内2790×1188', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Flip'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);

-- ===== 子代理收集·Apple（50 款，来源: apple.com.cn 官方抽查+模型知识(子代理无联网搜索),2026-10-25）=====
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `battery_mah`='3110', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A13 Bionic', `ram`='4GB', `storage`='64GB/128GB/256GB', `screen_size`='6.1英寸', `resolution`='1792×828', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `battery_mah`='3046', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A13 Bionic', `ram`='4GB', `storage`='64GB/256GB/512GB', `screen_size`='5.8英寸', `resolution`='2436×1125', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 11 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `battery_mah`='3969', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A13 Bionic', `ram`='4GB', `storage`='64GB/256GB/512GB', `screen_size`='6.5英寸', `resolution`='2688×1242', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 11 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `battery_mah`='2815', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A14 Bionic', `ram`='4GB', `storage`='64GB/128GB/256GB', `screen_size`='6.1英寸', `resolution`='2532×1170', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `battery_mah`='2815', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A14 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`='2532×1170', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 12 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `battery_mah`='3687', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A14 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.7英寸', `resolution`='2778×1284', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 12 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `battery_mah`='2227', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A14 Bionic', `ram`='4GB', `storage`='64GB/128GB/256GB', `screen_size`='5.4英寸', `resolution`='2340×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 12 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `battery_mah`='3240', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='4GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`='2532×1170', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `battery_mah`='3095', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.1英寸', `resolution`='2532×1170', `refresh_rate`='10-120 LTPO', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 13 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `battery_mah`='4352', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.7英寸', `resolution`='2778×1284', `refresh_rate`='10-120 LTPO', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 13 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='9', `battery_mah`='2406', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='4GB', `storage`='128GB/256GB/512GB', `screen_size`='5.4英寸', `resolution`='2340×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 13 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `battery_mah`='3279', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`='2532×1170', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `battery_mah`='4325', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A15 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.7英寸', `resolution`='2778×1284', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 14 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `battery_mah`='3200', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A16 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.1英寸', `resolution`='2556×1179', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 14 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='9', `battery_mah`='4323', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A16 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.7英寸', `resolution`='2796×1290', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 14 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `battery_mah`='3349', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A16 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`='2556×1179', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `battery_mah`='4383', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A16 Bionic', `ram`='6GB', `storage`='128GB/256GB/512GB', `screen_size`='6.7英寸', `resolution`='2796×1290', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 15 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `battery_mah`='3274', `charge_w`='27', `wireless_w`='15', `series`='iPhone', `chip`='A17 Pro', `ram`='8GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.1英寸', `resolution`='2556×1179', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 15 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='9', `battery_mah`='4422', `charge_w`='27', `wireless_w`='15', `series`='iPhone', `chip`='A17 Pro', `ram`='8GB', `storage`='256GB/512GB/1TB', `screen_size`='6.7英寸', `resolution`='2796×1290', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 15 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='9', `battery_mah`='3561', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A18', `ram`='8GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`='2556×1179', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 16'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='9', `battery_mah`='4389', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A18', `ram`='8GB', `storage`='128GB/256GB/512GB', `screen_size`='6.7英寸', `resolution`='2796×1290', `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 16 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='9', `battery_mah`='3585', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A18 Pro', `ram`='8GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.3英寸', `resolution`='2622×1206', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 16 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='9', `battery_mah`='4685', `charge_w`='20', `wireless_w`='15', `series`='iPhone', `chip`='A18 Pro', `ram`='8GB', `storage`='256GB/512GB/1TB', `screen_size`='6.9英寸', `resolution`='2868×1320', `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 16 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='2', `battery_mah`=NULL, `charge_w`='20', `wireless_w`='0', `series`='iPhone', `chip`='A18', `ram`='8GB', `storage`='128GB/256GB/512GB', `screen_size`='6.1英寸', `resolution`=NULL, `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 16e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A19', `ram`=NULL, `storage`=NULL, `screen_size`='6.3英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A19 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='6.3英寸', `resolution`=NULL, `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 17 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A19 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='6.9英寸', `resolution`=NULL, `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 17 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='2', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A19', `ram`=NULL, `storage`=NULL, `screen_size`='6.1英寸', `resolution`=NULL, `refresh_rate`='60', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 17e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A20 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='6.3英寸', `resolution`=NULL, `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 18 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A20 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='6.9英寸', `resolution`=NULL, `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 18 Pro Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2008', `release_month`='7', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`=NULL, `ram`=NULL, `storage`='8GB/16GB', `screen_size`='3.5英寸', `resolution`='480×320', `refresh_rate`='60', `rear_camera`='200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 3G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2009', `release_month`='6', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`=NULL, `ram`=NULL, `storage`='16GB/32GB', `screen_size`='3.5英寸', `resolution`='480×320', `refresh_rate`='60', `rear_camera`='320万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 3GS'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2011', `release_month`='10', `battery_mah`='1432', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A5', `ram`='512MB', `storage`='8GB/16GB/32GB/64GB', `screen_size`='3.5英寸', `resolution`='960×640', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 4S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`='9', `battery_mah`='1510', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A6', `ram`='1GB', `storage`='16GB/32GB', `screen_size`='4.0英寸', `resolution`='1136×640', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 5c'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`='9', `battery_mah`='1560', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A7', `ram`='1GB', `storage`='16GB/32GB/64GB', `screen_size`='4.0英寸', `resolution`='1136×640', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 5s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='9', `battery_mah`='1715', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A9', `ram`='2GB', `storage`='16GB/64GB/128GB', `screen_size`='4.7英寸', `resolution`='1334×750', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 6s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='9', `battery_mah`='2750', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A9', `ram`='2GB', `storage`='16GB/64GB/128GB', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 6s Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `battery_mah`='1960', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A10 Fusion', `ram`='2GB', `storage`='32GB/128GB/256GB', `screen_size`='4.7英寸', `resolution`='1334×750', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone', `chip`='A10 Fusion', `ram`='3GB', `storage`='32GB/128GB/256GB', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 7 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `battery_mah`='1821', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A11 Bionic', `ram`='2GB', `storage`='64GB/256GB', `screen_size`='4.7英寸', `resolution`='1334×750', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `battery_mah`='2675', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A11 Bionic', `ram`='3GB', `storage`='64GB/256GB', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone 8 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A19 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='6.5英寸', `resolution`=NULL, `refresh_rate`='1-120 LTPO', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone Air'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='9', `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='iPhone', `chip`='A20 Pro', `ram`=NULL, `storage`=NULL, `screen_size`='7.6英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone Duo'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='3', `battery_mah`='1624', `charge_w`=NULL, `wireless_w`='0', `series`='iPhone SE', `chip`='A9', `ram`='2GB', `storage`='16GB/64GB/128GB', `screen_size`='4.0英寸', `resolution`='1136×640', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `battery_mah`='1821', `charge_w`='18', `wireless_w`='7', `series`='iPhone SE', `chip`='A13 Bionic', `ram`='3GB', `storage`='64GB/128GB/256GB', `screen_size`='4.7英寸', `resolution`='1334×750', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone SE (第 2 代)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `battery_mah`='2018', `charge_w`='20', `wireless_w`='7', `series`='iPhone SE', `chip`='A15 Bionic', `ram`='4GB', `storage`='64GB/128GB/256GB', `screen_size`='4.7英寸', `resolution`='1334×750', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone SE (第 3 代)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `battery_mah`='2716', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A11 Bionic', `ram`='3GB', `storage`='64GB/256GB', `screen_size`='5.8英寸', `resolution`='2436×1125', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `battery_mah`='2942', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A12 Bionic', `ram`='3GB', `storage`='64GB/128GB/256GB', `screen_size`='6.1英寸', `resolution`='1792×828', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone XR'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `battery_mah`='2658', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A12 Bionic', `ram`='4GB', `storage`='64GB/256GB/512GB', `screen_size`='5.8英寸', `resolution`='2436×1125', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone XS'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `battery_mah`='3174', `charge_w`='18', `wireless_w`='7', `series`='iPhone', `chip`='A12 Bionic', `ram`='4GB', `storage`='64GB/256GB/512GB', `screen_size`='6.5英寸', `resolution`='2688×1242', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iPhone XS Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='Apple' AND `tenant_id`=0);

-- ===== 子代理收集·三星（11 款，来源: 模型知识(子代理无联网搜索,未逐条核实),2026-10-25）=====
UPDATE `phone_model` SET `release_year`='2021', `release_month`='3', `battery_mah`='4500', `charge_w`='25', `wireless_w`='0', `series`='Galaxy A系列', `chip`='骁龙750G', `ram`='6GB/8GB', `storage`='128GB/256GB', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A52 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `battery_mah`='5000', `charge_w`='25', `wireless_w`='0', `series`='Galaxy A系列', `chip`='Exynos 1280', `ram`='6GB/8GB', `storage`='128GB/256GB', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A53 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='3', `battery_mah`='5000', `charge_w`='25', `wireless_w`='0', `series`='Galaxy A系列', `chip`='Exynos 1380', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A54 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='3', `battery_mah`='5000', `charge_w`='25', `wireless_w`='0', `series`='Galaxy A系列', `chip`='Exynos 1480', `ram`='8GB/12GB', `storage`='128GB/256GB', `screen_size`='6.6英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A55 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `battery_mah`='5000', `charge_w`='45', `wireless_w`='0', `series`='Galaxy A系列', `chip`='Exynos 1580', `ram`='8GB/12GB', `storage`='128GB/256GB', `screen_size`='6.7英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A56 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='8', `battery_mah`='1860', `charge_w`=NULL, `wireless_w`='0', `series`='Galaxy Alpha系列', `chip`='Exynos 5430', `ram`='2GB', `storage`='32GB', `screen_size`='4.7英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Alpha'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `battery_mah`='4380', `charge_w`='15', `wireless_w`=NULL, `series`='Galaxy Fold系列', `chip`='骁龙855', `ram`='12GB', `storage`='256GB/512GB', `screen_size`='7.3英寸', `resolution`='2152×1536', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Fold'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2011', `release_month`='10', `battery_mah`='2500', `charge_w`=NULL, `wireless_w`='0', `series`='Galaxy Note系列', `chip`='Exynos 4210', `ram`='1GB', `storage`='16GB/32GB', `screen_size`='5.3英寸', `resolution`='1280×800', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='9', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `series`='Galaxy Note系列', `chip`='骁龙805', `ram`='3GB', `storage`='32GB/64GB', `screen_size`='5.6英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note Edge'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `battery_mah`='3500', `charge_w`='25', `wireless_w`='12', `series`='Galaxy Note系列', `chip`='骁龙855', `ram`='8GB', `storage`='256GB', `screen_size`='6.3英寸', `resolution`='2280×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `battery_mah`='4300', `charge_w`='45', `wireless_w`='15', `series`='Galaxy Note系列', `chip`='骁龙855', `ram`='12GB', `storage`='256GB/512GB', `screen_size`='6.8英寸', `resolution`='3040×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note10+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);

-- ===== 子代理收集·三星（55 款，来源: 模型知识(子代理无联网搜索,未逐条核实),2026-10-25）=====
UPDATE `phone_model` SET `release_year`='2012', `release_month`='9', `battery_mah`='3100', `charge_w`='5', `wireless_w`='0', `series`='Galaxy Note系列', `chip`='Exynos 4412', `ram`='2GB', `storage`='16GB/32GB/64GB', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `battery_mah`='4300', `charge_w`='25', `wireless_w`='15', `series`='Galaxy Note系列', `chip`='骁龙865+/Exynos 990', `ram`='8GB', `storage`='256GB', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `battery_mah`='4500', `charge_w`='25', `wireless_w`='15', `series`='Galaxy Note系列', `chip`='骁龙865+/Exynos 990', `ram`='8GB/12GB', `storage`='256GB/512GB', `screen_size`='6.9英寸', `resolution`='3088×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note20 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`='9', `battery_mah`='3200', `charge_w`='5', `wireless_w`='0', `series`='Galaxy Note系列', `chip`='骁龙800/Exynos 5420', `ram`='3GB', `storage`='16GB/32GB/64GB', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='1', `battery_mah`='3100', `charge_w`='5', `wireless_w`='0', `series`='Galaxy Note系列', `chip`='Exynos 5260', `ram`='1.5GB', `storage`='16GB', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note3 Lite'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='9', `battery_mah`='3220', `charge_w`='15', `wireless_w`='0', `series`='Galaxy Note系列', `chip`='骁龙805/Exynos 5433', `ram`='3GB', `storage`='16GB/32GB', `screen_size`='5.7英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='8', `battery_mah`='3500', `charge_w`='15', `wireless_w`='7', `series`='Galaxy Note系列', `chip`='骁龙820/Exynos 8890', `ram`='4GB', `storage`='64GB', `screen_size`='5.7英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `battery_mah`='3300', `charge_w`='15', `wireless_w`='9', `series`='Galaxy Note系列', `chip`='骁龙835/Exynos 8895', `ram`='6GB', `storage`='64GB/128GB/256GB', `screen_size`='6.3英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='8', `battery_mah`='4000', `charge_w`='15', `wireless_w`='9', `series`='Galaxy Note系列', `chip`='骁龙845/Exynos 9810', `ram`='6GB/8GB', `storage`='128GB/512GB', `screen_size`='6.4英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Note9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='10', `battery_mah`='2600', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='Exynos 3475', `ram`='1.5GB', `storage`='8GB', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `battery_mah`='2600', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='Exynos 7570', `ram`='2GB', `storage`='16GB', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On5 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`=NULL, `battery_mah`='2600', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='Exynos 7570', `ram`=NULL, `storage`=NULL, `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On5 (2016) 时尚版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`=NULL, `battery_mah`='2600', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='Exynos 7570', `ram`=NULL, `storage`=NULL, `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On5 (2016) 青春版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='10', `battery_mah`='3000', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='骁龙410', `ram`='1.5GB', `storage`='8GB/16GB', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='10', `battery_mah`='3300', `charge_w`='5', `wireless_w`='0', `series`='Galaxy On系列', `chip`='骁龙625', `ram`='3GB', `storage`='16GB/32GB', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`='60', `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy On7 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2010', `release_month`='3', `battery_mah`='1500', `charge_w`='5', `wireless_w`='0', `series`='Galaxy S系列', `chip`='Hummingbird', `ram`='512MB', `storage`='8GB/16GB', `screen_size`='4.0英寸', `resolution`='800×480', `refresh_rate`='60', `rear_camera`='500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2012', `release_month`='9', `battery_mah`='1500', `charge_w`='5', `wireless_w`='0', `series`='Galaxy S系列', `chip`='骁龙S1', `ram`='768MB', `storage`='4GB', `screen_size`='4.0英寸', `resolution`='800×480', `refresh_rate`='60', `rear_camera`='500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S Duos'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='5', `battery_mah`='3000', `charge_w`='15', `wireless_w`='0', `series`='Galaxy S系列', `chip`='骁龙660', `ram`='4GB', `storage`='64GB', `screen_size`='5.8英寸', `resolution`='2280×1080', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S 轻奢版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `battery_mah`='3400', `charge_w`='15', `wireless_w`='12', `series`='Galaxy S系列', `chip`='骁龙855/Exynos 9820', `ram`='8GB', `storage`='128GB/512GB', `screen_size`='6.1英寸', `resolution`='3040×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `battery_mah`='4100', `charge_w`='15', `wireless_w`='12', `series`='Galaxy S系列', `chip`='骁龙855/Exynos 9820', `ram`='8GB/12GB', `storage`='128GB/512GB/1TB', `screen_size`='6.4英寸', `resolution`='3040×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S10+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `battery_mah`='3100', `charge_w`='15', `wireless_w`='12', `series`='Galaxy S系列', `chip`='骁龙855/Exynos 9820', `ram`='6GB/8GB', `storage`='128GB/256GB', `screen_size`='5.8英寸', `resolution`='2280×1080', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S10e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `battery_mah`='4000', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙865/Exynos 990', `ram`='12GB', `storage`='128GB', `screen_size`='6.2英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `battery_mah`='4500', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙865/Exynos 990', `ram`='6GB/8GB', `storage`='128GB/256GB', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S20 FE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `battery_mah`='5000', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙865/Exynos 990', `ram`='12GB/16GB', `storage`='256GB/512GB', `screen_size`='6.9英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S20 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `battery_mah`='4500', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙865/Exynos 990', `ram`='12GB', `storage`='128GB/256GB', `screen_size`='6.7英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S20+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `battery_mah`='4000', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙888/Exynos 2100', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.2英寸', `resolution`='2400×1080', `refresh_rate`='48-120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S21'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='1', `battery_mah`='4500', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙888/Exynos 2100', `ram`='6GB/8GB', `storage`='128GB/256GB', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S21 FE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `battery_mah`='5000', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙888/Exynos 2100', `ram`='12GB/16GB', `storage`='256GB/512GB', `screen_size`='6.8英寸', `resolution`='3200×1440', `refresh_rate`='10-120 LTPO', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S21 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `battery_mah`='4800', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙888/Exynos 2100', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`='48-120', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S21+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `battery_mah`='3700', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 1/Exynos 2200', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.1英寸', `resolution`='2340×1080', `refresh_rate`='48-120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S22'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `battery_mah`='5000', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 1/Exynos 2200', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB/1TB', `screen_size`='6.8英寸', `resolution`='3080×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='1.08亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S22 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `battery_mah`='4500', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 1/Exynos 2200', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.6英寸', `resolution`='2340×1080', `refresh_rate`='48-120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S22+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `battery_mah`='3900', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 2 for Galaxy', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.1英寸', `resolution`='2340×1080', `refresh_rate`='48-120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S23'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='10', `battery_mah`='4500', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 1/Exynos 2200', `ram`='8GB', `storage`='128GB/256GB', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S23 FE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `battery_mah`='5000', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 2 for Galaxy', `ram`='8GB/12GB', `storage`='256GB/512GB/1TB', `screen_size`='6.8英寸', `resolution`='3088×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S23 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `battery_mah`='4700', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 2 for Galaxy', `ram`='8GB', `storage`='256GB/512GB', `screen_size`='6.6英寸', `resolution`='2340×1080', `refresh_rate`='48-120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S23+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='1', `battery_mah`='4000', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 3/Exynos 2400', `ram`='8GB/12GB', `storage`='128GB/256GB', `screen_size`='6.2英寸', `resolution`='2340×1080', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S24'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='1', `battery_mah`='5000', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 3 for Galaxy', `ram`='12GB', `storage`='256GB/512GB/1TB', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S24 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='1', `battery_mah`='4900', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8 Gen 3/Exynos 2400', `ram`='12GB', `storage`='256GB/512GB', `screen_size`='6.7英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S24+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `battery_mah`='4000', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8至尊版 for Galaxy', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `screen_size`='6.2英寸', `resolution`='2340×1080', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S25'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='5', `battery_mah`='3900', `charge_w`='25', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8至尊版 for Galaxy', `ram`='12GB', `storage`='256GB/512GB', `screen_size`='6.7英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S25 Edge'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `battery_mah`='5000', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8至尊版 for Galaxy', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `screen_size`='6.8英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='2亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S25 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `battery_mah`='4900', `charge_w`='45', `wireless_w`='15', `series`='Galaxy S系列', `chip`='骁龙8至尊版 for Galaxy', `ram`='12GB', `storage`='256GB/512GB', `screen_size`='6.7英寸', `resolution`='3120×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S25+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`=NULL, `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='Galaxy S系列', `chip`=NULL, `ram`=NULL, `storage`=NULL, `screen_size`=NULL, `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S26'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`=NULL, `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='Galaxy S系列', `chip`=NULL, `ram`=NULL, `storage`=NULL, `screen_size`=NULL, `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S26 Ultra'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`=NULL, `battery_mah`=NULL, `charge_w`=NULL, `wireless_w`=NULL, `series`='Galaxy S系列', `chip`=NULL, `ram`=NULL, `storage`=NULL, `screen_size`=NULL, `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S26+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='4', `battery_mah`='2550', `charge_w`='15', `wireless_w`='5', `series`='Galaxy S系列', `chip`='Exynos 7420', `ram`='3GB', `storage`='32GB/64GB/128GB', `screen_size`='5.1英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='4', `battery_mah`='2600', `charge_w`='15', `wireless_w`='5', `series`='Galaxy S系列', `chip`='Exynos 7420', `ram`='3GB', `storage`='32GB/64GB/128GB', `screen_size`='5.1英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S6 edge'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='8', `battery_mah`='3000', `charge_w`='15', `wireless_w`='5', `series`='Galaxy S系列', `chip`='Exynos 7420', `ram`='4GB', `storage`='32GB/64GB', `screen_size`='5.7英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S6 edge+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='2', `battery_mah`='3000', `charge_w`='15', `wireless_w`='7', `series`='Galaxy S系列', `chip`='骁龙820/Exynos 8890', `ram`='4GB', `storage`='32GB/64GB', `screen_size`='5.1英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='2', `battery_mah`='3600', `charge_w`='15', `wireless_w`='7', `series`='Galaxy S系列', `chip`='骁龙820/Exynos 8890', `ram`='4GB', `storage`='32GB/64GB', `screen_size`='5.5英寸', `resolution`='2560×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S7 edge'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='3', `battery_mah`='3000', `charge_w`='15', `wireless_w`='9', `series`='Galaxy S系列', `chip`='骁龙835/Exynos 8895', `ram`='4GB', `storage`='64GB', `screen_size`='5.8英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='3', `battery_mah`='3500', `charge_w`='15', `wireless_w`='9', `series`='Galaxy S系列', `chip`='骁龙835/Exynos 8895', `ram`='4GB', `storage`='64GB', `screen_size`='6.2英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S8+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='2', `battery_mah`='3000', `charge_w`='15', `wireless_w`='9', `series`='Galaxy S系列', `chip`='骁龙845/Exynos 9810', `ram`='4GB', `storage`='64GB/128GB/256GB', `screen_size`='5.8英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='2', `battery_mah`='3500', `charge_w`='15', `wireless_w`='9', `series`='Galaxy S系列', `chip`='骁龙845/Exynos 9810', `ram`='6GB', `storage`='64GB/128GB/256GB', `screen_size`='6.2英寸', `resolution`='2960×1440', `refresh_rate`='60', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy S9+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);

-- ===== 子代理收集·GSMArena 镜像批量导入（270 款，来源: github foykes/gsm-arena-dataset 全量转储,数据截至 2021-02,正则抽取+越界拒绝,2026-10-25）=====
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Mediatek MT6763T Helio P23', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='3180', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1440×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Qualcomm SDM665 Snapdragon 665', `ram`='6GB/4GB', `storage`='256GB/128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='4', `chip`='Mediatek MT6771 Helio P60', `ram`='4GB', `storage`='128GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='4', `chip`='Mediatek MT6771 Helio P60', `ram`='4GB', `storage`='128GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A3 / OPPO A3i Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='Qualcomm SM4250 Snapdragon 460', `ram`='8GB/4GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A32'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM6125 Snapdragon 665', `ram`='8GB/6GB/4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A52'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='6GB/4GB', `storage`='128GB', `battery_mah`='4040', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A53'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `chip`='MediaTek MT6833 Dimensity 700 5G', `ram`='6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A55'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='11', `chip`='Qualcomm MSM8940 Snapdragon 435', `ram`='3GB', `storage`='32GB', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A57'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='11', `chip`='Qualcomm SDM450 Snapdragon 450', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='4230', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='8GB/6GB/4GB', `storage`='128GB', `battery_mah`='4040', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A72'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Mediatek MT6765 Helio P35 (12nm)', `ram`='4GB', `storage`='64GB', `battery_mah`='4230', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A7n'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Mediatek MT6771 Helio P60', `ram`='4GB', `storage`='128GB/64GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A7x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Mediatek MT6765V/CB Helio P35 (12nm)', `ram`='4GB', `storage`='128GB', `battery_mah`='4230', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Mediatek MT6771V Helio P70 (12nm)', `ram`='8GB/6GB/4GB', `storage`='128GB', `battery_mah`='4020', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Mediatek MT6771V Helio P70 (12nm)', `ram`='8GB/4GB', `storage`='128GB', `battery_mah`='4025', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A91'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='MediaTek MT6873V Dimensity 800 5G', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A92s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `chip`='Qualcomm SM4350 Snapdragon 480 5G', `ram`='8GB', `storage`='256GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A93'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Mediatek MT6771V Helio P70 (12nm)', `ram`='6GB', `storage`='128GB', `battery_mah`='4020', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO A9x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Ace2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='6', `chip`='Qualcomm SDM845 Snapdragon 845', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='3730', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.42英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='3168×1440', `refresh_rate`='120', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Find X2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='3600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO K1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3765', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO K3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Qualcomm SDM730 Snapdragon 730G', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO K5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4025', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO K7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='11', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO K7x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Mediatek MT6771 Helio P60', `ram`='8GB/4GB', `storage`='128GB', `battery_mah`='3450', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.28英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO R15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB', `storage`='128GB', `battery_mah`='3600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO R15x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='8', `chip`='Qualcomm SDM670 Snapdragon 670', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO R17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='8GB', `storage`='256GB', `battery_mah`='4065', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.6英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno Ace'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Mediatek MT6779 Helio P90', `ram`='8GB/6GB/4GB', `storage`='256GB/128GB', `battery_mah`='4035', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno Z'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Qualcomm SDM730 Snapdragon 730G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Mediatek MT6779 Helio P90', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno2 Z'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Mediatek MT6779 Helio P90', `ram`='8GB', `storage`='128GB', `battery_mah`='4025', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='4400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Mediatek MT6779V Helio P95', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4025', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno3 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Qualcomm SM7125 Snapdragon 720G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno4 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.43英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno4 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='Mediatek MT6889Z Dimensity 1000+ (7nm)', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4350', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='OPPO Reno5 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='OPPO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='12GB/8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.41英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB', `storage`='256GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO 5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Qualcomm SDM845 Snapdragon 845', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2408×1080', `refresh_rate`='144', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Neo3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.41英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.41英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO Pro 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Qualcomm SM7125 Snapdragon 720G', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO U1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `chip`='Qualcomm SM6115 Snapdragon 662', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.51英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO U1x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='MediaTek Dimensity 800U 5G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.58英寸', `resolution`='2408×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='iQOO U3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='iQOO' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SDM712 Snapdragon 712', `ram`='8GB/6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4035', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `chip`='MediaTek Dimensity 800U 5G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q2 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='4GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 Q2i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1600×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 V3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB/6GB/4GB', `storage`='256GB/128GB/64GB', `battery_mah`='3765', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SDM730 Snapdragon 730G', `ram`='8GB/6GB/4GB', `storage`='256GB/128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X2 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X50m'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='MediaTek Dimensity 800U 5G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='Mediatek MT6889Z Dimensity 1000+ (7nm)', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.55英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='真我 X7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='realme' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.89英寸', `resolution`='2256×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo NEX 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.89英寸', `resolution`='2256×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo NEX 3 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Qualcomm SM8250 Snapdragon 865 (7 nm+)', `ram`='12GB/8GB', `storage`='256GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.89英寸', `resolution`='2256×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo NEX 3S 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Mediatek MT6768 Helio P65 (12nm)', `ram`='6GB/4GB', `storage`='256GB/128GB/64GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Qualcomm SDM665 Snapdragon 665', `ram`='8GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S1 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Qualcomm SDM712 Snapdragon 712', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='11', `chip`='MediaTek MT6853V Dimensity 720 5G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo S7e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Qualcomm SDM675 Snapdragon 675', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo U3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Qualcomm SDM670 Snapdragon 670', `ram`='8GB', `storage`='128GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.41英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X23'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X27'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB', `storage`='256GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2460×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X27 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Exynos 980', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4350', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Exynos 980', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4350', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X30 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='6', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4315', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='90', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='Exynos 1080', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='4300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='Exynos 1080', `ram`='12GB', `storage`='256GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.56英寸', `resolution`='2376×1080', `refresh_rate`='120', `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo X60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`=NULL, `chip`='Mediatek MT6582М', `ram`=NULL, `storage`='4GB', `battery_mah`='1700', `charge_w`=NULL, `wireless_w`='0', `screen_size`='4.0英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='6', `chip`='Mediatek MT6762 Helio P22', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.35英寸', `resolution`='1544×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y12'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Mediatek MT6765 Helio P35 (12nm)', `ram`='6GB/4GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.35英寸', `resolution`='1544×720', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='5', `chip`='Mediatek MT6765 Helio P35 (12nm)', `ram`='8GB/4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.47英寸', `resolution`='1560×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `chip`='Qualcomm SM4350 Snapdragon 480 5G', `ram`='6GB/4GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.58英寸', `resolution`='2408×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y31s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='7', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='2GB', `storage`='16GB', `battery_mah`='2300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y35 / vivo Y35m'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='8', `chip`='Qualcomm MSM8939 Snapdragon 615', `ram`='2GB', `storage`='16GB', `battery_mah`='2720', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y36c / vivo Y37'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM6125 Snapdragon 665', `ram`='8GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y37t / vivo Y37+ / vivo Y50 (2025) / vivo Y50i / vivo Y50e / vivo Y50s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='9', `chip`='Mediatek MT6765 Helio P35 (12nm)', `ram`='4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.35英寸', `resolution`='1544×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y3s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM6125 Snapdragon 665', `ram`='8GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Exynos 880', `ram`='6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y51s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='MediaTek MT6853 Dimensity 720 5G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.58英寸', `resolution`='2408×1080', `refresh_rate`='90', `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y52s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='2', `chip`='Qualcomm MSM8917 Snapdragon 425', `ram`='3GB/2GB', `storage`='16GB', `battery_mah`='2730', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y55s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Mediatek MT6768 Helio P65 (12nm)', `ram`='6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y5s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='6', `chip`='Mediatek MT6762 Helio P22', `ram`='4GB/3GB', `storage`='32GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.22英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y81'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='1', `chip`='Qualcomm MSM8953-Pro Snapdragon 626', `ram`='4GB', `storage`='64GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y89'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='11', `chip`='Qualcomm SDM439 Snapdragon 439', `ram`='3GB/2GB', `storage`='32GB/16GB', `battery_mah`='4030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.22英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y91'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='11', `chip`='Qualcomm SDM439 Snapdragon 439', `ram`='4GB', `storage`='64GB', `battery_mah`='4030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y93'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='12', `chip`='Mediatek MT6762 Helio P22', `ram`='4GB', `storage`='128GB', `battery_mah`='4030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.2英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y93s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Mediatek MT6771 Helio P60', `ram`='4GB', `storage`='128GB', `battery_mah`='3315', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y97'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Qualcomm SDM665 Snapdragon 665', `ram`='8GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y9s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='5', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Qualcomm SDM636 Snapdragon 636', `ram`='8GB/4GB', `storage`='128GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z1i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Qualcomm SDM670 Snapdragon 670Qualcomm SDM710 Snapdragon 710', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3315', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Mediatek MT6771 Helio P60', `ram`='6GB', `storage`='128GB', `battery_mah`='3315', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z3i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z3x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Qualcomm SDM712 Snapdragon 712', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Qualcomm SDM675 Snapdragon 675', `ram`='8GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z5i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB/6GB/4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z5x'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='2', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Z6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SDM450 Snapdragon 450', `ram`='4GB/3GB/2GB', `storage`='64GB/32GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='1560×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A20s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='10', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='1.5GB/1GB', `storage`='16GB', `battery_mah`='1900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='4.5英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='10', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='2GB', `storage`='16GB', `battery_mah`='2300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='10', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='2GB', `storage`='16GB', `battery_mah`='2300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A5 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Exynos 9611 (10nm)', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A50s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Exynos 980', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A51 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Qualcomm SDM675 Snapdragon 675', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A6s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='1', `chip`='Qualcomm MSM8939 Snapdragon 615 - A700FDExynos 5430 Octa - A700F', `ram`='2GB', `storage`='16GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='1', `chip`='Qualcomm MSM8939 Snapdragon 615 - A700FDExynos 5430 Octa - A700F', `ram`='2GB', `storage`='16GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A7 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Qualcomm SDM675 Snapdragon 675', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SDM675 Snapdragon 675', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A70s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Exynos 980', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A71 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='7', `chip`='Qualcomm MSM8939 Snapdragon 615', `ram`='2GB', `storage`='32GB', `battery_mah`='3050', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Qualcomm SDM730 Snapdragon 730', `ram`='8GB', `storage`='128GB', `battery_mah`='3700', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A80'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='12', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A8s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.7英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy A90 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='5', `chip`='Qualcomm MSM8952 Snapdragon 617', `ram`='4GB', `storage`='64GB/32GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy C5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='3', `chip`='Qualcomm MSM8953-Pro Snapdragon 626', `ram`='4GB', `storage`='64GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy C5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='5', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='64GB/32GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy C7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='1', `chip`='Qualcomm MSM8953-Pro Snapdragon 626', `ram`='4GB', `storage`='64GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy C7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='10', `chip`='Qualcomm MSM8976 Pro Snapdragon 653', `ram`='6GB', `storage`='64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy C9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='1', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='2GB', `storage`='16GB', `battery_mah`='2950', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy E7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='7', `chip`=NULL, `ram`='1.5GB', `storage`='8GB', `battery_mah`='1800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='3.8英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Folder'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='7', `chip`='Qualcomm MSM8917 Snapdragon 425', `ram`='2GB', `storage`='16GB', `battery_mah`='1950', `charge_w`=NULL, `wireless_w`='0', `screen_size`='3.8英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Folder 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='6', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='2GB', `storage`='16GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy J3 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='6', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='1.5GB', `storage`='16GB/8GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy J5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='6', `chip`='Qualcomm MSM8916 Snapdragon 410', `ram`='1.5GB', `storage`='16GB/8GB', `battery_mah`='2600', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy J5 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='6', `chip`='Qualcomm MSM8939 Snapdragon 615Exynos 7580 Octa', `ram`='1.5GB', `storage`='16GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy J7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='6', `chip`='Qualcomm MSM8939 Snapdragon 615Exynos 7580 Octa', `ram`='1.5GB', `storage`='16GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy J7 (2016)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Exynos 9611 (10nm)', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='6000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy M30s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Qualcomm SM8250 Snapdragon 865+ (7 nm+)', `ram`='8GB', `storage`='256GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.7英寸', `resolution`='2636×1080', `refresh_rate`=NULL, `rear_camera`='1000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Z Flip'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='Qualcomm SM8250 Snapdragon 865+ (7 nm+)', `ram`='8GB', `storage`='256GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.7英寸', `resolution`='2636×1080', `refresh_rate`=NULL, `rear_camera`='1000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Z Flip 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='8', `chip`='Qualcomm SM8250 Snapdragon 865+ (7 nm+)', `ram`='12GB', `storage`='512GB/256GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='7.6英寸', `resolution`='2208×1768', `refresh_rate`='120', `rear_camera`='1000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Galaxy Z Fold2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='三星' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='3', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='64GB', `battery_mah`='3630', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia M2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='7', `chip`='Mediatek MT6755 Helio P10', `ram`='3GB', `storage`='64GB/32GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia N1'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='3', `chip`='Mediatek MT6750', `ram`='4GB', `storage`='64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia N2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.01英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia N3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Qualcomm SM7250 Snapdragon 765G', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='5100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.65英寸', `resolution`='2340×1080', `refresh_rate`='144', `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Play'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.01英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia V18'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`=NULL, `release_month`=NULL, `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='8GB/6GB', `storage`='512GB/256GB/128GB/64GB', `battery_mah`='3800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2014', `release_month`='3', `chip`='Qualcomm MSM8974AB/ MSM8974AC Snapdragon 801', `ram`='3GB/2GB', `storage`='128GB/64GB/32GB', `battery_mah`='4250', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia X6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='6', `chip`='Qualcomm MSM8996 Snapdragon 820', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z11'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='6', `chip`='Qualcomm MSM8976 Snapdragon 652', `ram`='4GB', `storage`='64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z11 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='4', `chip`='Qualcomm MSM8952 Snapdragon 617', `ram`='3GB', `storage`='64GB', `battery_mah`='2800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z11 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='10', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='128GB/64GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='2300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z11 miniS'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='6', `chip`='Qualcomm MSM8998 Snapdragon 835', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='3200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z17'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='4', `chip`='Qualcomm MSM8976 Snapdragon 652 - Standard editionQualcomm MSM8976 Pro Snapdragon 653 - High edition', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='2950', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z17 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='10', `chip`='Qualcomm MSM8976 Pro Snapdragon 653', `ram`='6GB', `storage`='64GB', `battery_mah`='3200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z17 miniS'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='10', `chip`='Qualcomm MSM8998 Snapdragon 835', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='3100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.73英寸', `resolution`='2040×1080', `refresh_rate`=NULL, `rear_camera`='500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z17S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Qualcomm SDM845 Snapdragon 845', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='3450', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.99英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z18'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='4', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3450', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z18 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='8', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='8GB/6GB', `storage`='512GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.42英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='5', `chip`='Qualcomm MSM8994 Snapdragon 810', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='3', `chip`='Qualcomm MSM8994 Snapdragon 810', `ram`='3GB', `storage`='16GB', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z9 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='3', `chip`='Qualcomm MSM8939 Snapdragon 615', `ram`='2GB', `storage`='16GB', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='nubia Z9 mini'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='努比亚' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='10', `chip`='Kirin 970', `ram`='4GB', `storage`='64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.9英寸', `resolution`='2560×1440', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='10', `chip`='Kirin 970', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 10 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Kirin 980', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2244×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.39英寸', `resolution`='3120×1440', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 20 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='7.2英寸', `resolution`='2244×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 20 X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Kirin 990 5G (7 nm+)', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.62英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Kirin 990 5G (7 nm+)', `ram`='8GB', `storage`='512GB/256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.53英寸', `resolution`='2400×1176', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 30 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `chip`='Kirin 990E 5G (7 nm+)', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.53英寸', `resolution`='2400×1176', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 30E Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='10', `chip`='Kirin 9000 5G', `ram`='12GB', `storage`='256GB', `battery_mah`='4400', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.76英寸', `resolution`='2772×1344', `refresh_rate`='90', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 40 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='11', `chip`='Kirin 950', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='11', `chip`='Kirin 960', `ram`='4GB', `storage`='64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.9英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='11', `chip`='Kirin 960', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='2560×1440', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate 9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `chip`='Kirin 980', `ram`='8GB', `storage`='512GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='8.0英寸', `resolution`='2480×2200', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI Mate X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='2', `chip`='Kirin 960', `ram`='4GB', `storage`='64GB/32GB', `battery_mah`='3200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.1英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='2', `chip`='Kirin 960', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='2560×1440', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P10 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Kirin 970', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.8英寸', `resolution`='2240×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Kirin 970', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.1英寸', `resolution`='2240×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P20 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3650', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.1英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='512GB/256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.47英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P30 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Kirin 990 5G (7 nm+)', `ram`='8GB', `storage`='512GB/256GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.58英寸', `resolution`='2640×1200', `refresh_rate`='90', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P40 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='4', `chip`='Kirin 955', `ram`='4GB/3GB', `storage`='64GB/32GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='4', `chip`='Kirin 955', `ram`='4GB', `storage`='64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI P9 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='3GB', `storage`='32GB', `battery_mah`='3020', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='5', `chip`='Kirin 659', `ram`='4GB', `storage`='64GB', `battery_mah`='2950', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='5', `chip`='Kirin 659', `ram`='4GB', `storage`='128GB/64GB', `battery_mah`='3340', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 2 Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='12', `chip`='Kirin 960', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3340', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.0英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 2s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Kirin 970', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Kirin 710', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3340', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 3i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='12', `chip`='Kirin 970', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2310×1080', `refresh_rate`=NULL, `rear_camera`='2500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='3', `chip`='Kirin 710', `ram`='8GB/4GB', `storage`='128GB', `battery_mah`='3340', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.15英寸', `resolution`='2312×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 4e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='6', `chip`='Kirin 810', `ram`='8GB', `storage`='128GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='6', `chip`='Kirin 980', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 5 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='6', `chip`='Kirin 710F', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2310×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 5i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Kirin 810', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 5i Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Kirin 810', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 5z'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Kirin 990', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='12', `chip`='Kirin 810', `ram`='8GB', `storage`='128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2310×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 6 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Kirin 985 5G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 7 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Kirin 820 5G (7nm)', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 7 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='12', `chip`='Kirin 985 5G', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.72英寸', `resolution`='2676×1236', `refresh_rate`='120', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 8 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='11', `chip`='MediaTek MT6853 Dimensity 720 5G - standard modelMediaTek Dimensity 800U 5G - premium model', `ram`='8GB', `storage`='128GB', `battery_mah`='3800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.53英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='HUAWEI nova 8 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='4', `chip`='Kirin 930/ Kirin 935', `ram`='3GB', `storage`='64GB/16GB', `battery_mah`='2680', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为 P8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='4', `chip`='Kirin 935', `ram`='3GB', `storage`='64GB', `battery_mah`='4360', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.8英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='华为 P8 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='华为' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2012', `release_month`='8', `chip`='Qualcomm MSM8260 Snapdragon S3', `ram`='1GB', `storage`='4GB', `battery_mah`='1930', `charge_w`=NULL, `wireless_w`='0', `screen_size`='4.0英寸', `resolution`=NULL, `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 1S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`=NULL, `chip`='Xiaolong S4 Pro', `ram`='1GB', `storage`='16GB', `battery_mah`='2030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='4.5英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 2A'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`='4', `chip`='Qualcomm APQ8064 Snapdragon S4 Pro', `ram`='2GB', `storage`='32GB/16GB', `battery_mah`='2000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='4.3英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 2S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='2', `chip`='Qualcomm MSM8992 Snapdragon 808', `ram`='3GB', `storage`='64GB', `battery_mah`='3260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 4S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='9', `chip`='Qualcomm MSM8992 Snapdragon 808', `ram`='3GB/2GB', `storage`='32GB/16GB', `battery_mah`='3080', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 4c'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='2', `chip`='Qualcomm MSM8996 Snapdragon 820', `ram`='4GB/3GB', `storage`='128GB/64GB/32GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.15英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 5'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='2', `chip`='Xiaomi Surge S1', `ram`='3GB', `storage`='64GB', `battery_mah`='2860', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.15英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 5c'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `chip`='Qualcomm MSM8996 Snapdragon 821', `ram`='4GB/3GB', `storage`='128GB/64GB', `battery_mah`='3200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.15英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 5s'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='9', `chip`='Qualcomm MSM8996 Snapdragon 821', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 5s Plus'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='4', `chip`='Qualcomm MSM8998 Snapdragon 835', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3350', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.15英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 6'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='5', `chip`='Qualcomm SDM845 Snapdragon 845', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.21英寸', `resolution`='2248×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='5', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3120', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.88英寸', `resolution`='2244×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 8 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3300', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='512GB/256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `chip`='Qualcomm SDM712 Snapdragon 712', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3070', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.97英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 9 SE'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Qualcomm SDM710 Snapdragon 710', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='4030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 CC9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Qualcomm SDM730 Snapdragon 730G', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='5260', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.47英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 CC9 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Qualcomm SDM665 Snapdragon 665', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4030', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.01英寸', `resolution`='1560×720', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 CC9e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='10', `chip`='Qualcomm MSM8996 Snapdragon 821', `ram`='6GB/4GB', `storage`='256GB/128GB', `battery_mah`='4400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.4英寸', `resolution`='2040×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 MIX'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `chip`='Qualcomm MSM8998 Snapdragon 835', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.99英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 MIX 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='3', `chip`='Qualcomm SDM845 Snapdragon 845', `ram`='8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='5.99英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='500万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 MIX 2S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 MIX 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='2', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3800', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 MIX 3 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='5', `chip`='Qualcomm MSM8956 Snapdragon 650Qualcomm MSM8976 Snapdragon 652 - Prime model', `ram`='4GB/3GB/2GB', `storage`='128GB/64GB/32GB/16GB', `battery_mah`='4850', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='5', `chip`='Qualcomm MSM8953 Snapdragon 625', `ram`='4GB', `storage`='128GB/64GB/32GB', `battery_mah`='5300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.44英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Max 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Qualcomm SDM636 Snapdragon 636', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='5500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.9英寸', `resolution`='2160×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Max 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='1', `chip`='Qualcomm MSM8974AC Snapdragon 801', `ram`='3GB', `storage`='64GB/16GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Note'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='10', `chip`='Qualcomm MSM8996 Snapdragon 821', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4070', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='2250万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Note 2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='9', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB', `storage`='128GB/64GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.5英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Note 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='12', `chip`='Mediatek MT6765 Helio P35 (12nm)', `ram`='4GB', `storage`='64GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.84英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 Play'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Qualcomm SM8150 Snapdragon 855', `ram`='12GB/8GB/6GB', `storage`='256GB/128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.65英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Qualcomm SM8150 Snapdragon 855+', `ram`='12GB/8GB', `storage`='256GB/128GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.65英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='红魔 3S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红魔' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='5', `chip`='Kirin 970', `ram`='8GB/6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.84英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='2400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 20'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='5', `chip`='Kirin 980', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 20 PRO'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Kirin 810', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.26英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 20S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='4', `chip`='Kirin 710', `ram`='6GB/4GB', `storage`='256GB/128GB/64GB', `battery_mah`='3400', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.21英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='3200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 20i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Kirin 990 5G (7 nm+)', `ram`='12GB/8GB', `storage`='256GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.57英寸', `resolution`='2340×1080', `refresh_rate`='90', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 30 Pro+'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='3', `chip`='Kirin 820 5G (7nm)', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 30S'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2013', `release_month`='12', `chip`='Mediatek MT6582', `ram`='2GB', `storage`='8GB', `battery_mah`='2300', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.0英寸', `resolution`='1280×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 3C'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='6', `chip`='Kirin 935', `ram`='3GB', `storage`='64GB/32GB/16GB', `battery_mah`='3100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='2000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 7'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2015', `release_month`='8', `chip`='Qualcomm MSM8939v2 Snapdragon 616', `ram`='3GB/2GB', `storage`='32GB/16GB', `battery_mah`='3100', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 7i'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='7', `chip`='Kirin 950', `ram`='4GB', `storage`='64GB/32GB', `battery_mah`='3000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.2英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Kirin 710', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.5英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 8X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='9', `chip`='Qualcomm SDM660 Snapdragon 660', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='7.12英寸', `resolution`='2244×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 8X Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2017', `release_month`='6', `chip`='Kirin 960', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='3200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.15英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 9'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='10', `chip`='Kirin 710F', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 9X'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='7', `chip`='Kirin 810', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.59英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 9X PRO'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='12', `chip`='Kirin 950', `ram`='4GB', `storage`='64GB', `battery_mah`='2900', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.09英寸', `resolution`='2560×1440', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='10', `chip`='Kirin 980', `ram`='8GB/6GB', `storage`='256GB/128GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Magic2'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Kirin 970', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.95英寸', `resolution`='2220×1080', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Note10'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='8', `chip`='Kirin 955', `ram`='4GB', `storage`='128GB/64GB/32GB', `battery_mah`='4500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.6英寸', `resolution`='2560×1440', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Note8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2018', `release_month`='7', `chip`='Kirin 970', `ram`='6GB/4GB', `storage`='64GB', `battery_mah`='3750', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2340×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Kirin 710F', `ram`='6GB/4GB', `storage`='128GB/64GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='1560×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='9', `chip`='Mediatek MT6762R Helio P22', `ram`='3GB/2GB', `storage`='64GB/32GB', `battery_mah`='3020', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.71英寸', `resolution`='1520×720', `refresh_rate`=NULL, `rear_camera`='1300万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play3e'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='6', `chip`='Kirin 990', `ram`='8GB', `storage`='128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play4 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Kirin 710A', `ram`='6GB', `storage`='128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.39英寸', `resolution`='1560×720', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play4T'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='4', `chip`='Kirin 810', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.3英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`='1600万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 Play4T Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Kirin 990', `ram`='8GB/6GB', `storage`='128GB', `battery_mah`='4200', `charge_w`=NULL, `wireless_w`='0', `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 V30'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2019', `release_month`='11', `chip`='Kirin 990 5G (7 nm+)', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4100', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.57英寸', `resolution`='2400×1080', `refresh_rate`=NULL, `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 V30 PRO'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='1', `chip`='Mediatek MT6889Z Dimensity 1000+ (7nm)', `ram`='8GB', `storage`='256GB/128GB', `battery_mah`='4000', `charge_w`=NULL, `wireless_w`=NULL, `screen_size`='6.72英寸', `resolution`='2676×1236', `refresh_rate`='120', `rear_camera`=NULL
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 V40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2016', `release_month`='5', `chip`='Kirin 950 (KNT-AL10, KNT-TL10)Kirin 955 (KNT-AL20)', `ram`='4GB', `storage`='64GB/32GB', `battery_mah`='3500', `charge_w`=NULL, `wireless_w`='0', `screen_size`='5.7英寸', `resolution`='1920×1080', `refresh_rate`=NULL, `rear_camera`='1200万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 V8'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2020', `release_month`='7', `chip`='MediaTek MT6873 Dimensity 800 5G', `ram`='8GB/6GB', `storage`='128GB/64GB', `battery_mah`='5000', `charge_w`=NULL, `wireless_w`='0', `screen_size`='7.09英寸', `resolution`='2280×1080', `refresh_rate`=NULL, `rear_camera`='800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='荣耀 X10 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='荣耀' AND `tenant_id`=0);
