package com.taotao.content.service.impl;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;


    @Override
    public List<EasyUITreeNode> getContentCategoryList(Long parentId) {
        List<TbContentCategory> contentCategories  = tbContentCategoryMapper.findContentCategoryByParentId(parentId);

        List<EasyUITreeNode> reuslt = new ArrayList<EasyUITreeNode>();


        for (TbContentCategory cateogry:contentCategories){
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(cateogry.getId());
            node.setText(cateogry.getName());
            node.setState(cateogry.getIsParent()?"closed":"open");
            reuslt.add(node);

        }

        return reuslt;
    }

    @Override
    public TaotaoResult addContentCategory(Long parentId, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        Date date = new Date();
        // 默认没有父亲的
        contentCategory.setIsParent(false);
        //需要添加的节点名称
        contentCategory.setName(name);
        //关联父节点
        contentCategory.setParentId(parentId);
        //排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
        contentCategory.setSortOrder(1);
        //状态。可选值:1(正常),2(删除)
        contentCategory.setStatus(1);
        contentCategory.setCreated(date);
        contentCategory.setUpdated(date);
        //应该添加分类了
        tbContentCategoryMapper.addContentCategory(contentCategory);


        //根据parentId查询 分类对象 判断这个分类对象的isParent是否为false 如果为false 改为true
        TbContentCategory category = tbContentCategoryMapper.findContentCategoryById(parentId);
        //代表他本身不是父类节点
        if(!category.getIsParent()){
            category.setIsParent(true);
            tbContentCategoryMapper.updateContentCategory(category);
        }

        return TaotaoResult.ok(contentCategory);
    }
}
