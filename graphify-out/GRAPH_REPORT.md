# Graph Report - Cine  (2026-05-18)

## Corpus Check
- 138 files · ~54,708 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1241 nodes · 1814 edges · 58 communities detected
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 119 edges (avg confidence: 0.74)
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
2. `ClienteServicioImp` - 20 edges
3. `SalaServicioImp` - 19 edges
4. `AdministradorTeatroServicioImp` - 18 edges
5. `AdministradorServicioImp` - 18 edges
6. `PeliculaTest` - 17 edges
7. `PeliculaDisposicionServicioImp` - 17 edges
8. `CompraTest` - 16 edges
9. `FuncionTest` - 16 edges
10. `PeliculaServicioImp` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Image Entity` --belongs to--> `Customer Entity`  [INFERRED]
   →   _Bridges community 0 → community 1_
- `Image Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 0 → community 11_
- `Function Schema Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 3 → community 11_
- `City Repository` --extended by--> `Spring Data JPA Repository Interface`  [INFERRED]
   →   _Bridges community 2 → community 11_
- `Function/Movie Showing Entity` --references--> `Purchase/Order Entity`  [INFERRED]
   →   _Bridges community 3 → community 1_

## Communities (73 total, 42 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.02
Nodes (51): ImageKit Integration Service, Image Entity, Image Repository, Image Service, ColeccionCompuesta, PeliculaDisposicionCompuesta, Confiteria, FuncionInterseccionDTO (+43 more)

### Community 1 - "Community 1"
Cohesion: 0.03
Nodes (21): Customer Entity, Customer Repository, Collection/Catalog Entity, Collection Repository, Purchase/Order Entity, Purchase Repository, Coupon/Discount Entity, Coupon Repository (+13 more)

### Community 2 - "Community 2"
Cohesion: 0.03
Nodes (19): City Entity, City Attribute Validator, City Repository, City Service, CiudadServicioImp, CompraServicio, Theater Entity, Theater Attribute Validator (+11 more)

### Community 3 - "Community 3"
Cohesion: 0.05
Nodes (57): DetalleFuncionMapper, DetalleFuncionesDTO, DetalleFuncionesProjection, DetallePeliculaHorarioDTO, Distribution Attribute Validator, Seat Distribution Entity, Seat Distribution Repository, Seat Distribution Service (+49 more)

### Community 4 - "Community 4"
Cohesion: 0.03
Nodes (9): Imagenable, AdministradorTeatroTest, AdministradorTest, CiudadTest, ClienteTest, ConfiteriaTest, SalaTest, CiudadServicioTest (+1 more)

### Community 5 - "Community 5"
Cohesion: 0.04
Nodes (17): Movie State Enumeration, EstadoPeliculaService, EstadoPelicula, Movie Entity, Movie Attribute Validator, Movie Disposition Entity, Movie Disposition Repository, Movie Disposition Service (+9 more)

### Community 6 - "Community 6"
Cohesion: 0.04
Nodes (60): AuthenticationService, ColeccionCompuesta, PeliculaDisposicionCompuesta, TaskSchedulerConfig, Test Dataset SQL, EmailService, Administrador Entity, AdministradorTeatro Entity (+52 more)

### Community 7 - "Community 7"
Cohesion: 0.06
Nodes (6): ImageKitService, ProcesadorImagen, RefactorizadorRuta, ImagenServicio, ImagenServicioImp, ImagenServicioTest

### Community 8 - "Community 8"
Cohesion: 0.06
Nodes (6): PeliculaDisposicionServicio, FuncionRepo, PeliculaDisposicionRepo, PeliculaDisposicionServicioImp, PeliculaDisposicionServicioTest, EstadoPeliculaService

### Community 9 - "Community 9"
Cohesion: 0.05
Nodes (6): FuncionInterseccionMapper, DetalleFuncionesProjection, name(), CompraTest, FuncionServicioTest, PeliculaServicioTest

### Community 10 - "Community 10"
Cohesion: 0.08
Nodes (12): Administrador, AdministradorServicioImp, AdministradorTeatro, AdministradorTeatroServicioImp, AuthenticationService, ClienteServicioImp, EmailService, Person Service (Generic) (+4 more)

### Community 11 - "Community 11"
Cohesion: 0.05
Nodes (12): Spring Data JPA Repository Interface, AdministradorRepo, AdministradorTeatroRepo, ClienteRepo, ColeccionRepo, CompraConfiteriaRepo, ConfiteriaRepo, CuponClienteRepo (+4 more)

### Community 12 - "Community 12"
Cohesion: 0.09
Nodes (3): SalaPrecioInit, SalaRepo, SalaServicioImp

### Community 13 - "Community 13"
Cohesion: 0.09
Nodes (3): PeliculaServicio, PeliculaRepo, PeliculaServicioImp

### Community 15 - "Community 15"
Cohesion: 0.11
Nodes (3): DistribucionSillaRepo, DistribucionSillaTest, FuncionEsquemaServicioImp

### Community 34 - "Community 34"
Cohesion: 0.4
Nodes (5): MultiPattern, CiudadAtributoValidator, MultiPatternValidator, PeliculaAtributoValidator, PersonaAtributoValidator

## Ambiguous Edges - Review These
- `Purchase Detail Data Transfer Object` → `MedioPago`  [AMBIGUOUS]
   · relation: could_contain

## Knowledge Gaps
- **16 isolated node(s):** `FuncionEsquemaRepo`, `ColeccionRepo`, `CuponRepo`, `CompraConfiteriaRepo`, `ConfiteriaRepo` (+11 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **42 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Purchase Detail Data Transfer Object` and `MedioPago`?**
  _Edge tagged AMBIGUOUS (relation: could_contain) - confidence is low._
- **Why does `HorarioServicioImp` connect `Community 16` to `Community 3`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `AdministradorTeatroServicioImp` connect `Community 19` to `Community 10`?**
  _High betweenness centrality (0.038) - this node is a cross-community bridge._
- **Why does `SalaServicioImp` connect `Community 12` to `Community 3`?**
  _High betweenness centrality (0.037) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Spring Data JPA Repository Interface` (e.g. with `Image Repository` and `Function Schema Repository`) actually correct?**
  _`Spring Data JPA Repository Interface` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `FuncionEsquemaRepo`, `ColeccionRepo`, `CuponRepo` to the rest of the system?**
  _16 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.02 - nodes in this community are weakly interconnected._