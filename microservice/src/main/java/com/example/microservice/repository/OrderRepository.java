package com.example.microservice.repository;

import com.example.microservice.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Order> mapper = (rs, rowNum) -> {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setCustomerId(rs.getLong("customer_id"));
        o.setDescription(rs.getString("description"));
        o.setAmount(rs.getBigDecimal("amount"));
        o.setOrderDate(LocalDate.parse(rs.getString("order_date")));
        o.setStatus(rs.getString("status"));
        return o;
    };

    /* ---- orders for a specific customer (paginated) ---- */
    public List<Order> findByCustomerId(Long customerId, int offset, int limit) {
        return jdbc.query(
                "SELECT id, customer_id, description, amount, order_date, status "
                        + "FROM orders WHERE customer_id = ? ORDER BY order_date DESC LIMIT ? OFFSET ?",
                mapper, customerId, limit, offset
        );
    }

    public long countByCustomerId(Long customerId) {
        Long result = jdbc.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE customer_id = ?", Long.class, customerId
        );
        return result != null ? result : 0;
    }

    /* ---- single order scoped to customer ---- */
    public Optional<Order> findByIdAndCustomerId(Long id, Long customerId) {
        List<Order> list = jdbc.query(
                "SELECT id, customer_id, description, amount, order_date, status "
                        + "FROM orders WHERE id = ? AND customer_id = ?",
                mapper, id, customerId
        );
        return list.stream().findFirst();
    }

    /* ---- all orders with optional date range filter (paginated) ---- */
    public List<Order> findAll(int offset, int limit,
                               LocalDate startDate, LocalDate endDate, String sortDir) {

        StringBuilder sql = new StringBuilder(
                "SELECT id, customer_id, description, amount, order_date, status FROM orders");
        List<Object> params = new ArrayList<>();

        appendDateFilter(sql, params, startDate, endDate);

        String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        sql.append(" ORDER BY order_date ").append(direction);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sql.toString(), mapper, params.toArray());
    }

    public long countAll(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders");
        List<Object> params = new ArrayList<>();

        appendDateFilter(sql, params, startDate, endDate);

        Long result = jdbc.queryForObject(sql.toString(), Long.class, params.toArray());
        return result != null ? result : 0;
    }

    /* ---- save (insert or update) ---- */
    public Order save(Order order) {
        if (order.getId() == null) {
            jdbc.update(
                    "INSERT INTO orders(customer_id, description, amount, order_date, status) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    order.getCustomerId(), order.getDescription(), order.getAmount(),
                    order.getOrderDate().toString(), order.getStatus()
            );
            Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
            order.setId(id);
        } else {
            jdbc.update(
                    "UPDATE orders SET description = ?, amount = ?, order_date = ?, status = ? WHERE id = ?",
                    order.getDescription(), order.getAmount(),
                    order.getOrderDate().toString(), order.getStatus(), order.getId()
            );
        }
        return order;
    }

    /* ---- delete ---- */
    public boolean deleteByIdAndCustomerId(Long id, Long customerId) {
        int rows = jdbc.update("DELETE FROM orders WHERE id = ? AND customer_id = ?", id, customerId);
        return rows > 0;
    }

    /* ---- private helpers ---- */
    private void appendDateFilter(StringBuilder sql, List<Object> params,
                                  LocalDate startDate, LocalDate endDate) {
        if (startDate != null || endDate != null) {
            sql.append(" WHERE");
            if (startDate != null) {
                sql.append(" order_date >= ?");
                params.add(startDate.toString());
            }
            if (startDate != null && endDate != null) {
                sql.append(" AND");
            }
            if (endDate != null) {
                sql.append(" order_date <= ?");
                params.add(endDate.toString());
            }
        }
    }
}
