# Pharmacy Kafka Microservices

Gồm hai service Spring Boot:

- `medicine-service` (`8081`): quản lý thuốc, phát sự kiện tồn kho vào `medicine-stock-events` (3 partitions) và cập nhật giá vào `medicine-price-updates` (1 partition).
- `pharmacy-service` (`8082`): xử lý nhập kho/bán lẻ và phát thông báo vào `pharmacy-notifications` (2 partitions).
- `inventory-service` (`8083`): consumer thuộc group `inventory-service-group`, đọc `OrderEvent` từ `medicine-stock-events` và trừ tồn kho PostgreSQL theo `medicineId`.

## Chạy Kafka

```bash
docker compose up -d
docker compose logs -f kafka-init
```

PostgreSQL được chạy cùng Compose tại `localhost:5432`, database/user/password mặc định đều là `pharmacy`.

Kiểm tra topic và partition:

```bash
docker exec -it kafka-pharmacy /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
docker exec -it kafka-pharmacy /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic medicine-stock-events
```

## Chạy service

```bash
./mvnw spring-boot:run -pl medicine-service
./mvnw spring-boot:run -pl pharmacy-service
./mvnw spring-boot:run -pl inventory-service
```

Kiểm tra tồn kho sau khi thanh toán:

```bash
curl http://localhost:8083/api/inventory/medicines/PARA-500
```

Consumer thực hiện câu lệnh nguyên tử:

```sql
UPDATE medicines SET stock = stock - quantity
WHERE id = medicineId AND stock >= quantity;
```

Nếu chưa có Maven Wrapper, dùng `mvn spring-boot:run -pl <service>`.

Ví dụ tạo thuốc:

```bash
curl -X POST http://localhost:8081/api/medicines \
  -H 'Content-Type: application/json' \
  -d '{"code":"PARA-500","name":"Paracetamol","price":12000,"quantity":100}'
```

Thanh toán đơn hàng và gửi `OrderEvent` dạng JSON vào `medicine-stock-events`.
`medicineId` được dùng làm Kafka message key, vì vậy các đơn hàng cùng thuốc
luôn được phân phối vào cùng một partition:

```bash
curl -X POST http://localhost:8082/api/pharmacy/sales \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORDER-001","medicineId":"PARA-500","quantity":2}'
```

Event có dạng:

```json
{
  "orderId": "ORDER-001",
  "medicineId": "PARA-500",
  "quantity": 2,
  "timestamp": "2026-11-03T16:00:00Z"
}
```
# micro_btvn10_01
