package com.futuretech.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("department")
public class Department {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer companyId;
    private Integer parentId;
}
