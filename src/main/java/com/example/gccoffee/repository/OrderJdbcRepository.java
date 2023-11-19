package com.example.gccoffee.repository;

import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;
import com.example.gccoffee.model.OrderStatus;
import java.util.List;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Repository
public class OrderJdbcRepository implements OrderRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public OrderJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  @Transactional
  public Order insert(Order order) {
    jdbcTemplate.update("INSERT INTO orders(order_id, email, address, postcode, order_status, created_at, updated_at) " +
        "VALUES (UUID_TO_BIN(:orderId), :email, :address, :postcode, :orderStatus, :createdAt, :updatedAt)",
      toOrderParamMap(order));
    order.getOrderItems()
      .forEach(item ->
        jdbcTemplate.update("INSERT INTO order_items(order_id, product_id, category, price, quantity, created_at, updated_at) " +
            "VALUES (UUID_TO_BIN(:orderId), UUID_TO_BIN(:productId), :category, :price, :quantity, :createdAt, :updatedAt)",
          toOrderItemParamMap(order.getOrderId(), order.getCreatedAt(), order.getUpdatedAt(), item)));
    return order;
  }

    @Override
    @Transactional
    public List<Order> findAll() {
        return jdbcTemplate.query("SELECT BIN_TO_UUID(order_id) AS order_id, email, address, postcode, order_status, created_at, updated_at FROM orders",
                (resultSet, i) -> {
                    var orderId = UUID.fromString(resultSet.getString("order_id"));
                    var email = resultSet.getString("email");
                    var address = resultSet.getString("address");
                    var postcode = resultSet.getString("postcode");
                    var orderStatus = OrderStatus.valueOf(resultSet.getString("order_status"));
                    var createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                    var updatedAt = resultSet.getObject("updated_at", LocalDateTime.class);
                    return new Order(orderId, new Email(email), address, postcode, List.of(), orderStatus, createdAt, updatedAt);
                });
    }

  private Map<String, Object> toOrderParamMap(Order order) {
    var paramMap = new HashMap<String, Object>();
    paramMap.put("orderId", order.getOrderId().toString().getBytes());
    paramMap.put("email", order.getEmail().getAddress());
    paramMap.put("address", order.getAddress());
    paramMap.put("postcode", order.getPostcode());
    paramMap.put("orderStatus", order.getOrderStatus().toString());
    paramMap.put("createdAt", order.getCreatedAt());
    paramMap.put("updatedAt", order.getUpdatedAt());
    return paramMap;
  }

  private Map<String, Object> toOrderItemParamMap(UUID orderId, LocalDateTime createdAt, LocalDateTime updatedAt, OrderItem item) {
    var paramMap = new HashMap<String, Object>();
    paramMap.put("orderId", orderId.toString().getBytes());
    paramMap.put("productId", item.productId().toString().getBytes());
    paramMap.put("category", item.category().toString());
    paramMap.put("price", item.price());
    paramMap.put("quantity", item.quantity());
    paramMap.put("createdAt", createdAt);
    paramMap.put("updatedAt", updatedAt);
    return paramMap;
  }
}
