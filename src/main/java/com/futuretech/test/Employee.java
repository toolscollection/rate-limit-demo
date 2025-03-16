package com.futuretech.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("employee")
public class Employee {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer departmentId;
}
