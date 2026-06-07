# Diseño: Sistema de Estados de Película + Notificaciones + Historial

**Fecha:** 2025-06-07  
**Versión:** 1.0  
**Estado:** Aprobado para implementación

---

## 1. Resumen Ejecutivo

Implementar un sistema que gestione automáticamente los cambios de estado de las películas en función de sus funciones programadas, notifique a los usuarios suscriptos por email, y mantenga un historial de transiciones para auditoría.

---

## 2. Alcance

### Incluido
- Cambios automáticos de estado (PENDIENTE → PREVENTA → ESTRENO → CARTELERA → FUERA_CARTELERA)
- Notificaciones por email a usuarios que siguen una película
- Historial de cambios de estado por película
- Extensibilidad para futuras notificaciones (ej: precio)
- Test de cambio de estado en tiempo real
- Arreglo de 18 tests de repository fallidos

### Excluido (para futuras fases)
- API REST para suscribirse/desuscribirse (Fase 3)
- Notificación de cambio de precio (preparado pero no implementado)
- Panel de admin teatro (TODO: cambiar destinatario de alertas)

---

## 3. Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│  Capa de Servicio (Negocio)                                  │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │ Estado       │  │ Historial    │  │ EmailService       │  │
│  │ Pelicula     │  │ Estado       │  │ (existente)        │  │
│  │ Service      │  │ Service      │  │                    │  │
│  └──────┬───────┘  └─────────────┘  └────────────────────┘  │
│         │                 emits                              │
│         │                 Spring Events                      │
│         │         ┌───────▼────────┐                         │
│         │         │ PeliculaState  │                         │
│         │         │ ChangeEvent    │                         │
│         │         └───────┬────────┘                         │
│         │                 │ listened by                      │
│         │         ┌───────▼────────┐                         │
│         │         │ Notificacion   │                         │
│         │         │ EventListener  │                         │
│         │         └───────┬────────┘                         │
│         │                 │ checks Coleccion                  │
│         │                 │ notificacionActiva                │
│         └─────────────────┴───────────────────────────────────┘
│                                                             │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Scheduled Tasks                                         │   │
│  │ • @Scheduled(cron="0 0 0 * * ?") → midnight check      │   │
│  │ • Trigger on registrar Funcion → immediate update      │   │
│  └────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Modelo de Datos

### 4.1 Extensión de `Coleccion`

Agregar columna:
```sql
ALTER TABLE coleccion ADD COLUMN notificacion_activa BOOLEAN DEFAULT TRUE;
```

### 4.2 Nueva Tabla: `historial_estado_pelicula`

```sql
CREATE TABLE historial_estado_pelicula (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pelicula_disposicion_id INT NOT NULL,
    estado_anterior VARCHAR(20) NOT NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    fecha_cambio DATETIME NOT NULL,
    FOREIGN KEY (pelicula_disposicion_id) REFERENCES pelicula_disposicion(id)
);
```

**Nota:** Cada fila representa una transición. Consultar con `ORDER BY fecha_cambio` reconstruye la cronología completa.

---

## 5. Componentes

### 5.1 Evento: `PeliculaStateChangeEvent`

```java
public record PeliculaStateChangeEvent(
    Integer peliculaDisposicionId,
    EstadoPelicula estadoAnterior,
    EstadoPelicula estadoNuevo,
    LocalDateTime fechaCambio
) {}
```

### 5.2 Listener: `PeliculaStateChangeEventListener`

Responsabilidades:
1. Recibir `PeliculaStateChangeEvent`
2. Consultar `ColeccionRepo.findByPeliculaAndNotificacionActiva(...)`
3. Por cada suscriptor, construir email HTML
4. Invocar `EmailService.enviarEmail(...)`
5. Registrar en `historial_estado_pelicula`

### 5.3 Servicio: `HistorialEstadoPeliculaServicio`

