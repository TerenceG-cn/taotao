package com.taotao.controller;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/item")
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;


    @RequestMapping("/{itemId}")
    @ResponseBody
    public TbItem findItem(@PathVariable Long itemId){
        TbItem result = itemService.findTbItemById(itemId);
        return result;
    }

    @RequestMapping("/showItemPage")
    @ResponseBody
    public LayuiResult showItemPage(Integer page,Integer limit){
        LayuiResult result=itemService.findTbItemByPage(page,limit);
        return result;
    }

    @RequestMapping("/itemDelete")
    @ResponseBody
    public TaotaoResult itemDelete(@RequestBody List<TbItem> tbItem){
        /**
         * 写web页面的人 牛逼的话 他可以这样做
         * 传入两个参数过来
         *  1.需要删除的 商品的id 集合  List<Integer> ids
         *  2.type  1表示删除 2表示上架 3表示下架
         *      itemServie.updateItem(type,ids);
         *          itemMapper.updateItem()
         *              动态sql 完成修改  update tbitem set status = 2（删除）1（上架）0（下架） where id = xx
         *  没有web程序员配合我们
         *      itemService.deleteItem(tbItem);
         */
        Date date = new Date();
        TaotaoResult result =  itemService.updateItem(tbItem,2,date);
        return result;
    }

    /*上架*/
    @RequestMapping("/commodityUpperShelves")
    @ResponseBody
    public TaotaoResult commodityUpperShelves(@RequestBody List<TbItem> tbItem){
        Date date = new Date();
        TaotaoResult result =  itemService.updateItem(tbItem,1,date);
        return result;
    }
    /*下架*/
    @RequestMapping("/commodityLowerShelves")
    @ResponseBody
    public TaotaoResult commodityLowerShelves(@RequestBody List<TbItem> tbItem){
        Date date = new Date();
        TaotaoResult result =  itemService.updateItem(tbItem,0,date);
        return result;
    }

    @RequestMapping("/searchItem")
    @ResponseBody
    public LayuiResult searchItem(Integer page,Integer limit,String title,Integer priceMin,Integer priceMax,Long cId){
        LayuiResult result = itemService.getLikeItem(page,limit,title,priceMin,priceMax,cId);
        return result;
    }


}
