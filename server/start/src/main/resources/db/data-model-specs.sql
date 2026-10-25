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
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4610', `charge_w`='90', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2670×1200', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 14'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14 Pro (`shennong`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 14 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='数字系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='4820', `charge_w`='120', `wireless_w`='50', `screen_size`='6.73英寸', `resolution`='3200×1440', `refresh_rate`='1-120 LTPO', `rear_camera`='5000万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 14 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5400万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Xiaomi 13'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='小米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='数字系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='4500', `charge_w`='67', `wireless_w`='50', `screen_size`='6.36英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='5400万徕卡'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='小米 13'
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
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='K系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='6550', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI K80'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='11', `series`='K系列', `chip`='骁龙8 Elite', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.78英寸', `resolution`='2800×1260', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI K80 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='K系列', `chip`='骁龙8 Gen2', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='11', `series`='K系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='K系列', `chip`='骁龙8+ Gen1', `ram`='8GB/12GB/16GB', `storage`='128GB/256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='67', `wireless_w`='30', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K60'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='12', `series`='K系列', `chip`='骁龙8 Gen2', `ram`='8GB/12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='5400万IMX800'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K60 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='K系列', `chip`='天玑8100', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5500', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='3', `series`='K系列', `chip`='天玑9000', `ram`='8GB/12GB', `storage`='128GB/256GB/512GB', `battery_mah`='5000', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='3200×1440', `refresh_rate`='120', `rear_camera`='1亿主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='2', `series`='K系列', `chip`='骁龙870', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4520', `charge_w`='33', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='4800万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='2', `series`='K系列', `chip`='骁龙888', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4520', `charge_w`='33', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40 Pro'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
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
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='3', `series`='数字系列', `chip`='骁龙6s Gen3', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='7000', `charge_w`='33', `wireless_w`='0', `screen_size`='6.79英寸', `resolution`='2460×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI 15'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='12', `series`='数字系列', `chip`='Helio G85', `ram`='4GB/6GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='18', `wireless_w`='0', `screen_size`='6.74英寸', `resolution`='1600×720', `refresh_rate`='60', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi 13C 5G / Redmi 13R 5G'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='8', `series`='数字系列', `chip`='骁龙4 Gen2', `ram`='4GB/8GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='18', `wireless_w`='0', `screen_size`='6.79英寸', `resolution`='2460×1080', `refresh_rate`='90', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi 12 5G (`sky`)'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2026', `release_month`='1', `series`='Turbo系列', `chip`='天玑9500s', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='9000', `charge_w`='100', `wireless_w`='0', `screen_size`='6.83英寸', `resolution`='2712×1220', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Turbo 5 Max'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2025', `release_month`='1', `series`='Turbo系列', `chip`='天玑8400-Ultra', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='7000', `charge_w`='90', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='REDMI Turbo 4'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='4', `series`='Turbo系列', `chip`='骁龙8s Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5000', `charge_w`='90', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='120', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi Turbo 3'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2024', `release_month`='7', `series`='电竞系列', `chip`='骁龙8 Gen3', `ram`='12GB/16GB', `storage`='256GB/512GB/1TB', `battery_mah`='5500', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2712×1220', `refresh_rate`='144', `rear_camera`='5000万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K70 电竞版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2022', `release_month`='2', `series`='电竞系列', `chip`='骁龙8 Gen1', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='4700', `charge_w`='120', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K50 电竞版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2021', `release_month`='4', `series`='电竞系列', `chip`='天玑1200', `ram`='6GB/8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5065', `charge_w`='67', `wireless_w`='0', `screen_size`='6.67英寸', `resolution`='2400×1080', `refresh_rate`='120', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Redmi K40 游戏增强版'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='红米' AND `tenant_id`=0);
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
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='Y100'
  AND `brand_id`=(SELECT `id` FROM `brand` WHERE `name`='vivo' AND `tenant_id`=0);
UPDATE `phone_model` SET `release_year`='2023', `release_month`='2', `series`='Y系列', `chip`='天玑7020', `ram`='8GB/12GB', `storage`='128GB/256GB', `battery_mah`='5000', `charge_w`='44', `wireless_w`='0', `screen_size`='6.38英寸', `resolution`='2400×1080', `refresh_rate`='90', `rear_camera`='6400万主摄'
WHERE `chip` IS NULL AND `tenant_id`=0 AND `name`='vivo Y100'
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
