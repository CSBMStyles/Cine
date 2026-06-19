# Graph Report - Cine  (2026-06-18)

## Corpus Check
- 189 files · ~736,080 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2277 nodes · 3517 edges · 88 communities detected
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 349 edges (avg confidence: 0.78)
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
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]
- [[_COMMUNITY_Community 79|Community 79]]
- [[_COMMUNITY_Community 80|Community 80]]
- [[_COMMUNITY_Community 81|Community 81]]
- [[_COMMUNITY_Community 98|Community 98]]
- [[_COMMUNITY_Community 99|Community 99]]
- [[_COMMUNITY_Community 100|Community 100]]
- [[_COMMUNITY_Community 101|Community 101]]
- [[_COMMUNITY_Community 102|Community 102]]
- [[_COMMUNITY_Community 103|Community 103]]
- [[_COMMUNITY_Community 104|Community 104]]

## God Nodes (most connected - your core abstractions)
1. `EntradaServicioImp` - 26 edges
2. `ImagenServicioImp` - 23 edges
3. `ImagenServicioImp` - 23 edges
4. `ClienteServicioImp` - 20 edges
5. `CompraServicioImp` - 20 edges
6. `ClienteServicioImp` - 20 edges
7. `SalaServicioImp` - 19 edges
8. `ColeccionServicioImp` - 19 edges
9. `SalaServicioImp` - 19 edges
10. `ComentarioServicioImp` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Image Service Test` --framework for--> `Spring Boot Test Framework`  [INFERRED]
   →   _Bridges community 0 → community 1_
- `Image Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 0 → community 7_
- `Function Schema Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 1 → community 7_
- `City Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 2 → community 7_
- `SalaServicioImp` --implements--> `SalaServicio`  [EXTRACTED]
  negocio/src/main/java/com/unicine/service/theater/SalaServicioImp.java →   _Bridges community 3 → community 1_

## Communities (105 total, 63 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.01
Nodes (64): Administrador, AdministradorServicioImp, AdministradorTeatroServicioImp, AuthenticationService, ClienteServicioImp, EmailService, ImageKit Integration Service, Image Entity (+56 more)

### Community 1 - "Community 1"
Cohesion: 0.03
Nodes (77): Customer Entity, Customer Repository, Collection/Catalog Entity, Collection Repository, Purchase/Order Entity, Purchase Repository, DetalleFuncionMapper, DetalleFuncionesDTO (+69 more)

### Community 2 - "Community 2"
Cohesion: 0.02
Nodes (27): City Entity, City Attribute Validator, City Repository, City Service, CiudadServicioImp, CompraServicio, Theater Entity, Theater Attribute Validator (+19 more)

### Community 3 - "Community 3"
Cohesion: 0.02
Nodes (14): SalaPrecioInit, Imagenable, AdministradorTeatroTest, AdministradorTest, CiudadTest, ClienteTest, ConfiteriaTest, SalaRepo (+6 more)

### Community 4 - "Community 4"
Cohesion: 0.02
Nodes (13): EstadoPeliculaService, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, ColeccionTest, CompraConfiteriaTest, CuponTest, FuncionTest, HorarioTest (+5 more)

### Community 5 - "Community 5"
Cohesion: 0.03
Nodes (15): AuthenticationException, AuthorizationException, BusinessRuleException, ExternalServiceException, ResourceNotFoundException, UnicineException, ValidationException, ApiError (+7 more)

### Community 6 - "Community 6"
Cohesion: 0.03
Nodes (10): ImageKitConfig, ImageKitService, ImagenRepo, ImagenServicioImp, ProcesadorImagen, RefactorizadorRuta, ImagenServicio, ImagenRepo (+2 more)

### Community 7 - "Community 7"
Cohesion: 0.03
Nodes (17): Purchase Detail Data Transfer Object, Spring Data JPA Repository Interface, MedioPago, CompraRepo, AdministradorRepo, ClienteRepo, ColeccionRepo, CompraConfiteriaRepo (+9 more)

### Community 8 - "Community 8"
Cohesion: 0.05
Nodes (6): CompraServicio, DistribucionSillaParser, CompraServicioImp, EntradaRepo, EntradaServicioImp, EntradaTest

### Community 9 - "Community 9"
Cohesion: 0.04
Nodes (14): FormatoPelicula, Function Intersection Data Transfer Object, GeneroPelicula, ImagenServicioImp, Imagen, Persona, VersionArchivo, FuncionInterseccionMapper (+6 more)

