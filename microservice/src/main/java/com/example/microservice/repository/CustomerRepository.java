package com.example.microservice.repository;

import com.example.microservice.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbc;

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Customer> mapper = (rs, rowNum) -> {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setCreatedAt(LocalDate.parse(rs.getString("created_at")));
        return c;
    };

    /* ---- paginated list ---- */
    public List<Customer> findAll(int offset, int limit) {
        return jdbc.query(
                "SELECT id, name, email, phone, created_at FROM customers ORDER BY id LIMIT ? OFFSET ?",
                mapper, limit, offset
        );
    }

    public long count() {
        Long result = jdbc.queryForObject("SELECT COUNT(*) FROM customers", Long.class);
        return result != null ? result : 0;
    }

    /* ---- single lookup ---- */
    public Optional<Customer> findById(Long id) {
        List<Customer> list = jdbc.query(
                "SELECT id, name, email, phone, created_at FROM customers WHERE id = ?",
                mapper, id
        );
        return list.stream().findFirst();
    }

    /* ---- save (insert or update) ---- */
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            jdbc.update(
                    "INSERT INTO customers(name, email, phone, created_at) VALUES (?, ?, ?, ?)",
                    customer.getName(), customer.getEmail(), customer.getPhone(),
                    customer.getCreatedAt().toString()
            );
            Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
            customer.setId(id);
        } else {
            jdbc.update(
                    "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?",
                    customer.getName(), customer.getEmail(), customer.getPhone(), customer.getId()
            );
        }
        return customer;
    }

    /* ---- delete ---- */
    public boolean deleteById(Long id) {
        int rows = jdbc.update("DELETE FROM customers WHERE id = ?", id);
        return rows > 0;
    }

    /* ---- helper: count orders for a given customer ---- */
    public int countOrdersByCustomerId(Long customerId) {
        Integer result = jdbc.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE customer_id = ?", Integer.class, customerId
        );
        return result != null ? result : 0;
    }

    /* ---- helper: email uniqueness checks ---- */
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM customers WHERE email = ?", Integer.class, email
        );
        return count != null && count > 0;
    }

    public boolean existsByEmailAndNotId(String email, Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM customers WHERE email = ? AND id != ?", Integer.class, email, id
        );
        return count != null && count > 0;
    }
}
