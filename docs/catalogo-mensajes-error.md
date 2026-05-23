# Catalogo de Mensajes de Error - Proyecto UniCine

> **Fase 1 - Excepciones Centralizadas**
> **Tareas:** 1.1 (Auditoria), 1.1.1 (Refactor entidades), 1.2 (Catalogo centralizado)
> **Fecha:** Mayo 2026
> **Estado:** Completado (Tarea 1.2 + Catalogo de Exitos)

---

## Historial de Cambios

### Parte A - Correccion de Validaciones de Entidades (Completado)
- **Commit:** `60a20a5`
- **Cambios:**
  - Corregidos mensajes erroneos y agregados `@NotNull`/`@NotBlank` faltantes (~20 campos)
  - Reemplazados `@Pattern` por `@Size` en `Ciudad` y `Teatro`
  - Eliminados `@Pattern` con regex `^\s*$|(?s).{8,}` en `Persona.password` (ya cubierto por `@NotBlank` + `@Size`)

### Parte B - Refactorizacion de Servicios (Completado)
- **Commit:** `cd3e2d8`
- **Cambios:**
  - Eliminadas 6 clases `*AtributoValidator` (Persona, Pelicula, Ciudad, Sala, Teatro, Distribucion)
  - Refactorizados 9 interfaces de servicio para usar validacion inline (`@NotNull`, `@Positive`, `@NotBlank`)
  - Eliminados metodos `transformar()` de 3 implementaciones
  - Actualizados 15 archivos de test
  - Constructor `DistribucionSilla` ahora requiere `totalSillas`, `filas`, `columnas`

