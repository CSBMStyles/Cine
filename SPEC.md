# SPEC - Especificaciones de Estilo de Codigo UniCine

> Documento vivo con convenciones de estilo para mantener consistencia en el proyecto.
> Actualizacion: Junio 2026

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

### Variables y funciones
- **Sin guion bajo (underscore)** en nombres de variables, funciones o metodos.
- Ejemplo correcto: `findByPeliculaDisposicionPeliculaCodigo`, `calcularPrecioBase`
- Ejemplo incorrecto: `findByPeliculaDisposicion_Pelicula_Codigo`, `calcular_precio_base`
- En repositorios Spring Data JPA, navegar propiedades usando camelCase en lugar de underscore (`_`).

### Constantes
- **Todo mayusculas con underscore** (`UPPER_SNAKE_CASE`).
- Ejemplo correcto: `CONSTANTE_PELICULA`, `MAX_INTENTOS_LOGIN`, `PRECIO_BASE_ENTRADA`
- Ejemplo incorrecto: `constantePelicula`, `maxIntentosLogin`, `PrecioBaseEntrada`

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
- Usar los catalogos de dominio en `com.unicine.util.validation.catalog.domain`:
  - `UserErrorCatalog` (usuarios, autenticacion)
  - `MovieErrorCatalog` (peliculas)
  - `TheaterErrorCatalog` (teatros, salas, ciudades)
  - `ShowingErrorCatalog` (funciones, horarios)
  - `PurchaseErrorCatalog` (compras, entradas, cupones)
  - `ImageErrorCatalog` (imagenes, servicios externos)
  - `SystemErrorCatalog` (validacion, errores generales)
- Todos los catalogos de error implementan `ErrorCode`.
- No lanzar `RuntimeException` ni `Exception` genericos con mensajes hardcodeados.

---

## 5. Convenciones de Codigo

- Lombok: usar `@Builder`, `@Getter`, `@Setter`, `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`
- Validaciones: usar `ValidationMessages` para mensajes de Bean Validation.
  - Constantes en `UPPER_SNAKE_CASE` con números en palabras (`MAX_FIFTY`, `MIN_EIGHT`, `EXACT_TEN`).
  - Sin mensajes hardcodeados en español en interfaces de servicio.
- Repositorios: extender `JpaRepository`; consultas custom con `@Query`.
  - Palabras clave SQL en **minusculas** (`select`, `from`, `where`, `join`, `and`, `or`, `group by`, `order by`, `count`, `avg`, `sum`).
  - Entidades y atributos en JPQL con **mayuscula inicial** tal cual se declaran en Java.
  - Ejemplo correcto:
    ```java
    @Query("select c from Coleccion c where c.cliente.cedula = :cedula and c.puntuacion is not null")
    ```
  - Ejemplo incorrecto:
    ```java
    @Query("SELECT c FROM Coleccion c WHERE c.cliente.cedula = :cedula AND c.puntuacion IS NOT NULL")
    ```
- Servicios: anotar con `@Service` y `@Validated`.

---

## 6. Tests

- `@SpringBootTest` + `@Transactional`
- Dataset SQL: `@Sql("classpath:dataset.sql")`
- Nomenclatura sin conectores: `registrarCuponExpirado`, `obtenerInexistente`
- Validar mensajes de `SuccessCatalog` y catálogos de dominio en assertions.

---

## 7. Convenciones de Commits

- **Todos los mensajes de commit deben estar en inglés**.
- Formato: `<tipo>(<alcance opcional>): <descripción en imperativo>`.
- Tipos permitidos: `feat`, `fix`, `test`, `refactor`, `chore`, `docs`, `style`, `perf`, `build`, `ci`.
- Descripción breve, en imperativo y sin punto final.

Ejemplo correcto:
```
feat(coupon): add CuponServicio with extended queries and tests
```

Ejemplo incorrecto:
```
feat(cupon): agregar servicio de cupones con consultas adicionales
```