Métodos:
- `registrar(Integer peliculaDisposicionId, EstadoPelicula anterior, EstadoPelicula nuevo)`
- `obtenerPorPelicula(Integer peliculaDisposicionId)` → List<HistorialEstadoPelicula>
- `eliminarPorPelicula(Integer peliculaDisposicionId)` → para admin manual
- `contarPorPelicula(Integer peliculaDisposicionId)` → Long
- `obtenerAntiguedadUltimoCambio(Integer peliculaDisposicionId)` → Integer (días)
- `alertarSiHistorialViejo(Integer peliculaDisposicionId)` → envía email si ≥ 180 días

---

## 6. Flujo de Cambio de Estado

### 6.1 Medianoche (Cron)
1. `@Scheduled(cron = "0 0 0 * * ?")` ejecuta `actualizarEstadosAutomaticamente()`
2. Para cada `PeliculaDisposicion`, calcula estado basado en fechas de funciones
3. Si estado cambia:
   - Persiste nuevo estado
   - Emite `PeliculaStateChangeEvent`
   - Listener notifica suscriptores
   - Registra en historial

### 6.2 Mismo Día (Registro de Función)
1. Se registra nueva `Funcion`
2. Se invoca `actualizarEstado(peliculaDisposicion)` inmediatamente
3. Si la función empieza hoy, estado puede cambiar de PREVENTA → ESTRENO
4. Emite evento, notifica, registra historial

---

## 7. Email

### 7.1 Notificación de Cambio de Estado

**Asunto:** `Unicine: "{nombrePelicula}" ahora en {estadoNuevo}`

**Contenido HTML:**
- Título de la película
- Estado anterior → Estado nuevo
- Fecha del cambio
- Mensaje de agradecimiento

### 7.2 Alerta de Historial Viejo

**Destinatario:** `cristianbarrera100@gmail.com` (pruebas)
**TODO:** Cambiar a admin teatro creador de la película.

**Condición:** Último cambio de estado ≥ 180 días (6 meses)
**Mensaje:** "La película X lleva 6 meses sin cambiar de estado. Considere actualizar su programación."

---

## 8. Test "Vivo"

**Objetivo:** Demostrar cambio automático de estado en tiempo real.

**Pasos:**
1. Crear `PeliculaDisposicion` con función cuyo `fechaInicio` sea `now + 5 segundos`
2. Verificar estado inicial = `PREVENTA`
3. Esperar 5 segundos (`Awaitility`)
4. Re-evaluar estado → debe ser `ESTRENO`
5. Verificar:
   - Evento `PeliculaStateChangeEvent` emitido
   - Email enviado (verificado con mock)
   - Historial registrado en base de datos

---

## 9. Plan de Implementación

1. **Agregar `notificacionActiva` a `Coleccion`**
2. **Crear entidad `HistorialEstadoPelicula`** + repo + servicio
3. **Crear `PeliculaStateChangeEvent`**
4. **Modificar `EstadoPeliculaService`** para emitir evento
5. **Crear `PeliculaStateChangeEventListener`**
6. **Agregar métodos al repo de `Coleccion`** para notificaciones
7. **Crear test "vivo"** de cambio de estado con timer
8. **Arreglar los 18 tests de repository**
9. **Actualizar `dataset.sql`**

---

## 10. Decisiones Clave

| Decisión | Justificación |
|----------|--------------|
| Tabla separada para historial | Mejor que array embebido: permite queries SQL, índices, no crece fila de Película |
| Spring Events vs Observer | Spring Events es nativo del framework, sin dependencias extra, fácil de testear |
| Reutilizar `Coleccion` | Ya conecta Cliente↔Pelicula; agregar `notificacionActiva` es mínimo |
| Scheduled tasks medianoche | Las funciones se programan con fechas; el tiempo es el trigger natural |
| Alerta historial a email fijo | Para pruebas; TODO cambiar a admin teatro creador |

---

## 11. Futuras Extensiones

- Notificación de cambio de precio (`PeliculaPrecioChangeEvent`)
- Panel "Mis películas seguidas" con toggle de notificaciones
- Limpieza automática de historial mayor a 1 año (`@Scheduled`)
- Destinatario dinámico de alertas (admin teatro vs admin general)
