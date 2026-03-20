package com.demo.layered.repository;

import com.demo.layered.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Capa de Acceso a Datos - Repositorio para operaciones CRUD sobre órdenes
 *
 * Spring Data JPA hace el trabajo pesado, pero esta interfaz está directamente
 * amarrada a la infraestructura (JPA/JDBC). La capa de negocio dependerá
 * directamente de esta abstracción de infraestructura.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // Spring Data JPA proporciona automáticamente métodos como:
    // - save()
    // - findById()
    // - findAll()
    // - delete()
    // etc.

    // Aquí podrías agregar métodos de consulta personalizados si fuera necesario
    // Por ejemplo:
    // List<OrderEntity> findByCustomerName(String customerName);
}
