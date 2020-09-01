package com.taotao.service.impl;

import com.taotao.mapper.TbItemParamMapper;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Override
    public TbItem findTbItemById(Long itemId) {
        TbItem tbItem = tbItemMapper.findTbItemById(itemId);
        return tbItem;
    }

    @Override
    public LayuiResult findTbItemByPage(int page, int limit) {
        LayuiResult result = new LayuiResult();
        result.setCode(0);
        result.setMsg("");
        int count = tbItemMapper.findTbItemByCount();
        result.setCount(count);
        List<TbItem> data = tbItemMapper.findTbItemByPage((page-1)*limit,limit);
        result.setData(data);
        return result;
    }

    @Override
    public TaotaoResult updateItem(List<TbItem> tbItems, int type, Date date) {
        /**
         * 1.我们不知道 这个list集合 是有一个 还是多个  但是我们是要根据商品id来做修改
         */

        if(tbItems.size()<=0){
            //new TaotaoResult();
            return TaotaoResult.build(500,"请先勾选,在操作",null);
        }
        //需要修改的商品id
        List<Long> ids = new ArrayList<Long>();
        //吧需要修改的商品id 加入到list集合里面去
        for (TbItem tbItem: tbItems) {
            ids.add(tbItem.getId());
        }
        int count = tbItemMapper.updateItemByIds(ids,type,date);
        //count大于0才代表 我们修改了数据库里面的数据
        if(count>0&&type==0){
            return TaotaoResult.build(200,"商品下架成功",null);
        }else if(count>0&&type==1){
            return TaotaoResult.build(200,"商品上架成功",null);
        }else if(count>0&&type==2){
            return TaotaoResult.build(200,"商品删除成功",null);
        }
        return TaotaoResult.build(500,"商品修改",null);
    }

    @Override
    public LayuiResult getLikeItem(Integer page, Integer limit, String title, Integer priceMin, Integer priceMax, Long cId) {
        if(priceMin == null){
            priceMin = 0;
        }
        if(priceMax == null){
            priceMax = 100000000;
        }
        /**
         * 组装一下数据  然后 调用一个dao 得到结果集
         *
         */
        LayuiResult result = new LayuiResult();
        result.setCode(0);
        result.setMsg("");
        int count = tbItemMapper.findTbItemByLikeConut(title,priceMin,priceMax,cId);
        //设置查询结果集的数量
        result.setCount(count);
        List<TbItem> data = tbItemMapper.findTbItemByLike(title,priceMin,priceMax,cId,(page-1)*limit,limit);
        result.setData(data);

        return result;
    }

    @Override
    public PictureResult addPicture(String fileNmae, byte[] bytes) {
        //随机生成一个字符串   本身的名字 只要.jpg 随机字符串.jpg
        String filename = IDUtils.genImageName()+fileNmae.substring(fileNmae.lastIndexOf("."));
        //上传日期
        Date date=new Date();
        SimpleDateFormat dataFormat =new SimpleDateFormat("yyyyMMdd");
        String dateStr=dataFormat.format(date);
        try{
            //创建日期目录
            File file = new File("E:\\pic\\"+dateStr);//ngnix location
            //如果文件夹不存在则创建
            if  (!file .exists()  && !file .isDirectory())
            {
                System.out.println("//不存在");
                file .mkdir();
            } else
            {
                System.out.println("//目录存在");
            }
            //创建图片文件
            File filePic=new File("E:\\pic\\"+dateStr+"\\"+filename);
            FileOutputStream fos=new FileOutputStream(filePic);
            fos.write(bytes,0,bytes.length);

            fos.flush();

            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }


        PictureResult result = new PictureResult();
        result.setCode(0);
        result.setMsg("");
        PictureData data = new PictureData();
        data.setSrc("https://localhost//"+dateStr+"//"+filename);//图片服务器中url地址
        result.setData(data);
        return result;
    }

    @Override
    public TaotaoResult addItem(TbItem tbItem, String itemDesc, List<Integer> paramKeyIds, List<String> paramValue) {
        //生成一个商品id
        final Long itemId = IDUtils.genItemId();
        //生成一个当前时间 作为 创建时间和修改时间
        Date date = new Date();
        tbItem.setId(itemId);//id
        tbItem.setStatus((byte)1);
        tbItem.setCreated(date);//created
        tbItem.setUpdated(date);//updated
        //商品的基本信息准备完毕
        int i = tbItemMapper.addItem(tbItem);
        if(i<=0){
            return TaotaoResult.build(500,"添加商品基本信息失败");
        }
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);//itemid
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(itemDesc);//itemdesc
        //商品描述信息准备完毕
        int j = tbItemDescMapper.addItemDesc(tbItemDesc);
        if(j<=0){
            return TaotaoResult.build(500,"添加商品描述信息失败");
        }
//        /**
//         *        意味着前面都没有报错 添加商品成功了 我们应该做solr索引同步了
//         *        意味着他应该发布一个消息到消息队列里面去
//         *        提供给 search-service 来使用
//         *        为什么发送id过去呢 ？
//         *        因为 我发送了id过去  search-service可以根据id查询到商品信息
//         *        就会得到商品对象  使用solrService。add（商品对象）;
//         *
//         */
        for(int x = 0;x<paramKeyIds.size();x++){
            //根据id查ItemParamValue
            TbItemParamValue tbItemParamValue = new TbItemParamValue();
            tbItemParamValue.setItemId(itemId);
            tbItemParamValue.setParamId(paramKeyIds.get(x));
            tbItemParamValue.setParamValue(paramValue.get(x));
            int y = tbItemParamMapper.addGroupValue(tbItemParamValue);
            if(y<=0){
                return TaotaoResult.build(500,"添加商品规格参数信息失败");
            }
        }


        return TaotaoResult.build(200,"添加商品成功");
    }
}
