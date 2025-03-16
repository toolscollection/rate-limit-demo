package com.futuretech.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long itemId;
    private Long orderId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private LocalDateTime createTime;
}