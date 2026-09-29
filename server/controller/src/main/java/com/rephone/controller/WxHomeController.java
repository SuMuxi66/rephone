package com.rephone.controller;

import com.rephone.common.result.R;
import com.rephone.pojo.dto.HomeModelItem;
import com.rephone.service.HomeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 首页数据（需登录）：热门机型来自机型库实时数据。 */
@RestController
@RequestMapping("/api/wx/home")
public class WxHomeController {

    private final HomeService homeService;

    public WxHomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public R<List<HomeModelItem>> hotModels() {
        return R.ok(homeService.hotModels());
    }
}
