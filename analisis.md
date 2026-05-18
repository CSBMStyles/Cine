# Comparativa: Arquitectura Actual vs Arquitectura Hexagonal

## Resumen Ejecutivo

| Aspecto | Actual (Layered) | Hexagonal |
|---------|---------------|-----------|
| **Acoplamiento** | Alto: Servicios dependen directamente de JPA, Spring, librerías externas | Bajo: El dominio no conoce frameworks |
| **Testeabilidad** | Requiere contexto Spring / DB real | Domain testable puro (sin Spring) |
| **Flexibilidad** | Cambiar DB o librería implica tocar negocio | Solo se cambian adaptadores |
| **Complejidad inicial** | Baja | Media (más paquetes, interfaces) |
| **Escalabilidad** | Se degrada con el tamaño | Se mantiene lineal |

---

## Diagrama 1: Arquitectura Actual (Por Capas Tradicionnal)

```mermaid
flowchart TB
    subgraph Cliente["Cliente (HTTP / Consumer)"]
        direction LR
        C1[REST Controller]
        C2[GraphQL]
        C3[CLI]
    end

    subgraph Aplicacion["unicine-negocio (Capas Tradicionales)"]
        direction TB

        subgraph API["api"]
            RES["Respuesta.java<br>wrapper de response"]
        end

        subgraph Transfer["transfer"]
            DTO["DetalleCompraDTO<br>DetalleFuncionesDTO<br>..."]
            MAP["DetalleFuncionMapper<br>FuncionInterseccionMapper"]
        end

        subgraph Servicios["service"]
            direction TB
            SI["CiudadServicio.java<br>FuncionServicio.java<br>..."]
            SIP["*ServicioImp.java<br>~ lógica de negocio + reglas + validaciones ~"]
            SE1["AuthenticationService"]
            SE2["ImageKitService"]
            SE3["EmailService"]
            SE4["EstadoPeliculaService"]
        end

        subgraph Repositorios["repository"]
            REP["*Repo.java (Spring Data JPA)<br>dependencia directa de Hibernate"]
        end

        subgraph Entidades["entity"]
            E["@Entity JPA<br>Pelicula, Cliente, Compra...<br>anotaciones javax.persistence.*"]
        end

        subgraph Util["util"]
            CFG["ImageKitConfig<br>TaskSchedulerConfig"]
            VAL["*Validator.java"]
            INIT["*Init.java"]
        end
    end

    subgraph InfraExterna["Infraestructura Externa"]
        MYSQL[(MySQL)]
        IMG["ImageKit API"]
        MAIL["Servidor SMTP"]
    end

    Cliente --> API
    API --> Transfer
    Transfer --> Servicios
    Servicios --> Repositorios
    Repositorios --> Entidades
    Servicios --> Util
    Repositorios -.->|"Spring Data JPA"| MYSQL
    SE2 -.->|"SDK ImageKit"| IMG
    SE3 -.->|"Spring Mail"| MAIL
    SE1 -.->|"Seguridad + JWT?"| Cliente

    style Servicios fill:#ffcccc
    style Entidades fill:#ffcccc
    style SE2 fill:#ff9999
    style SE3 fill:#ff9999
    style SE4 fill:#ff9999
    style Repositorios fill:#ff9999
    style InfraExterna fill:#e6f3ff
```

### Problemas identificados en la arquitectura actual:

1. **Las entidades JPA son el modelo de dominio**: Están anotadas con `@Entity`, acopladas a Hibernate
2. **Los servicios son God Objects**: Mezclan lógica de negocio, reglas de aplicación, llamadas a servicios externos (Email, ImageKit, Auth)
3. **Dependencia directa de infraestructura**: `ImageKitService`, `EmailService` y `AuthenticationService` viven junto a la lógica de negocio
4. **Los repositorios exponen JPA**: Cualquier cambio de ORM o DB impacta los servicios directamente
5. **Difícil de testear unitariamente**: Requiere levantar contexto Spring + DB real para probar servicios

---

## Diagrama 2: Arquitectura Hexagonal (Propuesta)

