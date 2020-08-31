package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping("/")
    public String showIndex(){
        return "index";
    }

    /**
     * /{page}他代表的意思是
     * http://localhost:8081/参数
     * 这个参数 名称为 page=具体参数
     * @PathVariable 从url路径之下 取
     * http://localhost:8081/item-add
     *  page = item-add
     */
    @RequestMapping("/{page}")
    public String showPage(@PathVariable String page){
        return page;
    }
}
