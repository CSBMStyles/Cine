# Graph Report - Cine  (2026-06-28)

## Corpus Check
- 206 files · ~1,116,494 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2402 nodes · 3710 edges · 90 communities detected
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 366 edges (avg confidence: 0.78)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 43|Community 43]]
- [[_COMMUNITY_Community 44|Community 44]]
- [[_COMMUNITY_Community 45|Community 45]]
- [[_COMMUNITY_Community 46|Community 46]]
- [[_COMMUNITY_Community 47|Community 47]]
- [[_COMMUNITY_Community 48|Community 48]]
- [[_COMMUNITY_Community 49|Community 49]]
- [[_COMMUNITY_Community 50|Community 50]]
- [[_COMMUNITY_Community 51|Community 51]]
- [[_COMMUNITY_Community 52|Community 52]]
- [[_COMMUNITY_Community 53|Community 53]]
- [[_COMMUNITY_Community 54|Community 54]]
- [[_COMMUNITY_Community 55|Community 55]]
- [[_COMMUNITY_Community 56|Community 56]]
- [[_COMMUNITY_Community 57|Community 57]]
- [[_COMMUNITY_Community 58|Community 58]]
- [[_COMMUNITY_Community 59|Community 59]]
- [[_COMMUNITY_Community 60|Community 60]]
- [[_COMMUNITY_Community 61|Community 61]]
- [[_COMMUNITY_Community 62|Community 62]]
- [[_COMMUNITY_Community 63|Community 63]]
- [[_COMMUNITY_Community 64|Community 64]]
- [[_COMMUNITY_Community 65|Community 65]]
- [[_COMMUNITY_Community 66|Community 66]]
- [[_COMMUNITY_Community 67|Community 67]]
- [[_COMMUNITY_Community 68|Community 68]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]
- [[_COMMUNITY_Community 79|Community 79]]
- [[_COMMUNITY_Community 80|Community 80]]
- [[_COMMUNITY_Community 81|Community 81]]
- [[_COMMUNITY_Community 82|Community 82]]
- [[_COMMUNITY_Community 83|Community 83]]
- [[_COMMUNITY_Community 103|Community 103]]
- [[_COMMUNITY_Community 104|Community 104]]
- [[_COMMUNITY_Community 105|Community 105]]
- [[_COMMUNITY_Community 106|Community 106]]
- [[_COMMUNITY_Community 107|Community 107]]
- [[_COMMUNITY_Community 108|Community 108]]
- [[_COMMUNITY_Community 109|Community 109]]

## God Nodes (most connected - your core abstractions)
1. `EntradaServicioImp` - 26 edges
2. `ImagenServicioImp` - 23 edges
3. `ImagenServicioImp` - 23 edges
4. `ClienteServicioImp` - 20 edges
5. `CompraServicioImp` - 20 edges
6. `ClienteServicioImp` - 20 edges
7. `SalaServicioImp` - 19 edges
8. `ColeccionServicioImp` - 19 edges
9. `CompraConfiteriaServicioImp` - 19 edges
10. `SalaServicioImp` - 19 edges

## Surprising Connections (you probably didn't know these)
- `Image Service Test` --framework for--> `Spring Boot Test Framework`  [INFERRED]
   →   _Bridges community 1 → community 0_