```mermaid
flowchart TB
    subgraph AdaptadoresEntrada["🟢 Adaptadores de Entrada (Primary/Driving)"]
        direction LR
        A1["REST Controller"]
        A2["GraphQL Resolver"]
        A3["CLI / Job"]
        A4["Test Unitario"]
    end

    subgraph Core["🔵 Núcleo de Aplicación (Dominio + Puertos)"]
        direction TB

        subgraph Aplicacion["application (Casos de Uso)"]
            UC1["CrearCompraUseCase"]
            UC2["RegistrarClienteUseCase"]
            UC3["ProgramarFuncionUseCase"]
            UC4["..."]
        end

        subgraph Dominio["domain (Modelo Puro)"]
            direction TB
            D_ENT["Entidades de Dominio<br>Pelicula, Cliente, Compra<br>~ clases POJO sin anotaciones ~"]
            D_VAL["Objetos de Valor<br>Email, Dinero, HorarioFuncion"]
            D_REP["Puertos (Interfaces)<br>RepositorioPelicula<br>RepositorioCliente<br>ServicioEmail<br>ServicioImagen<br>ServicioAutenticacion"]
        end
    end

    subgraph AdaptadoresSalida["🟡 Adaptadores de Salida (Secondary/Driven)"]
        direction TB
        AD1["JpaPeliculaRepository<br>implementa RepositorioPelicula"]
        AD2["JpaClienteRepository<br>implementa RepositorioCliente"]
        AD3["ImageKitImageService<br>implementa ServicioImagen"]
        AD4["SmtpEmailService<br>implementa ServicioEmail"]
        AD5["JwtAuthService<br>implementa ServicioAutenticacion"]
        AD6["Jpa*Entity + JpaMapper<br>@Entity para Hibernate"]
    end

    subgraph InfraExterna2["Infraestructura Externa"]
        MYSQL2[(MySQL)]
        IMG2["ImageKit API"]
        MAIL2["Servidor SMTP"]
    end

    AdaptadoresEntrada -->|"llama a"| Aplicacion
    Aplicacion -->|"usa"| Dominio
    Aplicacion -->|"inyecta"| D_REP
    D_REP -->|"implementado por"| AdaptadoresSalida
    AD1 --> AD6
    AD1 -.->|"Spring Data JPA"| MYSQL2
    AD3 -.->|"SDK ImageKit"| IMG2
    AD4 -.->|"Spring Mail"| MAIL2

    style Core fill:#e6f7ff,stroke:#0066cc,stroke-width:3px
    style Dominio fill:#ccebff,stroke:#0066cc
    style Aplicacion fill:#cce5ff
    style D_ENT fill:#b3d9ff
    style D_VAL fill:#b3d9ff
    style D_REP fill:#99ccff
    style AdaptadoresEntrada fill:#d9f2d9
    style AdaptadoresSalida fill:#fff4cc
```

---

## Diagrama 3: Comparación de Flujo de Dependencias

```mermaid
flowchart LR
    subgraph Actual["📍 ACTUAL: Dependencias hacia abajo"]
        direction TB
        CTRL["Controller / API"]
        SRV["Service"]
        REP2["Repository"]
        ENT2["Entity"]
        INF["Infra (Email, ImageKit)"]

        CTRL --> SRV
        SRV --> REP2
        SRV --> INF
        REP2 --> ENT2
    end

    subgraph Hexa["🎯 HEXAGONAL: Dependencias hacia dentro"]
        direction TB
        AD_E["Adaptador Entrada"]
        UC["Use Case"]
        DOM["Dominio (Puertos)"]
        AD_S["Adaptador Salida"]
        INF2["Infra (Email, DB, ImageKit)"]

        AD_E --> UC
        UC --> DOM
        AD_S --> DOM
        AD_S -.-> INF2
    end

    Actual -.-x|"vs"| Hexa
```

---

## Diagrama 4: Mapa de Migración Paso a Paso

