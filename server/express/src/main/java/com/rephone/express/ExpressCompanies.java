package com.rephone.express;

import com.rephone.express.model.ExpressCompany;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 快递100 快递公司编码字典。
 *
 * <p>取自官方《快递公司编码表》(api.kuaidi100.com/manager/openapi/download/kdbm.do，共 3077 家)，
 * 这里只保留国内个人寄件常用的一批，避免把整表塞进小程序选择器。
 * needPhone：官方实时查询文档明确「顺丰速运、顺丰快运、中通快递必填」收寄件人电话。
 */
public final class ExpressCompanies {

    private static final List<ExpressCompany> ALL = List.of(
            new ExpressCompany("shunfeng", "顺丰速运", true),
            new ExpressCompany("zhongtong", "中通快递", true),
            new ExpressCompany("yuantong", "圆通速递", false),
            new ExpressCompany("shentong", "申通快递", false),
            new ExpressCompany("yunda", "韵达快递", false),
            new ExpressCompany("jtexpress", "极兔速递", false),
            new ExpressCompany("jd", "京东物流", false),
            new ExpressCompany("ems", "EMS", false),
            new ExpressCompany("youzhengguonei", "邮政快递包裹", false),
            new ExpressCompany("youzhengbk", "邮政标准快递", false),
            new ExpressCompany("debangkuaidi", "德邦快递", false),
            new ExpressCompany("debangwuliu", "德邦物流", false),
            new ExpressCompany("sxjdfreight", "顺心捷达", false),
            new ExpressCompany("zhaijisong", "宅急送", false),
            new ExpressCompany("huitongkuaidi", "百世快递", false),
            new ExpressCompany("youshuwuliu", "优速快递", false),
            new ExpressCompany("suning", "苏宁物流", false),
            new ExpressCompany("danniao", "菜鸟速递", false),
            new ExpressCompany("kuayue", "跨越速运", false),
            new ExpressCompany("yimidida", "壹米滴答", false),
            new ExpressCompany("shunfengkuaiyun", "顺丰快运", true),
            new ExpressCompany("zhongtongkuaiyun", "中通快运", false));

    /** 历史/口语叫法 → 官方编码，兼容早期订单里写死的展示名。 */
    private static final Map<String, String> NAME_ALIASES = Map.ofEntries(
            Map.entry("顺丰快递", "shunfeng"),
            Map.entry("顺丰", "shunfeng"),
            Map.entry("中通速递", "zhongtong"),
            Map.entry("中通", "zhongtong"),
            Map.entry("圆通快递", "yuantong"),
            Map.entry("圆通", "yuantong"),
            Map.entry("申通", "shentong"),
            Map.entry("韵达速递", "yunda"),
            Map.entry("韵达", "yunda"),
            Map.entry("京东快递", "jd"),
            Map.entry("京东", "jd"),
            Map.entry("极兔", "jtexpress"),
            Map.entry("邮政快递", "youzhengguonei"),
            Map.entry("邮政EMS", "ems"),
            Map.entry("百世汇通", "huitongkuaidi"),
            Map.entry("德邦", "debangkuaidi"),
            Map.entry("优速", "youshuwuliu"));

    private ExpressCompanies() {
    }

    /** 全部可选快递公司，顺序即小程序选择器顺序。 */
    public static List<ExpressCompany> all() {
        return ALL;
    }

    /** 按快递100 编码精确查找。 */
    public static Optional<ExpressCompany> byCom(String com) {
        if (com == null || com.isBlank()) {
            return Optional.empty();
        }
        String key = com.trim().toLowerCase();
        return ALL.stream().filter(c -> c.com().equals(key)).findFirst();
    }

    /** 按展示名查找（先精确匹配官方名，再落到常见别名）。 */
    public static Optional<ExpressCompany> byName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String key = name.trim();
        Optional<ExpressCompany> exact = ALL.stream().filter(c -> c.name().equals(key)).findFirst();
        if (exact.isPresent()) {
            return exact;
        }
        return byCom(NAME_ALIASES.get(key));
    }

    /** 展示名兜底：查不到就原样返回编码，避免前端出现空白。 */
    public static String nameOf(String com) {
        return byCom(com).map(ExpressCompany::name).orElse(com == null ? "" : com);
    }
}
