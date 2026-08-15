# Diseño: Nombres Deterministas Para Imágenes

**Fecha:** 2026-08-13  
**Versión:** 1.0  
**Estado:** Implementado

---

## 1. Resumen Ejecutivo

Reemplazar los nombres aleatorios generados por ImageKit en las nuevas imágenes de películas y personas por nombres deterministas, cortos y en mayúsculas. Las películas usarán su código interno; las personas usarán un identificador HMAC estable derivado de su tipo y cédula, sin exponer la cédula en carpetas, nombres ni URLs.

También se agregará Ratatouille al dataset de pruebas y se registrarán sus tres imágenes locales con los tipos `POSTER`, `BANNER` y `GALERIA`.

---

## 2. Objetivos

- Evitar que las pruebas repetidas acumulen archivos con sufijos aleatorios en ImageKit.
- Mantener un nombre estable para cada slot lógico de imagen.
- No exponer cédulas en nombres, carpetas o URLs públicas.
- Permitir una imagen `POSTER` y una `BANNER` por película.
- Permitir varias imágenes `GALERIA` ordenadas.
- Mantener las actualizaciones sobre el mismo asset remoto.
- Corregir el fixture que actualmente sube imágenes de Ratatouille asociadas a Encanto.

---

## 3. Alcance

### Incluido

- Generador dedicado de nombres deterministas.
- Generación de identificadores HMAC-SHA-256 para personas.
- Configuración secreta `IMAGE_IDENTIFIER_SECRET` fuera del repositorio.
- Nombres de películas basados en código, tipo y slot.
- Nombres de personas basados en prefijo `PER`, HMAC y tipo.
- Subida sin nombre aleatorio de ImageKit.
- Reutilización/sobrescritura del asset cuando existe el mismo slot.
- Registro de Ratatouille con código `6` en un fixture SQL exclusivo de imágenes.
- Pruebas con las tres imágenes existentes en `image/pelicula`.

### Excluido

- Migración automática de assets antiguos de Encanto u otras películas.
- Eliminación automática de archivos existentes en ImageKit.
- Cambio de las carpetas existentes basadas en el nombre de la película.
- Detección de duplicados por contenido mediante hash de bytes.
- Cambios en los datos de producción.

---

## 4. Formato De Nombres

### 4.1 Películas

El formato será:

```text
PEL-<codigo>-<tipo>-<orden>
```

Ejemplos para Ratatouille:

```text
PEL-6-POSTER-01
PEL-6-BANNER-02
PEL-6-GALERIA-03
```

El orden se conserva como metadato de `Imagen` y permite distinguir varias imágenes de galería.

### 4.2 Personas

El formato será:

```text
PER-<hmac8>-AVATAR
```

El HMAC se calculará sobre:

```text
<tipoPropietario>:<cedula>
```

El resultado será HMAC-SHA-256, codificado en hexadecimal y limitado a ocho caracteres uppercase. El prefijo visible será siempre `PER`; el tipo de propietario participa en el cálculo interno para evitar que dos roles con el mismo número produzcan el mismo identificador.

Ejemplo conceptual:

```text
PER-7F3A91C2-AVATAR
```

La clave HMAC se leerá desde `IMAGE_IDENTIFIER_SECRET`. No se utilizará la clave privada de ImageKit ni se almacenará la cédula en el nombre.

---

## 5. Reglas De Duplicados

### 5.1 Película

- `POSTER`: un slot por película.
- `BANNER`: un slot por película.
- `GALERIA`: múltiples slots, diferenciados por `orden`.
- Si el slot determinista ya existe en ImageKit, se sobrescribe el asset en lugar de crear otro.
- Si existe un registro de base de datos para `POSTER` o `BANNER`, se actualiza ese registro en lugar de insertar otro.
- Una nueva imagen de `GALERIA` recibe el siguiente orden disponible.

### 5.2 Persona

- Se conserva la regla actual de una imagen por persona.
- La validación de base de datos evita insertar una segunda relación.
- Si el asset remoto quedó de una prueba cuyo rollback eliminó la fila local, la siguiente subida reutiliza el nombre estable y lo sobrescribe.

