package com.example.inventory.model;

import java.time.Instant;

public record OrderEvent(
        String orderId,
        String medicineId,
        int quantity,
        Instant timestamp
) {
}
