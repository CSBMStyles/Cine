# Graph Report - Cine  (2026-05-18)

## Corpus Check
- 138 files · ~54,708 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1714 nodes · 2538 edges · 64 communities detected
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 187 edges (avg confidence: 0.76)
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
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]

## God Nodes (most connected - your core abstractions)
1. `ImagenServicioImp` - 23 edges
2. `ImagenServicioImp` - 23 edges
3. `ClienteServicioImp` - 20 edges
4. `ClienteServicioImp` - 20 edges
5. `SalaServicioImp` - 19 edges
6. `SalaServicioImp` - 19 edges
7. `AdministradorTeatroServicioImp` - 18 edges
8. `AdministradorServicioImp` - 18 edges
9. `AdministradorTeatroServicioImp` - 18 edges
10. `AdministradorServicioImp` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Image Service Test` --framework for--> `Spring Boot Test Framework`  [INFERRED]
   →   _Bridges community 0 → community 6_
- `Image Entity` --belongs to--> `Customer Entity`  [INFERRED]
   →   _Bridges community 0 → community 1_
- `Function Schema Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 4 → community 1_
- `City Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 3 → community 1_
- `Room Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 6 → community 1_

## Communities (79 total, 48 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.02
Nodes (52): ImageKit Integration Service, Image Entity, Image Repository, Image Service, Image Service Test, ColeccionCompuesta, PeliculaDisposicionCompuesta, Confiteria (+44 more)

### Community 1 - "Community 1"
Cohesion: 0.02
Nodes (39): Customer Entity, Customer Repository, Collection/Catalog Entity, Collection Repository, Purchase/Order Entity, Purchase Repository, Coupon/Discount Entity, Coupon Repository (+31 more)

### Community 2 - "Community 2"
Cohesion: 0.02
Nodes (14): SalaPrecioInit, Imagenable, AdministradorTeatroTest, AdministradorTest, CiudadTest, ClienteTest, ConfiteriaTest, SalaRepo (+6 more)

### Community 3 - "Community 3"
Cohesion: 0.02
Nodes (24): City Entity, City Attribute Validator, City Repository, City Service, CiudadServicioImp, CompraServicio, Theater Entity, Theater Attribute Validator (+16 more)

### Community 4 - "Community 4"
Cohesion: 0.02
Nodes (26): Distribution Attribute Validator, Seat Distribution Entity, Seat Distribution Repository, Seat Distribution Service, Seat Distribution Service Test, DistribucionSilla, Function Schema Entity, Function Schema Repository (+18 more)

### Community 5 - "Community 5"
Cohesion: 0.03
Nodes (9): EstadoPeliculaService, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, FuncionRepo, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, PeliculaDisposicionServicioTest, FuncionRepo (+1 more)

### Community 6 - "Community 6"
Cohesion: 0.05
Nodes (58): DetalleFuncionMapper, DetalleFuncionesDTO, DetalleFuncionesProjection, DetallePeliculaHorarioDTO, Movie State Enumeration, EstadoPeliculaService, EstadoPelicula, FormatoPelicula (+50 more)

### Community 7 - "Community 7"
Cohesion: 0.04
Nodes (9): ImageKitService, ImagenRepo, ImagenServicioImp, ProcesadorImagen, RefactorizadorRuta, ImagenServicio, ImagenRepo, ImagenServicioImp (+1 more)

### Community 8 - "Community 8"
Cohesion: 0.04
Nodes (18): Administrador, AdministradorServicioImp, AdministradorTeatro, AdministradorTeatroServicioImp, AuthenticationService, ClienteServicioImp, EmailService, Person Service (Generic) (+10 more)

### Community 9 - "Community 9"
Cohesion: 0.04
Nodes (8): DetalleFuncionMapper, FuncionInterseccionMapper, DetalleFuncionesProjection, name(), CompraTest, FuncionServicioImp, FuncionServicioTest, PeliculaServicioTest

### Community 10 - "Community 10"
Cohesion: 0.04
Nodes (60): AuthenticationService, ColeccionCompuesta, PeliculaDisposicionCompuesta, TaskSchedulerConfig, Test Dataset SQL, EmailService, Administrador Entity, AdministradorTeatro Entity (+52 more)

### Community 11 - "Community 11"
Cohesion: 0.05
Nodes (4): PeliculaRepo, PeliculaServicioImp, PeliculaRepo, PeliculaServicioImp

### Community 12 - "Community 12"
Cohesion: 0.07
Nodes (4): TeatroRepo, TeatroServicioImp, TeatroRepo, TeatroServicioImp

### Community 13 - "Community 13"
Cohesion: 0.07
Nodes (3): HorarioDescuentoInit, HorarioServicioImp, HorarioServicioImp

### Community 14 - "Community 14"
Cohesion: 0.16
Nodes (3): EmailService, EmailService, ClienteServicioTest

### Community 40 - "Community 40"
Cohesion: 0.4
Nodes (5): MultiPattern, CiudadAtributoValidator, MultiPatternValidator, PeliculaAtributoValidator, PersonaAtributoValidator

## Ambiguous Edges - Review These
- `Purchase Detail Data Transfer Object` → `MedioPago`  [AMBIGUOUS]
   · relation: could_contain

## Knowledge Gaps
- **21 isolated node(s):** `ColeccionRepo`, `ConfiteriaRepo`, `CuponRepo`, `CompraConfiteriaRepo`, `FuncionEsquemaRepo` (+16 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Purchase Detail Data Transfer Object` and `MedioPago`?**
  _Edge tagged AMBIGUOUS (relation: could_contain) - confidence is low._
- **Why does `ImagenServicioImp` connect `Community 7` to `Community 0`?**
  _High betweenness centrality (0.037) - this node is a cross-community bridge._
- **Why does `ClienteServicioTest` connect `Community 14` to `Community 8`?**
  _High betweenness centrality (0.037) - this node is a cross-community bridge._
- **Why does `PeliculaRepo` connect `Community 11` to `Community 6`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Spring Data JPA Repository Interface` (e.g. with `Image Repository` and `Function Schema Repository`) actually correct?**
  _`Spring Data JPA Repository Interface` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `ColeccionRepo`, `ConfiteriaRepo`, `CuponRepo` to the rest of the system?**
  _21 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.02 - nodes in this community are weakly interconnected._