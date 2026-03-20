# Demo: Arquitectura en Capas (Layered Architecture)

> **La vieja confiable... hasta que deja de serlo**

Esta es una aplicación demo pedagógica que muestra cómo funciona la arquitectura en capas tradicional, sus ventajas y sus limitaciones principales.

---

## ¿Qué es la Arquitectura en Capas?

La arquitectura en capas organiza el código en niveles jerárquicos donde cada capa tiene una responsabilidad específica y solo puede depender de la capa inmediatamente inferior.

```
┌─────────────────────────────────────┐
│     CAPA DE PRESENTACIÓN            │
│        (Controller)                 │
│  - Recibe peticiones HTTP           │
│  - Devuelve respuestas              │
└──────────────┬──────────────────────┘
               │ depende de
               ▼
┌─────────────────────────────────────┐
│     CAPA DE LÓGICA DE NEGOCIO       │
│         (Service)                   │
│  - Validaciones                     │
│  - Cálculos                         │
│  - Reglas de negocio                │
└──────────────┬──────────────────────┘
               │ depende de
               ▼
┌─────────────────────────────────────┐
│     CAPA DE ACCESO A DATOS          │
│        (Repository)                 │
│  - Operaciones CRUD                 │
│  - Queries a la base de datos       │
└──────────────┬──────────────────────┘
               │ depende de
               ▼
┌─────────────────────────────────────┐
│     CAPA DE PERSISTENCIA            │
│         (Entity)                    │
│  - Estructura de datos              │
│  - Mapeo objeto-relacional          │
└─────────────────────────────────────┘
```

---

## Estructura del Proyecto

```
demo-layered-order-app/
├── src/
│   └── main/
│       ├── java/com/demo/layered/
│       │   ├── LayeredOrderApplication.java    # Clase principal
│       │   ├── controller/
│       │   │   └── OrderController.java        # Capa de Presentación
│       │   ├── service/
│       │   │   └── OrderService.java           # Capa de Negocio
│       │   ├── repository/
│       │   │   └── OrderRepository.java        # Capa de Acceso a Datos
│       │   └── entity/
│       │       └── OrderEntity.java            # Capa de Persistencia
│       └── resources/
│           └── application.properties           # Configuración
├── pom.xml                                      # Dependencias Maven
└── README.md
```

---

## Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior

**No necesitas** instalar ninguna base de datos externa. La aplicación usa H2 en memoria.

---

## Cómo Ejecutar la Aplicación

### 1. Clonar o descargar el proyecto

```bash
git clone <tu-repositorio>
cd demo-layered-order-app
```

### 2. Compilar el proyecto

```bash
mvn clean install
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O alternativamente:

```bash
java -jar target/layered-order-app-1.0.0.jar
```

### 4. Verificar que está corriendo

Deberías ver en la consola:

```
========================================
  Layered Architecture Demo Started!
========================================
API disponible en: http://localhost:8080
H2 Console: http://localhost:8080/h2-console
========================================
```

---

## Endpoints Disponibles

### 1. Crear una Orden

**POST** `/orders`

**Request Body:**
```json
{
  "customerName": "Juan Pérez",
  "productName": "Laptop HP",
  "quantity": 2,
  "unitPrice": 850.00
}
```

**Respuesta Exitosa (201 Created):**
```json
{
  "id": 1,
  "customerName": "Juan Pérez",
  "productName": "Laptop HP",
  "quantity": 2,
  "totalAmount": 1700.00,
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Juan Pérez",
    "productName": "Laptop HP",
    "quantity": 2,
    "unitPrice": 850.00
  }'
```

**Ejemplo con descuento automático** (cantidad >= 10):
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "María González",
    "productName": "Mouse Inalámbrico",
    "quantity": 15,
    "unitPrice": 25.00
  }'
```
> Nota: El total será $337.50 en lugar de $375.00 (10% de descuento aplicado)

### 2. Obtener Todas las Órdenes

**GET** `/orders`

