package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.express.ExpressCompanies;
import com.rephone.express.model.ExpressCompany;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 快递公共字典（用户端，需登录）。 */
@RestController
@RequestMapping("/api/wx/express")
public class WxExpressController {

    /** 快递公司列表：小程序填运单号时的选择器数据源，编码与后端查询共用一份字典。 */
    @GetMapping("/companies")
    public R<List<ExpressCompany>> companies() {
        return R.ok(ExpressCompanies.all());
    }
}
