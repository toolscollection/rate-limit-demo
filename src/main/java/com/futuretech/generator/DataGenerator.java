package com.futuretech.generator;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class DataGenerator {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker(new Locale("zh-CN"));

    private static final int BATCH_SIZE = 1000;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public void generateData(int userCount, int ordersPerUser) {
        log.info("开始生成测试数据...");
        long startTime = System.currentTimeMillis();

        // 使用批处理提高插入效率
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            conn.setAutoCommit(false);

            // 生成用户数据
            generateUsers(conn, userCount);

            // 生成订单相关数据
            generateOrders(conn, userCount, ordersPerUser);

            conn.commit();
        } catch (SQLException e) {
            log.error("生成数据失败", e);
        }

        long endTime = System.currentTimeMillis();
        log.info("数据生成完成，耗时：{} 秒", (endTime - startTime) / 1000);
    }

    private void generateUsers(Connection conn, int userCount) throws SQLException {
        log.info("开始生成用户数据...");
        String sql = "INSERT INTO users (user_name, phone, email, address, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < userCount; i++) {
                ps.setString(1, faker.name().fullName());
                ps.setString(2, faker.phoneNumber().cellPhone());
                ps.setString(3, faker.internet().emailAddress());
                ps.setString(4, faker.address().fullAddress());
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(5, Timestamp.valueOf(now));
                ps.setTimestamp(6, Timestamp.valueOf(now));

                ps.addBatch();

                if ((i + 1) % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    log.info("已生成 {} 条用户数据", i + 1);
                }
            }
            ps.executeBatch();
        }
    }

    private void generateOrders(Connection conn, int userCount, int ordersPerUser) throws SQLException {
        log.info("开始生成订单数据...");
        String orderSql = "INSERT INTO orders (order_no, user_id, total_amount, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_name, product_price, quantity, create_time) VALUES (?, ?, ?, ?, ?)";
        String paymentSql = "INSERT INTO payments (order_id, payment_no, payment_status, payment_amount, payment_time, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemPs = conn.prepareStatement(itemSql);
             PreparedStatement paymentPs = conn.prepareStatement(paymentSql)) {

            Random random = new Random();
            String[] orderStatus = {"PAID", "UNPAID", "CANCELLED"};
            String[] paymentStatus = {"SUCCESS", "FAILED", "PENDING"};

            for (int userId = 1; userId <= userCount; userId++) {
                for (int j = 0; j < ordersPerUser; j++) {
                    // 生成订单
                    String orderNo = UUID.randomUUID().toString().replace("-", "");
                    BigDecimal totalAmount = BigDecimal.valueOf(random.nextInt(10000));
                    LocalDateTime orderTime = LocalDateTime.now().minusDays(random.nextInt(365));

                    orderPs.setString(1, orderNo);
                    orderPs.setLong(2, userId);
                    orderPs.setBigDecimal(3, totalAmount);
                    orderPs.setString(4, orderStatus[random.nextInt(orderStatus.length)]);
                    orderPs.setTimestamp(5, Timestamp.valueOf(orderTime));
                    orderPs.setTimestamp(6, Timestamp.valueOf(orderTime));
                    orderPs.executeUpdate();

                    ResultSet rs = orderPs.getGeneratedKeys();
                    if (rs.next()) {
                        long orderId = rs.getLong(1);

                        // 生成订单商品
                        int itemCount = random.nextInt(5) + 1;
                        for (int k = 0; k < itemCount; k++) {
                            itemPs.setLong(1, orderId);
                            itemPs.setString(2, faker.commerce().productName());
                            itemPs.setBigDecimal(3, BigDecimal.valueOf(random.nextInt(1000)));
                            itemPs.setInt(4, random.nextInt(5) + 1);
                            itemPs.setTimestamp(5, Timestamp.valueOf(orderTime));
                            itemPs.addBatch();
                        }

                        // 生成支付记录
                        paymentPs.setLong(1, orderId);
                        paymentPs.setString(2, UUID.randomUUID().toString().replace("-", ""));
                        paymentPs.setString(3, paymentStatus[random.nextInt(paymentStatus.length)]);
                        paymentPs.setBigDecimal(4, totalAmount);
                        paymentPs.setTimestamp(5, Timestamp.valueOf(orderTime.plusMinutes(random.nextInt(60))));
                        paymentPs.setTimestamp(6, Timestamp.valueOf(orderTime));
                        paymentPs.setTimestamp(7, Timestamp.valueOf(orderTime));
                        paymentPs.addBatch();
                    }

                    if ((j + 1) % BATCH_SIZE == 0) {
                        itemPs.executeBatch();
                        paymentPs.executeBatch();
                        conn.commit();
                        log.info("已为用户 {} 生成 {} 条订单数据", userId, j + 1);
                    }
                }
                itemPs.executeBatch();
                paymentPs.executeBatch();
                conn.commit();
            }
        }
    }
}