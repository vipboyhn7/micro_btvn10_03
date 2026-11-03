package com.example.inventory.service;

import com.example.inventory.model.OrderEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final JdbcTemplate jdbcTemplate;

    public InventoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void decreaseStock(OrderEvent event) {
        if (event.quantity() <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than zero");
        }

        try {
            int inserted = jdbcTemplate.update(
                    "INSERT INTO processed_orders (order_id) VALUES (?) ON CONFLICT (order_id) DO NOTHING",
                    event.orderId());
            if (inserted == 0) {
                return;
            }

            int updated = jdbcTemplate.update(
                    "UPDATE medicines SET stock = stock - ? WHERE id = ? AND stock >= ?",
                    event.quantity(), event.medicineId(), event.quantity());
            if (updated != 1) {
                throw new IllegalStateException(
                        "Medicine not found or insufficient stock: " + event.medicineId());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException("Could not process order " + event.orderId(), exception);
        }
    }
}