**Respuesta:**
```json
[
  {
    "id": 1,
    "customerName": "Juan Pérez",
    "productName": "Laptop HP",
    "quantity": 2,
    "totalAmount": 1700.00,
    "status": "PENDING",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

**Ejemplo con cURL:**
```bash
curl http://localhost:8080/orders
```

---

## Consola H2 (Opcional)

Para ver la base de datos directamente:

1. Abre tu navegador en: http://localhost:8080/h2-console
2. Usa estas credenciales:
   - **JDBC URL:** `jdbc:h2:mem:ordersdb`
   - **User:** `sa`
   - **Password:** *(dejar vacío)*
3. Click en "Connect"

---

## ¿Qué Demuestra esta Demo?

### 1. Flujo de una Petición

Cuando haces `POST /orders`, la petición fluye así:

```
HTTP Request
    │
    ▼
OrderController (recibe el JSON)
    │
    ├─> Valida formato básico
    │
    ▼
OrderService (aplica lógica de negocio)
    │
    ├─> Valida reglas de negocio
    ├─> Calcula el total
    ├─> Aplica descuentos si aplica
    │
    ▼
OrderRepository (guarda en base de datos)
    │
    ▼
OrderEntity (se persiste en H2)
    │
    ▼
HTTP Response
```

### 2. Dónde Vive la Lógica

En `OrderService.java` puedes ver claramente:

- **Validaciones:** `validateCustomerName()`, `validateQuantity()`, etc.
- **Cálculos:** `calculateTotalAmount()`
- **Reglas de negocio:** `applyBulkDiscount()` (10% de descuento para órdenes >= 10 unidades)

### 3. La Dependencia Problemática

Observa en `OrderService.java:20-23`:

```java
private final OrderRepository orderRepository;

public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
}
```

**La lógica de negocio depende directamente del repositorio (infraestructura).**

Esto significa:
- No puedes testear el servicio sin mockear JPA
- No puedes cambiar de base de datos sin modificar el servicio
- La lógica está "amarrada" a la infraestructura desde el inicio

---

## Ventajas de la Arquitectura en Capas

1. **Fácil de entender:** La estructura es intuitiva y clara
2. **Rápida de implementar:** Ideal para MVPs y proyectos pequeños
3. **Ampliamente conocida:** Cualquier desarrollador la entiende
4. **Buen punto de partida:** Funciona bien al inicio de un proyecto

---

## Limitaciones Principales

### 1. La Lógica Nace Amarrada a la Infraestructura

El `OrderService` depende directamente de `OrderRepository` (JPA/Spring Data). Si mañana quieres:
- Cambiar de base de datos
- Usar un servicio externo
- Testear sin base de datos

...tendrás que modificar tu lógica de negocio o crear mocks complejos.

### 2. Las Entidades Son Objetos de Infraestructura

`OrderEntity` tiene anotaciones de JPA (`@Entity`, `@Table`, `@Column`). Esto significa que tu modelo de dominio está dictado por la base de datos, no por el negocio.

### 3. Difícil de Testear en Aislamiento

Para testear `OrderService`, necesitas:
- Mockear `OrderRepository`
- O levantar una base de datos (aunque sea H2)

No puedes testear la lógica de negocio de forma pura y aislada.

### 4. Crece en Complejidad

A medida que la aplicación crece:
- Los servicios se vuelven "god classes"
- Aparecen dependencias cruzadas entre capas
- Es difícil mantener la separación de responsabilidades

---

## Entonces... ¿Es Mala la Arquitectura en Capas?

**No.** Es un paso importante en la evolución del desarrollo de software.

- Funcionó bien durante años
- Sigue siendo válida para proyectos pequeños o MVPs
- Es mejor que no tener arquitectura

**Pero** cuando la complejidad crece, sus limitaciones se vuelven evidentes. Ahí es donde arquitecturas como **Hexagonal** o **Clean Architecture** empiezan a brillar.

---

## Próximos Pasos

Si quieres entender cómo resolver estas limitaciones, explora:

1. **Arquitectura Hexagonal (Ports & Adapters)**
2. **Clean Architecture**
3. **Domain-Driven Design (DDD)**

Estas arquitecturas invierten las dependencias para que la lógica de negocio sea el centro, no la infraestructura.

---

## Tecnologías Usadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (en memoria)
- **Maven**

---

## Contribuciones

Este es un proyecto educativo. Si encuentras formas de hacerlo más claro o pedagógico, ¡los PRs son bienvenidos!

---

## Licencia

MIT - Siéntete libre de usar este código como quieras para aprender o enseñar.

---

**¿Preguntas o comentarios?** Abre un issue en GitHub.
