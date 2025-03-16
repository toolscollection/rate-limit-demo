package com.futuretech.test;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CompanyService extends IService<Company> {
    List<CompanyNode> getCompanyHierarchy(String name);

    List<TreeNode<Company>> getCompanyHierarchyv2(String name);
}
