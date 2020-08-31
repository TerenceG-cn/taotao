package com.taotao.content.service.impl;

import com.taotao.pojo.EasyUIResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${CONTENT_KEY}")
    private String CONTENT_KEY;


    @Override
    public EasyUIResult findContentAll(long contentCategoryId) {


        List<TbContent> contents= tbContentMapper.findContentByCategroyId(contentCategoryId);
        EasyUIResult result = new EasyUIResult();
        result.setTotal(contents.size());
        result.setRows(contents);


        return result;
    }

    @Override
    public TaotaoResult addContent(TbContent tbContent) {
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        tbContentMapper.addContent(tbContent);

        jedisClient.del(CONTENT_KEY);
        return TaotaoResult.ok(tbContent);
    }

    @Override
    public List<TbContent> getContentAll(long contentCategoryId) {
//        /**
//         * 这里从redis中 获取数据 如果获取到了 则直接返回数据
//         */
//        String json = jedisClient.get(CONTENT_KEY);
//        if(StringUtils.isNotBlank(json)){
//            List<TbContent> contents = JsonUtils.jsonToList(json, TbContent.class);
//            return contents;
//        }
//        //判断 json不等于null 不为空
//        List<TbContent> contents= tbContentMapper.findContentByCategroyId(contentCategoryId);
//        /**
//         * 走到了这里 就把在数据存入到redis中
//         * redis中只能存入 key value(我认为是这个对象的json格式)
//         */
//        jedisClient.set(CONTENT_KEY, JsonUtils.objectToJson(contents));
//        return contents;
        /**
         * 未设置redies
         */
        List<TbContent> contents= tbContentMapper.findContentByCategroyId(contentCategoryId);
        return contents;
    }
}
