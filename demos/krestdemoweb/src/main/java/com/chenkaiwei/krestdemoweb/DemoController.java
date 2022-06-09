package com.chenkaiwei.krestdemoweb;

import cn.hutool.http.HttpUtil;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("index")//页面的url地址
    public String getindex(Model model)//对应函数
    {
        return "index";//与templates中index.html对应
    }
}