### 5.3 ImageKit

Las nuevas subidas deterministas usarán:

```text
useUniqueFileName = false
overwriteFile = true
```

La actualización seguirá usando el nombre existente y el mismo `fileId`. ImageKit documenta que `useUniqueFileName=false` usa el nombre proporcionado, mientras que `overwriteFile=true` permite reemplazar el archivo existente en la misma carpeta.

---

## 6. Arquitectura

Se agregará un componente dedicado para generar nombres y códigos de persona. Sus responsabilidades serán:

1. Construir nombres de película a partir de código, tipo y orden.
2. Construir el material de entrada del HMAC para personas.
3. Calcular el HMAC con `HmacSHA256`.
4. Normalizar la salida a uppercase y limitarla a ocho caracteres.
5. Rechazar una configuración vacía de `IMAGE_IDENTIFIER_SECRET` con un error claro.

`ImagenServicioImp` resolverá el slot y el orden. `ImageKitService` recibirá el nombre ya resuelto y se limitará a procesar/subir/actualizar el archivo.

La carpeta de película continuará basándose en el nombre normalizado de la película para no migrar assets existentes en esta fase.

---

## 7. Datos De Prueba

Se agregará a `negocio/src/test/resources/ratatouille-dataset.sql`:

- Película `6` con nombre `Ratatouille`.
- Datos mínimos válidos para `puntuacion`, `sinopsis`, `urlTrailer` y `restriccionEdad`.
- Géneros solo cuando sean necesarios para mantener las relaciones del fixture.

`ImagenServicioTest` usará:

| Archivo | Tipo | Nombre esperado |
|---|---|---|
| `Ratatouille 1.webp` | `POSTER` | `PEL-6-POSTER-01` |
| `Ratatouille 2.webp` | `BANNER` | `PEL-6-BANNER-02` |
| `Ratatouille 3.jpg` | `GALERIA` | `PEL-6-GALERIA-03` |

Las imágenes antiguas que ya existan en ImageKit no serán eliminadas por el código. Su limpieza será manual.

---

## 8. Pruebas

- Verificar que el mismo propietario y slot producen el mismo nombre en ejecuciones repetidas.
- Verificar que los nombres de personas no contienen la cédula.
- Verificar que el identificador de persona solo contiene caracteres uppercase hexadecimales.
- Verificar que Ratatouille se resuelve por código `6`, no por el nombre del archivo local.
- Verificar que los tres tipos de Ratatouille generan nombres distintos y ordenados.
- Verificar que `POSTER` y `BANNER` reutilizan el registro existente.
- Verificar que `GALERIA` obtiene un nuevo orden cuando corresponde.
- Ejecutar la suite de `negocio` y revisar que no se modifiquen archivos sensibles.

---

## 9. Criterios De Aceptación

- Ningún nombre nuevo de persona contiene la cédula.
- Ningún nombre nuevo de película depende del título textual para su identidad.
- Repetir una subida del mismo slot no crea un sufijo aleatorio nuevo en ImageKit.
- Ratatouille aparece en la carpeta `unicine/peliculas/Ratatouille`.
- Las tres imágenes de Ratatouille quedan asociadas a la película `6`.
- La configuración secreta no se versiona.
- Las pruebas pasan o cualquier fallo externo de ImageKit queda identificado explícitamente.

---

## 10. Decisiones Clave

| Decisión | Justificación |
|---|---|
| ID de película en el nombre | Es estable y no contiene información personal |
| HMAC para personas | Oculta la cédula sin agregar una columna ni migración |
| Ocho caracteres para HMAC | Cuatro caracteres tienen un espacio de colisión demasiado pequeño |
| `PER` como prefijo visible | Evita revelar el rol exacto de la persona |
| Sobrescritura del mismo slot | Hace idempotentes las pruebas y evita assets duplicados |
| Orden para galería | Permite múltiples imágenes sin perder el orden visual |
| No migrar assets antiguos | Reduce el alcance y evita borrar datos del usuario |
