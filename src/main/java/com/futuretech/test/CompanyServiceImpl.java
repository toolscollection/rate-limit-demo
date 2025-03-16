package com.futuretech.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Array;
import java.util.*;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    /**
     * 支持机构名称模糊查询并返回层级结构
     * @param name
     * @return
     */
    public List<CompanyNode> getCompanyHierarchy(String name) {
        LambdaQueryWrapper<Company> companyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        companyLambdaQueryWrapper.like(StringUtils.isNotBlank(name), Company::getName, name);
        List<Company> matchedCompanies = this.list(companyLambdaQueryWrapper);
        HashSet<Company> result = new HashSet<>();
        for (Company matchedCompany : matchedCompanies) {
            addCompanyHierarchy(matchedCompany, result);
        }

        //层级结构
        ArrayList<CompanyNode> companyNodes = new ArrayList<>();
        for (Company company : result) {
            CompanyNode companyNode = new CompanyNode();
            companyNode.setId(company.getId());
            companyNode.setName(company.getName());
            companyNode.setParentId(company.getParentId());
            companyNodes.add(companyNode);
        }
        List<CompanyNode> companyNodes1 = buildHierarchy(companyNodes);
        return new ArrayList<>(companyNodes1);
    }

    private void addCompanyHierarchy(Company company, Set<Company> result) {
        result.add(company);
        Integer parentId = company.getParentId();
        if (parentId != null) {
            Company parentCompany = this.getById(parentId);
            if (parentCompany != null) {
                addCompanyHierarchy(parentCompany, result);
            }
        }
    }

    /**
     * 将平铺的节点转换为层级结构
     * @param nodes
     * @return
     */
    public List<CompanyNode> buildHierarchy(List<CompanyNode> nodes) {
        Map<Integer, CompanyNode> nodeMap = new HashMap<>();
        List<CompanyNode> roots = new ArrayList<>();

        // 将所有节点放入Map中
        for (CompanyNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        // 构建层级结构
        for (CompanyNode node : nodes) {
            if (node.getParentId() == null) {
                roots.add(node);
            } else {
                CompanyNode parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }

    /**
     * 查询指定名称机构的所有下级
     * @param name
     * @return
     */
    public List<TreeNode<Company>> getCompanyHierarchyv2(String name) {
        LambdaQueryWrapper<Company> companyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        companyLambdaQueryWrapper.like(StringUtils.isNotBlank(name), Company::getName, name);
        List<Company> matchedCompanies = this.list(companyLambdaQueryWrapper);
        ArrayList<TreeNode<Company>> roots = new ArrayList<>();
        for (Company matchedCompany : matchedCompanies) {
            TreeNode<Company> companyTreeNode = buildCompanyTree(matchedCompany);
            if (companyTreeNode != null) {
                roots.add(companyTreeNode);
            }
        }
        return roots;
    }

    private TreeNode<Company> buildCompanyTree(Company company) {
        TreeNode<Company> node = new TreeNode<>();
        node.setData(company);
        LambdaQueryWrapper<Company> companyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        companyLambdaQueryWrapper.eq(Company::getParentId, company.getId());
        List<Company> children = this.list(companyLambdaQueryWrapper);
        for (Company child : children) {
            node.addChild(buildCompanyTree(child));
        }
        return node;
    }

    public static void main(String[] args) throws Exception {
        //
        try (FileInputStream fileInputStream = new FileInputStream("D:\\ceshi\\myfile.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("D:\\ceshi\\myfilebak.txt");) {
            int byteData;
            while ((byteData = fileInputStream.read()) != -1) {
                fileOutputStream.write(byteData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
