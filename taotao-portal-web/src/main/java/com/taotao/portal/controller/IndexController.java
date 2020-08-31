package com.taotao.portal.controller;

import com.taotao.pojo.ItemCatResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.Ad1Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private ContentService contentService;
    @Autowired
    private ItemCatService itemCatService;
    @Value("${AD1_CID}")
    private Long AD1_CID;
    @Value("${AD1_HEIGHT}")
    private Integer AD1_HEIGHT;
    @Value("${AD1_WIDTH}")
    private Integer AD1_WIDTH;
    @Value("${AD1_HEIGHT_B}")
    private Integer AD1_HEIGHT_B;
    @Value("${AD1_WIDTH_B}")
    private Integer AD1_WIDTH_B;
    /**
     * 我们的页面请求 如果是
     * http://localhost:8082/index.jsp
     *   <welcome-file-list>
             <welcome-file>index.jsp</welcome-file>
             <welcome-file>index.html</welcome-file>
         </welcome-file-list>
        http://localhost:8082/index.html
     //只要我们把一个集合对象 变成json格式 并且把他通过Model存入到域对象中 发送到index.jsp页面 就可以展示出我们的图片的了
     先要调用Content的接口 才能得到 所有的内容信息集合对象
     遍历集合对象 组装成为 想要的json格式 通过域发送过去 才能展示数据
     */
    @RequestMapping("/index")
    public String showIndex(Model model){
        List<TbContent> result = contentService.getContentAll(AD1_CID);
        //返回给页面的json格式的对象
        List<Ad1Node> ad1List = new ArrayList<>();
        //遍历集合 得到对象 组装数据
        for (TbContent tbContent : result) {
            Ad1Node node = new Ad1Node();
            node.setAlt(tbContent.getTitle());
            node.setHeight(AD1_HEIGHT);
            node.setHeightB(AD1_HEIGHT_B);
            node.setWidth(AD1_WIDTH);
            node.setWidthB(AD1_WIDTH_B);
            node.setHref(tbContent.getUrl());
            node.setSrc(tbContent.getPic());
            node.setSrcB(tbContent.getPic2());
            //添加到列表
            ad1List.add(node);
        }
        //吧集合对象变成json格式 就可以搞定了

        model.addAttribute("ad1", JsonUtils.objectToJson(ad1List));

        return "index";
    }

    @RequestMapping(value = "/item/cat/itemcat/all")
    @ResponseBody
    public String queryAll(String callback) {
        //因为这里跨域了
        ItemCatResult result = itemCatService.getItemCatAll(0L);
        String json = callback + "(" + JsonUtils.objectToJson(result)+");" ;
        return json;

    }
}
