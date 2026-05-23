# Graph Report - Cine  (2026-05-23)

## Corpus Check
- 144 files · ~63,587 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1773 nodes · 2679 edges · 58 communities detected
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 263 edges (avg confidence: 0.77)
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
- [[_COMMUNITY_Community 66|Community 66]]
- [[_COMMUNITY_Community 67|Community 67]]
- [[_COMMUNITY_Community 68|Community 68]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]

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
   →   _Bridges community 0 → community 2_
- `Image Entity` --belongs to--> `Customer Entity`  [INFERRED]
   →   _Bridges community 0 → community 1_
- `Function Schema Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 5 → community 1_
- `City Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 4 → community 1_
- `Room Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 2 → community 1_

## Communities (73 total, 39 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.02
Nodes (51): ImageKit Integration Service, Image Entity, Image Repository, Image Service, Image Service Test, ColeccionCompuesta, PeliculaDisposicionCompuesta, Confiteria (+43 more)

### Community 1 - "Community 1"
Cohesion: 0.02
Nodes (42): Customer Entity, Customer Repository, Collection/Catalog Entity, Collection Repository, Purchase/Order Entity, Purchase Repository, Coupon/Discount Entity, Coupon Repository (+34 more)

### Community 2 - "Community 2"
Cohesion: 0.03
Nodes (55): DetalleFuncionMapper, DetalleFuncionesDTO, DetalleFuncionesProjection, DetallePeliculaHorarioDTO, Movie State Enumeration, EstadoPeliculaService, EstadoPelicula, Function/Movie Showing Entity (+47 more)

### Community 3 - "Community 3"
Cohesion: 0.02
Nodes (14): SalaPrecioInit, Imagenable, AdministradorTeatroTest, AdministradorTest, CiudadTest, ClienteTest, ConfiteriaTest, SalaRepo (+6 more)

### Community 4 - "Community 4"
Cohesion: 0.02
Nodes (22): City Entity, City Attribute Validator, City Repository, City Service, CiudadServicioImp, CompraServicio, Theater Entity, Theater Attribute Validator (+14 more)

### Community 5 - "Community 5"
Cohesion: 0.02
Nodes (25): Distribution Attribute Validator, Seat Distribution Entity, Seat Distribution Repository, Seat Distribution Service, Seat Distribution Service Test, DistribucionSilla, Function Schema Entity, Function Schema Repository (+17 more)

### Community 6 - "Community 6"
Cohesion: 0.03
Nodes (10): ImageKitConfig, ImageKitService, ImagenRepo, ImagenServicioImp, ProcesadorImagen, RefactorizadorRuta, ImagenServicio, ImagenRepo (+2 more)

### Community 7 - "Community 7"
Cohesion: 0.04
Nodes (8): EstadoPeliculaService, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, PeliculaDisposicionServicioTest, FuncionRepo, EstadoPeliculaService

### Community 8 - "Community 8"
Cohesion: 0.04
Nodes (6): CuponTest, FuncionTest, HorarioTest, PeliculaTest, TeatroTest, HorarioServicioTest

### Community 9 - "Community 9"
Cohesion: 0.04
Nodes (14): FormatoPelicula, Function Intersection Data Transfer Object, GeneroPelicula, ImagenServicioImp, Imagen, Persona, VersionArchivo, DetalleFuncionMapper (+6 more)

### Community 10 - "Community 10"
Cohesion: 0.04
Nodes (16): Administrador, AdministradorServicioImp, AdministradorTeatro, AdministradorTeatroServicioImp, AuthenticationService, ClienteServicioImp, EmailService, Person Service (Generic) (+8 more)

### Community 11 - "Community 11"
Cohesion: 0.04
Nodes (60): AuthenticationService, ColeccionCompuesta, PeliculaDisposicionCompuesta, TaskSchedulerConfig, Test Dataset SQL, EmailService, Administrador Entity, AdministradorTeatro Entity (+52 more)

### Community 12 - "Community 12"
Cohesion: 0.05
Nodes (4): PeliculaRepo, PeliculaServicioImp, PeliculaRepo, PeliculaServicioImp

### Community 13 - "Community 13"
Cohesion: 0.05
Nodes (4): HorarioDescuentoInit, FuncionRepo, HorarioServicioImp, HorarioServicioImp

### Community 14 - "Community 14"
Cohesion: 0.08
Nodes (11): AuthenticationException, AuthorizationException, BusinessRuleException, ExternalServiceException, ResourceNotFoundException, UnicineException, ValidationException, ApiError (+3 more)

### Community 15 - "Community 15"
Cohesion: 0.07
Nodes (5): TeatroRepo, TeatroServicioImp, TeatroServicio, TeatroRepo, TeatroServicioImp

### Community 16 - "Community 16"
Cohesion: 0.08
Nodes (3): PeliculaServicio, FuncionEsquemaServicioTest, PeliculaServicio

### Community 18 - "Community 18"
Cohesion: 0.16
Nodes (3): EmailService, EmailService, ClienteServicioTest

### Community 33 - "Community 33"
Cohesion: 0.4
Nodes (5): MultiPattern, CiudadAtributoValidator, MultiPatternValidator, PeliculaAtributoValidator, PersonaAtributoValidator

## Ambiguous Edges - Review These
- `Purchase Detail Data Transfer Object` → `MedioPago`  [AMBIGUOUS]
   · relation: could_contain

## Knowledge Gaps
- **21 isolated node(s):** `ColeccionRepo`, `ConfiteriaRepo`, `CuponRepo`, `CompraConfiteriaRepo`, `FuncionEsquemaRepo` (+16 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **39 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Purchase Detail Data Transfer Object` and `MedioPago`?**
  _Edge tagged AMBIGUOUS (relation: could_contain) - confidence is low._
- **Why does `PeliculaDisposicionServicioImp` connect `Community 7` to `Community 2`?**
  _High betweenness centrality (0.035) - this node is a cross-community bridge._
- **Why does `ImagenServicioImp` connect `Community 6` to `Community 0`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Why does `SalaServicioImp` connect `Community 3` to `Community 2`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Spring Data JPA Repository Interface` (e.g. with `Image Repository` and `Function Schema Repository`) actually correct?**
  _`Spring Data JPA Repository Interface` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `ColeccionRepo`, `ConfiteriaRepo`, `CuponRepo` to the rest of the system?**
  _21 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.02 - nodes in this community are weakly interconnected._