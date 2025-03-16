package com.futuretech.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hierarchy")
public class HierarchyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/companies")
    public List<TreeNode<Company>> getCompaniesHierarchy(@RequestParam String name) {
        //return companyService.getCompanyHierarchy(name);
        List<TreeNode<Company>> companyHierarchyv2 = companyService.getCompanyHierarchyv2(name);
        return companyHierarchyv2;
    }
}