```mermaid
flowchart LR
    subgraph Paso1["Paso 1: Extraer Dominio"]
        P1A["entity/*.java"]
        P1B["Crear: domain/model/<br>domain/port/"]

        P1A -->|"Copiar lógica de negocio a modelos puros<br>Extraer interfaces (puertos)"| P1B
    end

    subgraph Paso2["Paso 2: Crear Adaptadores"]
        P2A["repository/*.java<br>service/extend/*.java"]
        P2B["adapter/out/jpa/<br>adapter/out/imagekit/<br>adapter/out/email/"]

        P2A -->|"Mover implementaciones concretas<br>hacia adaptadores que implementan puertos"| P2B
    end

    subgraph Paso3["Paso 3: Casos de Uso"]
        P3A["service/*ServicioImp.java"]
        P3B["application/usecase/<br>~ solo orquestan puertos ~"]

        P3A -->|"Refactorizar: quitar @Service<br>inyectar puertos, no repositorios"| P3B
    end

    subgraph Paso4["Paso 4: Entrada"]
        P4A["api/Respuesta.java"]
        P4B["adapter/in/rest/<br>Controllers con @RestController"]

        P4A -->|"Crear controladores que llaman casos de uso<br>Respuesta como DTO de presentación"| P4B
    end

    Paso1 --> Paso2 --> Paso3 --> Paso4
```

---

## Evaluación: ¿Es complicado para Unicine?

### Factores que la hacen FÁCIL ✅

| Factor | Estado |
|--------|--------|
| **Tamaño** | ~20 entidades, ~15 servicios. Muy manejable. |
| **Módulos** | Solo `negocio`. No hay que coordinar múltiples módulos. |
| **Framework** | Spring Boot facilita inyección de dependencias y configuración de beans. |
| **Tests** | Ya tiene tests de servicio. Sirven de base para validar la migración. |
| **Dependencias externas** | Solo 3: MySQL (JPA), ImageKit, Email. Pocos adaptadores a crear. |

### Desafíos a considerar ⚠️

| Desafío | Solución |
|---------|----------|
| **Entidades JPA anotadas** | Crear clases de dominio puras + mappers a entidades JPA (adapter) |
| **Servicios mezclados** | Separar en: UseCase (orquestación), DomainService (lógica pura), Adapter (infra) |
| **DTOs y Mappers** | Los DTOs actuales pueden quedar en `application/dto/` o `adapter/in/rest/dto` |
| **Configuración Spring** | Usar `@Configuration` en capa `adapter/` para crear beans de adaptadores |

### Esfuerzo estimado

- **Proyectos pequeños** (< 10 entidades): 1-2 días
- **Proyectos medianos** (10-30 entidades, como Unicine): **3-5 días**
- **Proyectos grandes** (> 30 entidades): 1-2 semanas

> 💡 **Recomendación**: No migrar todo de golpe. Empezar por un solo agregado (ej: `Compra` o `Funcion`), validar el patrón, y luego replicar.

---

## Diagrama 5: Vista de Paquetes Propuesta (Hexagonal)

```mermaid
graph TD
    A["com.unicine"]
    A --> B["adapter"]
    A --> C["application"]
    A --> D["domain"]

    B --> B1["in.rest"]
    B --> B2["in.graphql"]
    B --> B3["out.jpa"]
    B --> B4["out.email"]
    B --> B5["out.imagekit"]
    B --> B6["out.auth"]

    C --> C1["usecase"]
    C --> C2["dto"]
    C --> C3["mapper"]
    C --> C4["port/in"]
    C --> C5["port/out"]

    D --> D1["model"]
    D --> D2["service"]
    D --> D3["exception"]

    style D fill:#e6f7ff,stroke:#0066cc,stroke-width:2px
    style C fill:#d9f2d9,stroke:#2d8a2d,stroke-width:2px
    style B fill:#fff4cc,stroke:#cc9900,stroke-width:2px
```

### Regla de oro de paquetes:

```
domain/     ← NO depende de NINGÚN framework (Spring, JPA, etc.)
application/ ← Solo depende de domain/
adapter/    ← Puede depender de application/ y domain/
```

---

## Conclusión

Para **Unicine** la arquitectura hexagonal es:

- **Factible**: El proyecto es de tamaño medio, un solo módulo.
- **Beneficiosa**: Mejorará testeabilidad, desacoplará el dominio de JPA, y permitirá cambiar infraestructura sin tocar negocio.
- **No compleja**: Spring Boot ya tiene DI, solo hay que reorganizar paquetes y extraer interfaces.
- **Progresiva**: Se puede adoptar agregado por agregado sin rewrite total.

> 🚀 **Veredicto**: No es complicado. Es una inversión de 3-5 días que paga dividendos en mantenibilidad y testing.