### Parte C - Catalogo Centralizado Errores (Completado)
- **Commits:** `0340c5e`, `5e5f65d`
- **Cambios:**
  - Creada clase `ValidationMessages` con constantes `static final String` para anotaciones Bean Validation
  - Creado enum `ErrorCatalog` con codigos por dominio (VAL###, ENT###, DUP###, AUTH###, REG###, etc.)
  - Migradas todas las entidades a usar `ValidationMessages` (18 archivos)
  - Migrados todos los servicios para usar `ErrorCatalog` en excepciones (14 archivos)
  - HTTP status desacoplado del enum (se asignara en `@ControllerAdvice`)

### Parte D - Catalogo Centralizado Exitos (Completado)
- **Cambios:**
  - Creado enum `SuccessCatalog` con codigos por dominio (SUC###)
  - 40+ mensajes de exito organizados por categoria (Creacion, Actualizacion, Eliminacion, Auth, Compras, etc.)
  - Soporta formateo parametrizado `{0}`, `{1}` para mensajes dinamicos

### Parte E - Excepciones de Negocio Personalizadas (Completado)
- **Cambios:**
  - Creada clase base abstracta `UnicineException` con soporte para `ErrorCatalog` y formateo
  - 6 excepciones especificas:
    - `ResourceNotFoundException` (404) - Recurso no existe
    - `ValidationException` (400) - Datos invalidos / duplicados
    - `BusinessRuleException` (400) - Reglas de negocio violadas
    - `AuthenticationException` (401) - Fallo de autenticacion
    - `AuthorizationException` (403) - Sin permisos
    - `ExternalServiceException` (502) - Fallo en servicio externo
  - Migrados todos los servicios para lanzar excepciones tipadas en lugar de `Exception` generica
  - Cada excepcion integra con `ErrorCatalog` para codigos y mensajes centralizados

### Parte F - Mapeo a Codigos HTTP para API REST (Completado)
- **Cambios:**
  - Creado `ApiError` DTO para respuestas de error estandarizadas (timestamp, status, code, message, path)
  - Creado `GlobalExceptionHandler` con `@RestControllerAdvice`
  - Mapeo de excepciones a HTTP status:
    | Excepcion | HTTP Status |
    |-----------|-------------|
    | ResourceNotFoundException | 404 Not Found |
    | ValidationException | 400 Bad Request |
    | BusinessRuleException | 400 Bad Request |
    | AuthenticationException | 401 Unauthorized |
    | AuthorizationException | 403 Forbidden |
    | ExternalServiceException | 502 Bad Gateway |
    | MethodArgumentNotValidException | 400 Bad Request |
    | ConstraintViolationException | 400 Bad Request |
    | Exception (generica) | 500 Internal Server Error |
  - Respuesta JSON uniforme para todos los errores
  - Logging diferenciado: WARN para errores de cliente, ERROR para errores de servidor

---

## Historial de Cambios

### Parte A - Correccion de Validaciones de Entidades (Completado)
- **Commit:** `60a20a5`
- **Cambios:**
  - Corregidos mensajes copiados: `Persona.apellido`, `Persona.correo` (size mismatch), `Cliente.fechaNacimiento`, `FuncionEsquema.funcion`
  - Agregados `@NotNull`/`@NotBlank` faltantes a ~20 campos numericos y relaciones
  - Reemplazados `@Pattern` por `@Size` en `Ciudad` y `Teatro`
  - Eliminados `@Pattern` con regex `^\s*$|(?s).{8,}` en `Persona.password` (ya cubierto por `@NotBlank` + `@Size`)

### Parte B - Refactorizacion de Servicios (Completado)
- **Commit:** `cd3e2d8`
- **Cambios:**
  - Eliminadas 6 clases `*AtributoValidator` (Persona, Pelicula, Ciudad, Sala, Teatro, Distribucion)
  - Refactorizados 9 interfaces de servicio para usar validacion inline (`@NotNull`, `@Positive`, `@NotBlank`)
  - Eliminados metodos `transformar()` de 3 implementaciones
  - Actualizados 15 archivos de test
  - Constructor `DistribucionSilla` ahora requiere `totalSillas`, `filas`, `columnas`

---

---

## Resumen Ejecutivo

Se realizo un inventario completo de todos los mensajes de error del sistema. Se encontraron **~92 mensajes de validacion en entidades** y **~40 mensajes de error en servicios**, para un total de **~132 mensajes** que deben centralizarse.

| Origen | Cantidad | Tipo |
|--------|----------|------|
| Entidades (Bean Validation) | ~92 | Validacion de entidad |
| Servicios (logica de negocio) | ~40 | Reglas de negocio, sistema, seguridad |
| **Total** | **~132** | |

### Acciones Completadas
- ✅ **Parte A:** Corregidos mensajes erroneos y agregados `@NotNull`/`@NotBlank` faltantes (~20 campos)
- ✅ **Parte B:** Eliminados 6 `*AtributoValidator`, validacion inline en parametros de servicio
- ⏳ **Parte C (actual):** Crear catálogo centralizado `ErrorCatalog` + `ValidationMessages`

---

## Clasificacion por Categoria

### 1. VALIDACION_ENTIDAD (72 mensajes)
Mensajes de las anotaciones de Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`, `@Future`, etc.) en las clases del paquete `entity`.

#### 1.1 Campos obligatorios vacios (`@NotNull`, `@NotBlank`, `@NotEmpty`)
| # | Mensaje | Entidad | Campo |
|---|---------|---------|-------|
| 1 | `La cedula no puede estar vacia` | Persona | cedula |
| 2 | `El nombre no puede estar en blanco` | Persona | nombre |
| 3 | `El apellido no puede estar en blanco` | Persona | apellido |
| 4 | `El correo no puede estar vacio` | Persona | correo |
| 5 | `La contrasena no puede estar en blanco` | Persona | contrasena |
| 6 | `El nombre no puede estar en blanco` | Confiteria | nombre |
| 7 | `El nombre no puede estar en blanco` | Ciudad | nombre |
| 8 | `El nombre no puede estar vacio` | Genero | nombre |
| 9 | `La direccion no puede estar vacia` | Teatro | direccion |
| 10 | `El nombre no puede estar en blanco` | Cupon | descripcion |
| 11 | `La descripcion no puede estar en blanco` | Cupon | descripcion |
| 12 | `El criterio no puede estar en blanco` | Cupon | criterio |
| 13 | `El telefono no puede estar vacio` | Cliente | telefono |
| 14 | `La sinopsis no puede estar vacia` | Pelicula | sinopsis |
| 15 | `La url del trailer no puede tener mas de doscientos caracteres` | Pelicula | urlTrailer |
| 16 | `El estado no puede estar vacio` | CuponCliente | estado |
| 17 | `El estado no puede estar vacio` | Cliente | estado |
| 18 | `El precio no puede estar vacio` | Confiteria | precio |
| 19 | `El precio no puede estar vacio` | Entrada | precio |
| 20 | `El precio no puede estar vacio` | CompraConfiteria | precio |
| 21 | `Las unidades no pueden estar vacias` | CompraConfiteria | unidades |
| 22 | `El descuento no puede estar vacio` | Cupon | descuento |
| 23 | `El medio de pago no puede estar vacio` | Compra | medioPago |
| 24 | `La fecha de compra no puede estar vacia` | Compra | fechaCompra |
| 25 | `La fecha de la pelicula no puede estar vacia` | Compra | fechaPelicula |
| 26 | `El valor total no puede estar vacio` | Compra | valorTotal |
| 27 | `La fecha de inicio no puede estar vacia` | Horario | fechaInicio |
| 28 | `La fecha de fin no puede estar vacia` | Horario | fechaFin |
| 29 | `El formato no puede estar vacio` | Funcion | formato |
| 30 | `El estado no puede estar vacio` | Cliente | estado |
| 31 | `El esquema no puede estar vacio` | FuncionEsquema | esquema |
| 32 | `La funcion no estar vacia` | FuncionEsquema | funcion |
| 33 | `El teatro no puede estar vacio` | Sala | teatro |
| 34 | `La distribucion de sillas no puede estar vacia` | Sala | distribucionSilla |
| 35 | `El nombre no puede estar vacio` | Sala | nombre |
| 36 | `El tipo de sala no puede estar vacio` | Sala | tipoSala |
| 37 | `La ciudad no puede estar vacia` | Teatro | ciudad |
| 38 | `El administrador no puede estar vacio` | Teatro | administrador |
| 39 | `El horario no puede estar vacio` | Funcion | horario |
| 40 | `La sala no puede estar vacia` | Funcion | sala |
| 41 | `La pelicula no puede estar vacia` | Funcion | pelicula |
| 42 | `El nombre no puede estar vacio` | Ciudad | nombre |
| 43 | `El nombre no puede estar en blanco` | Confiteria | nombre |
| 44 | `El apellido no puede estar vacio` | Cliente | apellido |
| 45 | `La fila no puede estar vacia` | Entrada | fila |
| 46 | `La columna no puede estar vacia` | Entrada | columna |
| 47 | `El nombre no puede estar en blanco` | Pelicula | nombre |
| 48 | `El nombre no puede estar en blanco` | PeliculaDisposicion | nombre |
| 49 | `El nombre no puede estar en blanco` | Teatro | nombre |
| 50 | `La fecha de vencimiento no puede estar vacia` | Cupon | fechaVencimiento |

#### 1.2 Formato y longitud (`@Size`, `@Email`, `@Pattern`)
| # | Mensaje | Entidad | Campo |
|---|---------|---------|-------|
| 51 | `El nombre no puede tener mas de cincuenta caracteres` | Persona | nombre |
| 52 | `El nombre no puede tener mas de cincuenta caracteres` | Persona | apellido |
| 53 | `El correo no puede tener mas de cincuenta caracteres` | Persona | correo |
| 54 | `El nombre de la ciudad no debe ser menor a dos caracteres` | Ciudad | nombre |
| 55 | `El nombre de la ciudad no debe pasar los cien caracteres` | Ciudad | nombre |
| 56 | `El nombre de la ciudad solo puede contener letras y espacios` | Ciudad | nombre |
| 57 | `El nombre no puede tener mas de cien caracteres` | Confiteria | nombre |
| 58 | `El nombre no puede tener mas de cien caracteres` | Cupon | criterio |
| 59 | `El criterio no puede tener mas de cien caracteres` | Cupon | criterio |
| 60 | `El telefono no puede tener mas de cinco telefonos` | Cliente | telefonos |
| 61 | `El telefono solo puede contener numeros` | Cliente | telefono |
| 62 | `El telefono debe tener exactamente diez caracteres` | Cliente | telefono |
| 63 | `La contrasena debe tener al menos ocho caracteres` | Persona | contrasena |
| 64 | `La contrasena no puede tener mas de doscientos caracteres` | Persona | contrasena |
| 65 | `La contrasena debe contener al menos una letra mayuscula` | Persona | contrasena |
| 66 | `La contrasena debe contener al menos una letra minuscula` | Persona | contrasena |
| 67 | `La contrasena debe contener al menos un digito` | Persona | contrasena |
| 68 | `La contrasena debe contener al menos un caracter especial` | Persona | contrasena |
| 69 | `El correo no tiene un formato valido` | Persona | correo |
| 70 | `El nombre no puede tener mas de cien caracteres` | Genero | nombre |
| 71 | `El nombre no puede tener mas de cien caracteres` | Pelicula | nombre |
| 72 | `La url del trailer no puede tener mas de doscientos caracteres` | Pelicula | urlTrailer |

#### 1.3 Rangos y valores numericos (`@Positive`, `@PositiveOrZero`, `@Max`)
| # | Mensaje | Entidad | Campo |
|---|---------|---------|-------|
| 73 | `La cedula debe ser un numero positivo` | Persona | cedula |
| 74 | `El precio debe ser un numero positivo o cero` | Confiteria | precio |
| 75 | `El precio debe ser un numero positivo o cero` | Funcion | precio |
| 76 | `El numero de sillas ocupadas debe ser un numero positivo o cero` | FuncionEsquema | sillasOcupadas |
| 77 | `El numero de sillas disponibles debe ser un numero positivo o cero` | FuncionEsquema | sillasDisponibles |
| 78 | `El numero de sillas en mantenimiento debe ser un numero positivo o cero` | FuncionEsquema | sillasMantenimiento |
| 79 | `El total de sillas debe ser un numero positivo` | DistribucionSilla | totalSillas |
| 80 | `El numero de filas debe ser un numero positivo` | DistribucionSilla | numeroFilas |
| 81 | `El numero de columnas debe ser un numero positivo` | DistribucionSilla | numeroColumnas |
| 82 | `El precio debe ser un numero positivo` | Entrada | precio |
| 83 | `La fila debe ser un numero positivo` | Entrada | fila |
| 84 | `La columna debe ser un numero positivo` | Entrada | columna |
| 85 | `El descuento debe ser un numero positivo o cero` | Cupon | descuento |
| 86 | `El descuento no puede ser mayor al total` | Cupon | descuento |
| 87 | `Las unidades deben ser un numero positivo o cero` | CompraConfiteria | unidades |
| 88 | `El valor total debe ser un numero positivo` | Compra | valorTotal |
| 89 | `La puntuacion no puede ser mayor a cinco` | Pelicula | puntuacion |
| 90 | `La puntuacion debe ser un numero positivo` | Pelicula | puntuacion |
| 91 | `La restriccion de edad no puede ser mayor a treinta` | Pelicula | restriccionEdad |
| 92 | `La restriccion de edad debe ser un numero positivo` | Pelicula | restriccionEdad |

#### 1.4 Fechas (`@Past`, `@Future`, `@FutureOrPresent`)
| # | Mensaje | Entidad | Campo |
|---|---------|---------|-------|
| 93 | `La fecha de nacimiento debe estar en el pasado` | Cliente | fechaNacimiento |
| 94 | `La fecha de inicio debe ser en el futuro` | Horario | fechaInicio |
| 95 | `La fecha de fin debe ser en el futuro` | Horario | fechaFin |
| 96 | `La fecha de la pelicula debe estar en el presente o en el futuro` | Compra | fechaPelicula |
| 97 | `La fecha de vencimiento debe estar en el presente o en el futuro` | Cupon | fechaVencimiento |

#### 1.5 Otros validadores
| # | Mensaje | Entidad | Campo |
|---|---------|---------|-------|
| 98 | `El rol del actor no puede tener mas de ciento cincuenta caracteres` | Pelicula | repartos (key) |
| 99 | `El nombre del actor no puede tener mas de ciento cincuenta caracteres` | Pelicula | repartos (value) |
| 100 | `La direccion debe tener al menos cuatro caracteres` | Teatro | direccion |
| 101 | `La direccion no puede tener mas de cien caracteres` | Teatro | direccion |

---

### 2. NEGOCIO_RECURSO_NO_ENCONTRADO (13 mensajes)
Errores cuando un recurso solicitado no existe en la base de datos.

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 102 | `La distribucion de sillas no existe` | DistribucionSillaServicioImp | obtener |
| 103 | `El sala no existe` | SalaServicioImp | obtener |
| 104 | `No existe salas con ese nombre` | SalaServicioImp | listarPorNombre |
| 105 | `No existe ciudad con el codigo proporcionado` | CiudadServicioImp | obtener |
| 106 | `No existe ciudades con ese nombre` | CiudadServicioImp | listarPorNombre |
| 107 | `El teatro no existe` | TeatroServicioImp | obtener |
| 108 | `La pelicula no existe` | PeliculaServicioImp | obtener |
| 109 | `No existe peliculas con ese nombre` | PeliculaServicioImp | listarPorNombre |
| 110 | `La disposicion de pelicula no existe` | PeliculaDisposicionServicioImp | obtener |
| 111 | `El administrador no existe` | AdministradorServicioImp | obtener |
| 112 | `El cliente no existe` | ClienteServicioImp | obtener |
| 113 | `El esquema de la funcion no existe` | FuncionEsquemaServicioImp | obtener |
| 114 | `La funcion no existe` | FuncionServicioImp | obtener |
| 115 | `El horario no existe` | HorarioServicioImp | obtener |

---

### 3. NEGOCIO_CONFLICTO_DUPLICADO (9 mensajes)
Errores cuando se viola una regla de unicidad o ya existe un registro.

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 116 | `El nombre de la sala ya existe en el sala` | SalaServicioImp | registrar / actualizar |
| 117 | `La direccion del teatro ya existe en la ciudad` | TeatroServicioImp | registrar / actualizar |
| 118 | `La pelicula ya existe` | PeliculaServicioImp | registrar |
| 119 | `El nombre que esta ingresando ya existe` | PeliculaServicioImp | actualizar |
| 120 | `Esta cedula ya esta registrada` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | registrar |
| 121 | `Este correo ya esta registrado` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | registrar / actualizar |
| 122 | `La persona ya tiene una imagen, deberia utilizar el metodo actualizar` | ImagenServicioImp | registrar |

---

### 4. NEGOCIO_ELIMINACION (9 mensajes)
Confirmacion de eliminacion fallida.

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 123 | `La eliminacion no fue confirmada` | DistribucionSillaServicioImp | eliminar |
| 124 | `La eliminacion no fue confirmada` | SalaServicioImp | eliminar |
| 125 | `La eliminacion no fue confirmada` | TeatroServicioImp | eliminar |
| 126 | `La eliminacion no fue confirmada` | PeliculaServicioImp | eliminar |
| 127 | `La eliminacion no fue confirmada` | PeliculaDisposicionServicioImp | eliminar |
| 128 | `La eliminacion no fue confirmada` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | eliminar |
| 129 | `La eliminacion no fue confirmada` | ImagenServicioImp | eliminar |
| 130 | `La eliminacion no fue confirmada` | FuncionEsquemaServicioImp | eliminar |
| 131 | `La eliminacion no fue confirmada` | FuncionServicioImp | eliminar |
| 132 | `La eliminacion no fue confirmada` | HorarioServicioImp | eliminar |

---

### 5. SEGURIDAD_AUTENTICACION (7 mensajes)
Errores relacionados con login, credenciales y autenticacion.

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 133 | `Credenciales invalidas` | AuthenticationService | login |
| 134 | `El correo no existe` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | login |
| 135 | `Los datos de autenticacion son incorrectos` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | login |
| 136 | `La contrasena actual es incorrecta` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | cambiarPassword |
| 137 | `La nueva contrasena no puede ser igual a la actual` | AdministradorServicioImp / ClienteServicioImp / AdministradorTeatroServicioImp | cambiarPassword |
| 138 | `El cliente no esta activo, debe activarla con el enlace que fue enviado a su correo` | ClienteServicioImp | login |

---

### 6. NEGOCIO_REGLA_VALIDACION (3 mensajes)
Reglas de negocio especificas que no encajan en otras categorias.

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 139 | `El cliente debe ser mayor de edad para registrarse` | ClienteServicioImp | registrar |
| 140 | `El tamano de la imagen excede el limite permitido de 5 MB. Tamano actual: ... MB` | ImagenServicioImp | registrar/actualizar |

---

### 7. SISTEMA_EXTERNO_IO (8 mensajes)
Errores de comunicacion con servicios externos (ImageKit).

| # | Mensaje | Servicio | Metodo |
|---|---------|----------|--------|
| 141 | `Error al subir la imagen: ...` | ImageKitService | uploadImage |
| 142 | `Error al actualizar la imagen: ...` | ImageKitService | updateImage |
| 143 | `Error al restaurar la version de la imagen: ...` | ImageKitService | restoreVersion |
| 144 | `Error al renombrar el archivo: ...` | ImageKitService | renameFile |
| 145 | `Error al eliminar la imagen: ...` | ImageKitService | deleteImage |
| 146 | `Error al eliminar las imagenes: ...` | ImageKitService | bulkDeleteFiles |
| 147 | `Error al obtener los datos de la imagen: ...` | ImageKitService | getFileDetails |
| 148 | `Error al listar las imagenes: ...` | ImageKitService | listFiles |
| 149 | `Error al listar las versiones de la imagen: ...` | ImageKitService | listFileVersions |

---

## 8. Catalogo de Exitos (SuccessCatalog)

Mensajes de exito para operaciones de negocio, organizados por categoria.

### 8.1 Creacion / Registro (SUC001-SUC015)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC001 | `Registro creado correctamente` | Generico para cualquier entidad |
| SUC002 | `Administrador registrado correctamente` | AdministradorServicio |
| SUC003 | `Cliente registrado correctamente` | ClienteServicio |
| SUC004 | `Administrador de teatro registrado correctamente` | AdministradorTeatroServicio |
| SUC005 | `Pelicula registrada correctamente` | PeliculaServicio |
| SUC006 | `Teatro registrado correctamente` | TeatroServicio |
| SUC007 | `Sala registrada correctamente` | SalaServicio |
| SUC008 | `Ciudad registrada correctamente` | CiudadServicio |
| SUC009 | `Funcion registrada correctamente` | FuncionServicio |
| SUC010 | `Horario registrado correctamente` | HorarioServicio |
| SUC011 | `Esquema de funcion registrado correctamente` | FuncionEsquemaServicio |
| SUC012 | `Cupon registrado correctamente` | CuponServicio |
| SUC013 | `Confiteria registrada correctamente` | ConfiteriaServicio |
| SUC014 | `Distribucion de sillas registrada correctamente` | DistribucionSillaServicio |
| SUC015 | `Imagen subida correctamente` | ImagenServicio |

### 8.2 Actualizacion (SUC101-SUC109)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC101 | `Registro actualizado correctamente` | Generico |
| SUC102 | `Administrador actualizado correctamente` | AdministradorServicio |
| SUC103 | `Cliente actualizado correctamente` | ClienteServicio |
| SUC104 | `Contrasena actualizada correctamente` | PersonaServicio |
| SUC105 | `Pelicula actualizada correctamente` | PeliculaServicio |
| SUC106 | `Teatro actualizado correctamente` | TeatroServicio |
| SUC107 | `Sala actualizada correctamente` | SalaServicio |
| SUC108 | `Imagen actualizada correctamente` | ImagenServicio |
| SUC109 | `Estado de la pelicula actualizado correctamente` | EstadoPeliculaServicio |

### 8.3 Eliminacion (SUC201-SUC207)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC201 | `Registro eliminado correctamente` | Generico |
| SUC202 | `Administrador eliminado correctamente` | AdministradorServicio |
| SUC203 | `Cliente eliminado correctamente` | ClienteServicio |
| SUC204 | `Pelicula eliminada correctamente` | PeliculaServicio |
| SUC205 | `Teatro eliminado correctamente` | TeatroServicio |
| SUC206 | `Sala eliminada correctamente` | SalaServicio |
| SUC207 | `Imagen eliminada correctamente` | ImagenServicio |

### 8.4 Autenticacion / Autorizacion (SUC301-SUC306)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC301 | `Inicio de sesion exitoso` | AuthenticationService |
| SUC302 | `Cierre de sesion exitoso` | AuthenticationService |
| SUC303 | `Token refrescado correctamente` | AuthenticationService |
| SUC304 | `Cuenta activada correctamente` | ClienteServicio |
| SUC305 | `Correo de recuperacion enviado correctamente` | EmailService |
| SUC306 | `Contrasena restablecida correctamente` | PersonaServicio |

### 8.5 Compras / Transacciones (SUC401-SUC405)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC401 | `Compra realizada con exito` | CompraServicio |
| SUC402 | `Pago procesado correctamente` | CompraServicio |
| SUC403 | `Entradas generadas correctamente` | EntradaServicio |
| SUC404 | `Cupon aplicado correctamente` | CuponServicio |
| SUC405 | `Descuento aplicado correctamente` | CuponServicio |

### 8.6 Notificaciones / Email (SUC501-SUC503)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC501 | `Correo enviado correctamente` | EmailService |
| SUC502 | `Notificacion enviada correctamente` | EmailService |
| SUC503 | `Recordatorio programado correctamente` | EmailService |

### 8.7 Operaciones Especificas (SUC601-SUC606)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC601 | `Pelicula agregada a la coleccion correctamente` | ColeccionServicio |
| SUC602 | `Pelicula removida de la coleccion correctamente` | ColeccionServicio |
| SUC603 | `Funcion asignada a la sala correctamente` | FuncionServicio |
| SUC604 | `Disposicion de pelicula configurada correctamente` | PeliculaDisposicionServicio |
| SUC605 | `Imagen restaurada a version anterior correctamente` | ImageKitService |
| SUC606 | `Archivo renombrado correctamente` | ImageKitService |

### 8.8 Generales (SUC901-SUC903)

| Codigo | Mensaje | Uso tipico |
|--------|---------|-----------|
| SUC901 | `Operacion completada con exito` | Cualquier servicio |
| SUC902 | `Solicitud procesada correctamente` | Cualquier servicio |
| SUC903 | `Datos recuperados correctamente` | Consultas / listados |

---

## Mapeo Provisional a Codigos HTTP

> **Nota:** Este mapeo se implementara formalmente en la Fase 3 (Controladores REST) mediante un `@ControllerAdvice`. Ahora es solo una guia de diseno.

| Categoria | Codigo HTTP | Significado | Ejemplos |
|-----------|-------------|-------------|----------|
| VALIDACION_ENTIDAD | **400 Bad Request** | Datos de entrada invalidos | `@NotNull`, `@Size`, `@Email` |
| NEGOCIO_REGLA_VALIDACION | **400 Bad Request** | Regla de negocio violada | Menor de edad, tamano de imagen |
| NEGOCIO_CONFLICTO_DUPLICADO | **409 Conflict** | Recurso duplicado / ya existe | Cedula registrada, correo duplicado |
| NEGOCIO_RECURSO_NO_ENCONTRADO | **404 Not Found** | Recurso no existe | "La pelicula no existe" |
| NEGOCIO_ELIMINACION | **409 Conflict** | No se pudo completar la eliminacion | "Eliminacion no confirmada" |
| SEGURIDAD_AUTENTICACION | **401 Unauthorized** | Credenciales invalidas | "Contrasena incorrecta" |
| SEGURIDAD_AUTORIZACION | **403 Forbidden** | Sin permisos | (Aun no implementado) |
| SISTEMA_EXTERNO_IO | **502 Bad Gateway** | Fallo en servicio externo | ImageKit error |
| SISTEMA_INTERNO | **500 Internal Server Error** | Error inesperado del servidor | (Casos no controlados) |

### Diagrama de flujo de decision HTTP

```
Excepcion lanzada
        |
        v
+------------------+
| Validacion de    |  --> 400 Bad Request
| entidad?         |
+------------------+
        | No
        v
+------------------+
| Recurso no       |  --> 404 Not Found
| encontrado?      |
+------------------+
        | No
        v
+------------------+
| Conflicto /      |  --> 409 Conflict
| duplicado?       |
+------------------+
        | No
        v
+------------------+
| Autenticacion?   |  --> 401 Unauthorized
+------------------+
        | No
        v
+------------------+
| Autorizacion?    |  --> 403 Forbidden
+------------------+
        | No
        v
+------------------+
| Servicio externo?|  --> 502 Bad Gateway
+------------------+
        | No
        v
    500 Internal Server Error
```

---

## Hallazgos y Recomendaciones

### Problemas Identificados

1. **Mensajes duplicados**: "Este correo ya esta registrado" aparece en 3 servicios (Administrador, Cliente, AdministradorTeatro). Debe centralizarse en un mensaje unico: `ERR_EMAIL_EXISTS`.

2. **Mensaje generico repetido**: "La eliminacion no fue confirmada" aparece en 10 servicios. Idealmente cada entidad deberia tener su propio mensaje contextual: "La pelicula no pudo ser eliminada", "La sala no pudo ser eliminada", etc.

3. **Inconsistencia de tildes y mayusculas**: Algunos mensajes usan tildes (`La eliminacion`) y otros no (`La funcion`). Hay que unificar el estilo.

4. **Mensajes sin contexto**: "El nombre de la sala ya existe en el sala" tiene un error gramatical ("en el sala" deberia ser "en el teatro").

5. **Tipos de excepcion inconsistentes**: Algunos servicios lanzan `Exception`, otros `RuntimeException`. Debe unificarse a excepciones de dominio.

6. **Validacion de contrasena fragmentada**: La contrasena tiene 5 anotaciones `@Pattern` separadas. Deberia consolidarse en un solo mensaje o usar una anotacion custom.

### Recomendaciones para la Tarea 1.2 (Catalogo centralizado) ✅ IMPLEMENTADO

1. ✅ **ErrorCatalog** - Enum con codigos por dominio: `VAL###`, `ENT###`, `DUP###`, `AUTH###`, `REG###`, `EXT###`, `GEN###`
2. ✅ **SuccessCatalog** - Enum con codigos por dominio: `SUC###` (Creacion, Actualizacion, Eliminacion, Auth, Compras, etc.)
3. ✅ **ValidationMessages** - Constantes `static final String` para anotaciones Bean Validation
4. ✅ Todos los mensajes hardcodeados han sido reemplazados por referencias a los catalogos
5. ✅ HTTP status desacoplado de los catalogos (se asignara en `@ControllerAdvice`)
6. ✅ Soporte de formateo parametrizado `{0}`, `{1}` en ambos catalogos
7. ⏳ Soporte i18n (messages.properties) - planificado para Fase 2

### Estructura final de catalogos

```
com.unicine.util.validation.catalog/
├── ValidationMessages.java    (Constantes para @NotBlank, @Size, etc.)
├── ErrorCatalog.java          (Enum: errores de negocio)
└── SuccessCatalog.java        (Enum: mensajes de exito)
```

---

## Archivos Auditados

### Entidades (25 archivos)
- `entity/confiteria/Confiteria.java`
- `entity/image/Imagen.java`
- `entity/movie/Coleccion.java`
- `entity/movie/Pelicula.java`
- `entity/movie/PeliculaDisposicion.java`
- `entity/purchase/Compra.java`
- `entity/purchase/CompraConfiteria.java`
- `entity/purchase/Cupon.java`
- `entity/purchase/CuponCliente.java`
- `entity/purchase/Entrada.java`
- `entity/showing/Funcion.java`
- `entity/showing/FuncionEsquema.java`
- `entity/showing/Horario.java`
- `entity/theater/Ciudad.java`
- `entity/theater/DistribucionSilla.java`
- `entity/theater/Sala.java`
- `entity/theater/Teatro.java`
- `entity/user/Administrador.java`
- `entity/user/AdministradorTeatro.java`
- `entity/user/Cliente.java`
- `entity/user/Persona.java`

### Servicios (18 archivos)
- `service/image/ImageKitService.java`
- `service/image/ImagenServicioImp.java`
- `service/movie/EstadoPeliculaService.java`
- `service/movie/PeliculaDisposicionServicioImp.java`
- `service/movie/PeliculaServicioImp.java`
- `service/notification/EmailService.java`
- `service/showing/FuncionEsquemaServicioImp.java`
- `service/showing/FuncionServicioImp.java`
- `service/showing/HorarioServicioImp.java`
- `service/theater/CiudadServicioImp.java`
- `service/theater/DistribucionSillaServicioImp.java`
- `service/theater/SalaServicioImp.java`
- `service/theater/TeatroServicioImp.java`
- `service/user/AdministradorServicioImp.java`
- `service/user/AdministradorTeatroServicioImp.java`
- `service/user/AuthenticationService.java`
- `service/user/ClienteServicioImp.java`

---

## Hallazgos Adicionales (Post-Analisis Profundo)

> Este analisis adicional fue realizado tras una revision exhaustiva del codigo fuente por un agente especializado.
> **Estado:** ✅ Los problemas marcados como **RESUELTO** fueron corregidos en la Parte A o B.

### Problemas de Jerarquia / Solapamiento

| Estado | Entidad | Campo | Problema | Detalle |
|--------|---------|-------|----------|---------|
| ✅ **RESUELTO** | **Persona** | `password` | `@NotBlank` + `@Pattern(^\s*$\|...)` | Eliminados `@Pattern` redundantes en Parte A. `@NotBlank` + `@Size` cubren la validacion. |
| ✅ **RESUELTO** | **Ciudad** | `nombre` | `@NotBlank` + `Pattern.{2,}` | Reemplazados por `@Size(min=2, max=100)` en Parte A. |
| ✅ **RESUELTO** | **Teatro** | `direccion` | `@NotBlank` + `Pattern.{4,}` | Reemplazados por `@Size(min=4, max=100)` en Parte A. |

### Inconsistencias de Mensajes vs Validaciones

| Estado | Entidad | Campo | Anotacion | Problema | Correccion |
|--------|---------|-------|-----------|----------|------------|
| ✅ **RESUELTO** | **Persona** | `apellido` | `@Size(max=50)` | Mensaje copiado de `nombre` | Corregido a "apellido" en Parte A |
| ✅ **RESUELTO** | **Persona** | `correo` | `@Size(max=150)` | Mensaje decia "cincuenta" pero anotacion era 150 | Corregido a "ciento cincuenta" en Parte A |
| ✅ **RESUELTO** | **Cliente** | `fechaNacimiento` | `@NotNull` | Mensaje copiado de `apellido` | Corregido a "fecha de nacimiento" en Parte A |
| ✅ **RESUELTO** | **FuncionEsquema** | `funcion` | `@NotNull` | Faltaba verbo "puede" | Corregido a "La funcion no puede estar vacia" en Parte A |

### Validaciones Insuficientes (Falta @NotNull/@NotBlank)

| Estado | Entidad | Campo | Impacto |
|--------|---------|-------|---------|
| ✅ **RESUELTO** | **Pelicula** | `nombre` | Agregado `@NotBlank` en Parte A |
| ✅ **RESUELTO** | **Pelicula** | `puntuacion` | Agregado `@NotNull` en Parte A |
| ✅ **RESUELTO** | **DistribucionSilla** | `totalSillas`, `filas`, `columnas` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **Funcion** | `precio` | Agregado `@NotNull` en Parte A |
| ✅ **RESUELTO** | **FuncionEsquema** | `sillasOcupadas`, `sillasDisponibles`, `sillasMantenimiento` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **Coleccion** | `cliente`, `pelicula` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **PeliculaDisposicion** | `estadoPelicula`, `pelicula`, `ciudad` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **CuponCliente** | `cupon`, `cliente` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **Compra** | `cliente`, `funcion` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **Entrada** | `compra` | Agregado `@NotNull` en Parte A |
| ✅ **RESUELTO** | **CompraConfiteria** | `compra`, `confiteria` | Agregados `@NotNull` en Parte A |
| ✅ **RESUELTO** | **Imagen** | `codigo`, `url` | Agregados `@NotBlank` en Parte A |

### Errores en Patterns / RegExp

| Estado | Entidad | Campo | Problema | Correccion |
|--------|---------|-------|----------|------------|
| ✅ **RESUELTO** | **Persona** | `password` | `@Pattern` permitia espacios | Eliminados, ahora usa `@NotBlank` + `@Size` |
| ✅ **RESUELTO** | **Persona** | `cedula` | Divergencia entidad/validator | Unificado a `Integer` en Parte B |
| ✅ **RESUELTO** | **Cliente** | `telefonos` | Lista sin `@NotNull` | Agregado `@NotEmpty` en Parte A |

### Problemas con *AtributoValidator

| Estado | Validator | Problema | Correccion |
|--------|-----------|----------|------------|
| ✅ **RESUELTO** | **SalaAtributoValidator** | Copy-paste de Ciudad | **ELIMINADO** en Parte B |
| ✅ **RESUELTO** | **TeatroAtributoValidator** | Solo `@Pattern`, falta `@NotBlank` | **ELIMINADO** en Parte B |
| ✅ **RESUELTO** | **DistribucionAtributoValidator** | Solo `@Pattern`, falta `@NotBlank` | **ELIMINADO** en Parte B |
| ✅ **RESUELTO** | **PersonaAtributoValidator** | `cedula` como `String` vs `Integer` | **ELIMINADO** en Parte B |
| ✅ **RESUELTO** | **PeliculaAtributoValidator** | `@NotNull`/`@NotBlank` faltantes | **ELIMINADO** en Parte B |
| ✅ **RESUELTO** | **CiudadAtributoValidator** | Wrapper innecesario | **ELIMINADO** en Parte B |

### Normalizacion de Mensajes (Inconsistencias Textuales)

- **Tildes inconsistentes**: `"El correo no puede estar vacio"` vs `"La contrasena no puede estar en blanco"`.
- **Mayusculas inconsistentes**: `"El nombre no puede estar en blanco"` vs `"La direccion no puede estar vacia"`.
- **Numeros escritos alternados**: Unos mensajes usan numeros (`50`, `100`) y otros letras (`cincuenta`, `cien`, `doscientos`).

> **Pendiente:** Estas inconsistencias textuales se resolveran en la **Tarea 1.2** (Catalogo centralizado) al unificar todos los mensajes.

---

*Documento generado automaticamente como parte de la Tarea 1.1 del Proyecto UniCine.*
*Actualizado con hallazgos adicionales del analisis profundo.*