### Community 10 - "Community 10"
Cohesion: 0.05
Nodes (8): Coupon/Discount Entity, Coupon Repository, CuponServicio, CuponRepo, CuponServicio, CuponServicioImp, CuponRepo, CuponServicioTest

### Community 11 - "Community 11"
Cohesion: 0.05
Nodes (6): AdministradorTeatro, AdministradorTeatroRepo, AdministradorTeatroServicioImp, AdministradorTeatroServicioTest, AdministradorTeatroRepo, AdministradorTeatroServicioImp

### Community 12 - "Community 12"
Cohesion: 0.04
Nodes (60): AuthenticationService, ColeccionCompuesta, PeliculaDisposicionCompuesta, TaskSchedulerConfig, Test Dataset SQL, EmailService, Administrador Entity, AdministradorTeatro Entity (+52 more)

### Community 13 - "Community 13"
Cohesion: 0.03
Nodes (5): DistribucionSillaServicio, HorarioServicio, SalaServicio, DistribucionSillaServicio, SalaServicio

### Community 14 - "Community 14"
Cohesion: 0.05
Nodes (4): PeliculaRepo, PeliculaServicioImp, PeliculaRepo, PeliculaServicioImp

### Community 15 - "Community 15"
Cohesion: 0.06
Nodes (5): FuncionRepo, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, PeliculaDisposicionServicioTest, EstadoPeliculaService

### Community 16 - "Community 16"
Cohesion: 0.05
Nodes (7): DistribucionSillaRepo, DistribucionSillaTest, DistribucionSillaServicioTest, FuncionEsquemaServicioImp, FuncionEsquemaServicioTest, FuncionEsquemaServicioImp, DistribucionSillaRepo

### Community 17 - "Community 17"
Cohesion: 0.07
Nodes (3): HorarioDescuentoInit, HorarioServicioImp, HorarioServicioImp

### Community 18 - "Community 18"
Cohesion: 0.08
Nodes (3): CuponClienteRepo, CuponClienteServicioImp, CuponClienteTest

### Community 19 - "Community 19"
Cohesion: 0.08
Nodes (6): HistorialEstadoPeliculaServicio, EmailService, HistorialEstadoPeliculaRepo, HistorialEstadoPeliculaServicioImp, EmailService, ClienteServicioTest

### Community 20 - "Community 20"
Cohesion: 0.09
Nodes (3): DistribucionSillaServicio, DistribucionSillaServicioImp, DistribucionSillaServicioImp

### Community 21 - "Community 21"
Cohesion: 0.1
Nodes (4): ColeccionServicio, ColeccionRepo, ColeccionServicioImp, PeliculaStateChangeEventListener

### Community 22 - "Community 22"
Cohesion: 0.15
Nodes (3): ComentarioServicio, ComentarioRepo, ComentarioServicioImp

### Community 60 - "Community 60"
Cohesion: 0.4
Nodes (5): MultiPattern, CiudadAtributoValidator, MultiPatternValidator, PeliculaAtributoValidator, PersonaAtributoValidator

### Community 61 - "Community 61"
Cohesion: 0.4
Nodes (5): Seat Distribution Service, Seat Distribution Service Test, DistribucionSilla, FuncionEsquemaServicioImp, FuncionEsquema

## Ambiguous Edges - Review These
- `Purchase Detail Data Transfer Object` → `MedioPago`  [AMBIGUOUS]
   · relation: could_contain

## Knowledge Gaps
- **19 isolated node(s):** `OnUpdate`, `OnCreate`, `Comentario`, `DetalleCompraDTO`, `DetalleSillaDTO` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **63 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Purchase Detail Data Transfer Object` and `MedioPago`?**
  _Edge tagged AMBIGUOUS (relation: could_contain) - confidence is low._
- **Why does `SalaServicio` connect `Community 13` to `Community 1`?**
  _High betweenness centrality (0.031) - this node is a cross-community bridge._
- **Why does `SalaServicioImp` connect `Community 3` to `Community 1`?**
  _High betweenness centrality (0.026) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Spring Data JPA Repository Interface` (e.g. with `Image Repository` and `Function Schema Repository`) actually correct?**
  _`Spring Data JPA Repository Interface` has 4 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `Customer Entity` (e.g. with `Image Entity` and `Person Base Entity`) actually correct?**
  _`Customer Entity` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `OnUpdate`, `OnCreate`, `Comentario` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.01 - nodes in this community are weakly interconnected._