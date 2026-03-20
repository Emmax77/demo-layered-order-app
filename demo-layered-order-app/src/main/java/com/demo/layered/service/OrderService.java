package com.demo.layered.service;

import com.demo.layered.entity.OrderEntity;
import com.demo.layered.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de Negocio - Servicio que contiene la lógica de negocio para órdenes
 *
 * Aquí es donde vive la lógica de la aplicación: validaciones, cálculos, reglas de negocio.
 * Sin embargo, esta capa depende directamente de OrderRepository, que es infraestructura.
 *
 * Esta es la limitación principal de la arquitectura en capas: la lógica de negocio
 * nace amarrada a la infraestructura.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Crea una nueva orden aplicando reglas de negocio
     */
    public OrderEntity createOrder(String customerName, String productName, Integer quantity, Double unitPrice) {
        // Validaciones de negocio
        validateCustomerName(customerName);
        validateProductName(productName);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);

        // Cálculo del total (lógica de negocio)
        Double totalAmount = calculateTotalAmount(quantity, unitPrice);

        // Aplicar descuento si la orden es grande (más lógica de negocio)
        if (quantity >= 10) {
            totalAmount = applyBulkDiscount(totalAmount);
        }

        // Crear la entidad (aquí ya estamos usando un objeto de infraestructura)
        OrderEntity order = new OrderEntity(customerName, productName, quantity, totalAmount);

        // Persistir (dependencia directa de infraestructura)
        return orderRepository.save(order);
    }

    /**
     * Obtiene todas las órdenes
     */
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    // ==================== LÓGICA DE NEGOCIO ====================

    private void validateCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }

    private void validateUnitPrice(Double unitPrice) {
        if (unitPrice == null || unitPrice <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
    }

    private Double calculateTotalAmount(Integer quantity, Double unitPrice) {
        return quantity * unitPrice;
    }

    private Double applyBulkDiscount(Double totalAmount) {
        // 10% de descuento para órdenes grandes
        return totalAmount * 0.9;
    }
}
