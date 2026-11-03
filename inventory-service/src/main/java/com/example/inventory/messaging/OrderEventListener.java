package com.example.inventory.messaging;

import com.example.inventory.model.OrderEvent;
import com.example.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final InventoryService inventoryService;

    public OrderEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "medicine-stock-events",
            groupId = "inventory-service-group",
            containerFactory = "orderEventKafkaListenerContainerFactory"
    )
    public void consume(OrderEvent event) {
        inventoryService.decreaseStock(event);
    }
}
