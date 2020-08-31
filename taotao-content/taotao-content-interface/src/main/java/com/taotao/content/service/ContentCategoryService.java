package com.taotao.content.service;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;

import java.util.List;

public interface ContentCategoryService {
    List<EasyUITreeNode> getContentCategoryList(Long parentId);
    TaotaoResult addContentCategory(Long parentId, String name);
}