- `Image Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 1 → community 8_
- `Function Schema Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 0 → community 8_
- `City Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 2 → community 8_
- `Imagen` --implements--> `Serializable`  [EXTRACTED]
  negocio/src/main/java/com/unicine/entity/image/Imagen.java →   _Bridges community 1 → community 9_

## Communities (110 total, 64 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.01
Nodes (89): Customer Entity, Customer Repository, Collection/Catalog Entity, Collection Repository, Purchase/Order Entity, Purchase Repository, Coupon/Discount Entity, Coupon Repository (+81 more)

### Community 1 - "Community 1"
Cohesion: 0.02
Nodes (54): ImageKit Integration Service, Image Entity, Image Repository, Image Service, Image Service Test, Person Base Entity, ColeccionCompuesta, PeliculaDisposicionCompuesta (+46 more)

### Community 2 - "Community 2"
Cohesion: 0.02
Nodes (27): City Entity, City Attribute Validator, City Repository, City Service, CiudadServicioImp, CompraServicio, Theater Entity, Theater Attribute Validator (+19 more)

### Community 3 - "Community 3"
Cohesion: 0.02
Nodes (14): SalaPrecioInit, Imagenable, AdministradorTeatroTest, AdministradorTest, CiudadTest, ClienteTest, ConfiteriaTest, SalaRepo (+6 more)

### Community 4 - "Community 4"
Cohesion: 0.02
Nodes (11): EstadoPeliculaService, PeliculaDisposicionServicioImp, ColeccionTest, CompraConfiteriaTest, CuponTest, FuncionTest, HorarioTest, PeliculaTest (+3 more)

### Community 5 - "Community 5"
Cohesion: 0.03
Nodes (9): HorarioDescuentoInit, PeliculaDisposicionRepo, FuncionRepo, PeliculaDisposicionRepo, HorarioServicioImp, PeliculaDisposicionServicioImp, FuncionRepo, HorarioServicioImp (+1 more)

### Community 6 - "Community 6"
Cohesion: 0.03
Nodes (9): ImageKitConfig, ImageKitService, ImagenRepo, ImagenServicioImp, ProcesadorImagen, RefactorizadorRuta, ImagenRepo, ImagenServicioImp (+1 more)

### Community 7 - "Community 7"
Cohesion: 0.04
Nodes (10): CuponServicio, UnicineException, ApiError, GlobalExceptionHandler, RuntimeException, ColeccionServicioTest, CompraServicioTest, ConfiteriaServicioTest (+2 more)

### Community 8 - "Community 8"
Cohesion: 0.02
Nodes (20): Purchase Detail Data Transfer Object, Spring Data JPA Repository Interface, MedioPago, CompraRepo, AdministradorRepo, AdministradorTeatroRepo, ClienteRepo, ColeccionRepo (+12 more)

### Community 9 - "Community 9"
Cohesion: 0.04
Nodes (16): Administrador, AdministradorServicioImp, AdministradorTeatro, AdministradorTeatroServicioImp, AuthenticationService, ClienteServicioImp, Person Service (Generic), AuthenticationService (+8 more)

### Community 10 - "Community 10"
Cohesion: 0.04
Nodes (60): AuthenticationService, ColeccionCompuesta, PeliculaDisposicionCompuesta, TaskSchedulerConfig, Test Dataset SQL, EmailService, Administrador Entity, AdministradorTeatro Entity (+52 more)

### Community 11 - "Community 11"
Cohesion: 0.05
Nodes (10): ConfiteriaPresentacion, ConfiteriaPresentacionJob, ConfiteriaPresentacionRepo, ConfiteriaPresentacionServicioImp, HistorialPrecioPresentacionRepo, HistorialPrecioPresentacionServicioImp, ConfiteriaPresentacionServicio, HistorialPrecioPresentacionServicio (+2 more)

### Community 12 - "Community 12"
Cohesion: 0.07
Nodes (5): DistribucionSillaParser, EntradaServicioImp, DistribucionSillaServicioTest, EntradaServicioTest, FuncionEsquemaServicioTest

### Community 13 - "Community 13"
Cohesion: 0.05
Nodes (4): PeliculaRepo, PeliculaServicioImp, PeliculaRepo, PeliculaServicioImp

### Community 14 - "Community 14"
Cohesion: 0.07
Nodes (4): CompraServicio, CompraServicioImp, EntradaRepo, EntradaTest

### Community 15 - "Community 15"
Cohesion: 0.09
Nodes (11): FormatoPelicula, Function Intersection Data Transfer Object, GeneroPelicula, ImagenServicioImp, Imagen, Persona, VersionArchivo, FuncionInterseccionMapper (+3 more)

### Community 16 - "Community 16"
Cohesion: 0.08
Nodes (3): CuponClienteRepo, CuponClienteServicioImp, CuponClienteTest

### Community 17 - "Community 17"
Cohesion: 0.07
Nodes (5): DistribucionSillaRepo, DistribucionSillaTest, FuncionEsquemaServicioImp, FuncionEsquemaServicioImp, DistribucionSillaRepo

### Community 18 - "Community 18"
Cohesion: 0.08
Nodes (6): HistorialEstadoPeliculaServicio, EmailService, HistorialEstadoPeliculaRepo, HistorialEstadoPeliculaServicioImp, EmailService, ClienteServicioTest

### Community 19 - "Community 19"
Cohesion: 0.06
Nodes (3): HistorialEstadoPeliculaServicio, PeliculaDisposicionServicio, PeliculaDisposicionServicio

### Community 20 - "Community 20"
Cohesion: 0.09
Nodes (3): DistribucionSillaServicio, DistribucionSillaServicioImp, DistribucionSillaServicioImp

### Community 21 - "Community 21"
Cohesion: 0.1
Nodes (4): ColeccionServicio, ColeccionRepo, ColeccionServicioImp, PeliculaStateChangeEventListener

### Community 23 - "Community 23"
Cohesion: 0.15
Nodes (3): ComentarioServicio, ComentarioRepo, ComentarioServicioImp

### Community 27 - "Community 27"
Cohesion: 0.1
Nodes (7): AuthenticationException, AuthorizationException, BusinessRuleException, ExternalServiceException, ResourceNotFoundException, ValidationException, UnicineException

### Community 62 - "Community 62"
Cohesion: 0.4
Nodes (5): Seat Distribution Service, Seat Distribution Service Test, DistribucionSilla, FuncionEsquemaServicioImp, FuncionEsquema

### Community 63 - "Community 63"
Cohesion: 0.4
Nodes (5): MultiPattern, CiudadAtributoValidator, MultiPatternValidator, PeliculaAtributoValidator, PersonaAtributoValidator

## Ambiguous Edges - Review These
- `Purchase Detail Data Transfer Object` → `MedioPago`  [AMBIGUOUS]
   · relation: could_contain

## Knowledge Gaps
- **19 isolated node(s):** `OnUpdate`, `OnCreate`, `Comentario`, `DetalleCompraDTO`, `DetalleSillaDTO` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **64 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Purchase Detail Data Transfer Object` and `MedioPago`?**
  _Edge tagged AMBIGUOUS (relation: could_contain) - confidence is low._
- **Why does `EntradaServicioImp` connect `Community 12` to `Community 0`, `Community 4`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **Why does `FuncionServicioImp` connect `Community 36` to `Community 0`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Spring Data JPA Repository Interface` (e.g. with `Image Repository` and `Function Schema Repository`) actually correct?**
  _`Spring Data JPA Repository Interface` has 4 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `Customer Entity` (e.g. with `Image Entity` and `Person Base Entity`) actually correct?**
  _`Customer Entity` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `OnUpdate`, `OnCreate`, `Comentario` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.01 - nodes in this community are weakly interconnected._