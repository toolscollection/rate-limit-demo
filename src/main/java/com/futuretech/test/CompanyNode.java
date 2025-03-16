package com.futuretech.test;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyNode {
    private Integer id;
    private String name;
    private Integer parentId;
    private List<CompanyNode> children = new ArrayList<>();
}
