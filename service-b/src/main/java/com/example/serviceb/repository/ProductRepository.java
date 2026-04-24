package com.example.serviceb.repository;

import com.example.serviceb.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbc;

    public ProductRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Product> mapper = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        return p;
    };

    public List<Product> findAll(int offset, int limit) {
        return jdbc.query(
                "SELECT id, name, description, price, stock_quantity FROM products ORDER BY id LIMIT ? OFFSET ?",
                mapper, limit, offset
        );
    }

    public long count() {
        Long result = jdbc.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        return result != null ? result : 0;
    }

    public Optional<Product> findById(Long id) {
        List<Product> list = jdbc.query(
                "SELECT id, name, description, price, stock_quantity FROM products WHERE id = ?",
                mapper, id
        );
        return list.stream().findFirst();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            jdbc.update(
                    "INSERT INTO products(name, description, price, stock_quantity) VALUES (?, ?, ?, ?)",
                    product.getName(), product.getDescription(), product.getPrice(), product.getStockQuantity()
            );
            Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
            product.setId(id);
        } else {
            jdbc.update(
                    "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ? WHERE id = ?",
                    product.getName(), product.getDescription(), product.getPrice(),
                    product.getStockQuantity(), product.getId()
            );
        }
        return product;
    }

    public boolean deleteById(Long id) {
        int rows = jdbc.update("DELETE FROM products WHERE id = ?", id);
        return rows > 0;
    }
}
