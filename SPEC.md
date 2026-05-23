# SPEC - Especificaciones de Estilo de Codigo UniCine

> Documento vivo con convenciones de estilo para mantener consistencia en el proyecto.
> Actualizacion: Mayo 2026

---

## 1. Convenciones de Nomenclatura

### Metodos, funciones y tests
- **Sin conectores** entre palabras clave.
- Ejemplo correcto: `registrarCuponExpirado`, `calcularValorTotal`, `validarExistencia`
- Ejemplo incorrecto: `registrarConCuponExpirado`, `calcularElValorTotal`, `validarParaExistencia`

### Paquetes
- Dominio por feature: `com.unicine.service.purchase`, `com.unicine.entity.purchase`

### Clases
- Interfaces de servicio: `XxxServicio`
- Implementaciones: `XxxServicioImp`
- Tests: `XxxServicioTest`
- Repositorios: `XxxRepo`

---

## 2. Comentarios y Documentacion

### Comentarios de clase y metodo
- Ubicados **arriba** de la declaracion de la clase o metodo.
- Si el comentario es largo (supera una linea), se desplaza **una linea hacia arriba** del `//` o `/**` para no estorbar la lectura del codigo.

### Formato
```java
/**
 * Metodo para comprobar la presencia del recurso que se esta buscando.
 * Lanza ResourceNotFoundException si no se encuentra el recurso
 * en el catalogo centralizado de errores.
 */
private void validarExiste(Optional<Recurso> recurso) {
    // ...
}
```

```java
// Metodo para calcular el valor total de la compra sumando entradas,
// confiteria y aplicando el descuento del cupon si aplica.
private Double calcularValorTotal(Compra compra) {
    // ...
}
```

---

## 3. Estructura de Clases

Orden recomendado dentro de una clase de servicio:

1. **Atributos / Inyeccion de dependencias**
2. **Constructor**
3. **Metodos de soporte** (privados, validaciones, utilidades)
4. **Metodos de negocio** (implementacion de la interfaz)
5. **Metodos de listado** (CRUD y consultas)

---

## 4. Manejo de Excepciones

- Usar exclusivamente las excepciones del paquete `com.unicine.exception`.
- Usar los catalogos `ErrorCatalog` y `SuccessCatalog` para mensajes.
- No lanzar `RuntimeException` ni `Exception` genericos con mensajes hardcodeados.

---

## 5. Convenciones de Codigo

- Lombok: usar `@Builder`, `@Getter`, `@Setter`, `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`
- Validaciones: usar `ValidationMessages` para mensajes de Bean Validation.
- Repositorios: extender `JpaRepository`; consultas custom con `@Query`.
- Servicios: anotar con `@Service` y `@Validated`.

---

## 6. Tests

- `@SpringBootTest` + `@Transactional`
- Dataset SQL: `@Sql("classpath:dataset.sql")`
- Nomenclatura sin conectores: `registrarCuponExpirado`, `obtenerInexistente`
- Validar mensajes de `ErrorCatalog` y `SuccessCatalog` en assertions.
