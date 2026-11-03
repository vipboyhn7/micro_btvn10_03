package com.example.inventory.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final JdbcTemplate jdbcTemplate;

    public InventoryController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/medicines/{medicineId}")
    public Map<String, Object> stock(@PathVariable String medicineId) {
        return jdbcTemplate.queryForMap("SELECT id, name, stock FROM medicines WHERE id = ?", medicineId);
    }

    @GetMapping("/medicines")
    public List<Map<String, Object>> medicines() {
        return jdbcTemplate.queryForList("SELECT id, name, stock FROM medicines ORDER BY id");
    }
}
