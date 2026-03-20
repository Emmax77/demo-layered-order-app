package com.demo.layered.controller;

import com.demo.layered.entity.OrderEntity;
import com.demo.layered.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Capa de Presentación - Controlador REST que maneja las peticiones HTTP
 *
 * Esta capa recibe las peticiones del cliente, delega la lógica al servicio,
 * y devuelve las respuestas. Es el punto de entrada de la aplicación.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint para crear una nueva orden
     * POST /orders
     *
     * Demuestra el flujo típico en arquitectura en capas:
     * Controller -> Service -> Repository -> Database
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            OrderEntity order = orderService.createOrder(
                    request.getCustomerName(),
                    request.getProductName(),
                    request.getQuantity(),
                    request.getUnitPrice()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new OrderResponse(order));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al crear la orden"));
        }
    }

    /**
     * Endpoint para obtener todas las órdenes
     * GET /orders
     */
    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        List<OrderEntity> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // ==================== DTOs ====================

    /**
     * DTO para la petición de crear orden
     */
    public static class CreateOrderRequest {
        private String customerName;
        private String productName;
        private Integer quantity;
        private Double unitPrice;

        // Getters y Setters
        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }
    }

    /**
     * DTO para la respuesta de orden creada
     */
    public static class OrderResponse {
        private Long id;
        private String customerName;
        private String productName;
        private Integer quantity;
        private Double totalAmount;
        private String status;
        private String createdAt;

        public OrderResponse(OrderEntity order) {
            this.id = order.getId();
            this.customerName = order.getCustomerName();
            this.productName = order.getProductName();
            this.quantity = order.getQuantity();
            this.totalAmount = order.getTotalAmount();
            this.status = order.getStatus();
            this.createdAt = order.getCreatedAt().toString();
        }

        // Getters
        public Long getId() {
            return id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getProductName() {
            return productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    /**
     * DTO para respuestas de error
     */
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
