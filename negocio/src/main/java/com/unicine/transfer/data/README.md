# Deuda técnica: DTOs legacy en `transfer.data`

Este paquete conserva temporalmente DTOs que aún no han podido migrarse a `com.unicine.transfer.dto.response` sin romper la interfaz pública de los repositorios o sin modificar servicios.

## DTOs ya migrados

- `DetalleSillaDTO` → `com.unicine.transfer.dto.response.DetalleSillaDTO`
- `DetalleCompraDTO` → `com.unicine.transfer.dto.response.DetalleCompraDTO`
- `DetalleFuncionesDTO` → `com.unicine.transfer.dto.response.DetalleFuncionesDTO`
- `FuncionInterseccionDTO` → `com.unicine.transfer.dto.response.FuncionInterseccionDTO`
- `CompraResumenDTO` → eliminado (sin usos)
- `CompraConfiteriaDTO` → eliminado (sin usos)

## DTO pendiente

### `DetallePeliculaHorarioDTO`

**Motivo de permanencia:**

`PeliculaRepo.peliculaHorariosSalas` utiliza una consulta JPQL con `new com.unicine.transfer.data.DetallePeliculaHorarioDTO(p, f.horario, f.sala)` que construye el DTO directamente a partir de las entidades JPA `Pelicula`, `Horario` y `Sala`.

Para migrar este DTO a `transfer.dto.response` sin exponer entidades se requiere una de estas alternativas:

1. Cambiar el método del repositorio para que retorne `List<Object[]>` y construir el DTO en un mapper/servicio. Esto modifica la firma pública del repositorio y rompe `PeliculaTest.peliculaHorariosSalas`.
2. Cambiar el DTO para que contenga `PeliculaResponse`, `HorarioResponse` y `SalaResponse`, pero JPQL no permite invocar mappers de MapStruct dentro de `new`, por lo que la consulta actual dejaría de compilar.
3. Modificar `HorarioServicio` o crear un servicio intermedio que ejecute la consulta y realice el mapeo. Esto viola la restricción de no modificar servicios más allá de imports.

**Decisión:** Se deja `DetallePeliculaHorarioDTO` en `transfer.data` hasta que se defina una estrategia de migración que no rompa tests ni altere la interfaz de repositorios/servicios.
