# Notion Backup — Documentacion Proyecto Cine
**Fecha del backup:** 2026-07-04
**Ejecutado por:** opencode, sesión de reorganización documental

Este archivo contiene los IDs de todas las páginas de Notion que serán
modificadas o eliminadas durante la reorganización. Permite revertir
manualmente los cambios si es necesario.

---

## 1. Raíz del proyecto

| Página | ID | URL | Acción |
|---|---|---|---|
| Documentacion Proyecto Cine | `349fe4bf-7d90-8148-9060-d50b78e98d8d` | https://app.notion.com/p/349fe4bf7d9081489060d50b78e98d8d | mantener raíz, reescribir índice |

## 2. Páginas hijas (estado actual)

| Página | ID | Padre actual | Padre nuevo | Acción |
|---|---|---|---|---|
| Estado y Proximos Pasos | `349fe4bf-7d90-811c-837d-df5b5224997f` | root | root (2. Estado Actual) | renombrar a "Estado Actual" |
| Referencia Tecnica del Proyecto | `349fe4bf-7d90-815f-900e-cbe65d4db564` | root | 4. Referencia Técnica | renombrar a "Stack y Dependencias" |
| Flujo de Test de Service | `349fe4bf-7d90-8181-ba07-fa97f5131111` | root | 5. Arquitectura | mover |
| Explicacion Arquitectonica | `349fe4bf-7d90-8140-b4eb-de198a142739` | root | 5. Arquitectura | mover |
| Flujo de Infraestructura | `349fe4bf-7d90-815d-82d9-fb033c19860a` | root | 5. Arquitectura | mover |
| Glosario y Convenciones | `349fe4bf-7d90-81d1-8959-fd32bac6d529` | root | 1. Empezar aquí | mover + expandir |
| Tutorial de Onboarding Rapido | `349fe4bf-7d90-815b-9280-f8e6fce6ebbe` | root | 1. Empezar aquí | mover |
| Planificacion y Roadmap | `368fe4bf-7d90-8138-b03a-fe93230ddf0c` | root | 3. Roadmap | mantener + actualizar |
| Referencia: CompraConfiteriaServicio | `384fe4bf-7d90-81fd-9cef-de4d1a8f3950` | root | 4. Contratos de Servicios | renombrar a "Contrato: CompraConfiteriaServicio" |
| Bitacora de Errores y Decisiones | `38efe4bf-7d90-81d8-a658-c9ce3aec162e` | root | 7. Bitacora | mantener |

## 3. Páginas anidadas en Planificacion y Roadmap

| Página | ID | Padre actual | Padre nuevo | Acción |
|---|---|---|---|---|
| Catalogos de Mensajes | `369fe4bf-7d90-800f-b90f-ccaad85166da` | Planificacion y Roadmap | 4. Referencia Técnica | mover + renombrar "Catalogo de Codigos de Error" |

## 4. Base de datos

| Recurso | ID | URL | Acción |
|---|---|---|---|
| Tareas Proyecto Cine (database) | `c3e009e5-e87f-4e0d-bcad-c3b0134fc0dc` | https://app.notion.com/p/c3e009e5e87f4e0dbcadc3b0134fc0dc | mover a root, enriquecer esquema |
| Data source de Tareas | `c87886c4-358c-443d-b442-3a7f0696f1be` | collection://c87886c4-358c-443d-b442-3a7f0696f1be | añadir 5 propiedades + 4 vistas |
| Vista por defecto | `a5918dae-4f5d-41fa-a9a7-57e32ed86996` | view://a5918dae-4f5d-41fa-a9a7-57e32ed86996 | mantener + añadir Kanban/Timeline/Componente/Skill |

## 5. Páginas huérfanas (fuera de Documentacion Proyecto Cine)

| Página | ID | Padre actual | Padre nuevo | Acción |
|---|---|---|---|---|
| Incidencias Proyecto Unicine | `19afe4bf-7d90-809f-8e53-e7a83161a742` | workspace root | (eliminar) | eliminar (vacía desde feb 2025) |
| Notas Sueltas | `5ddb4716-fbd4-485f-b1aa-d8f12f75525d` | workspace root | (eliminar tras mover hijos) | eliminar tras mover hijos |
| Lenguajes para Realizar Diagramas | `1901d662-81dd-48bb-9e17-bca830d10474` | Notas Sueltas | 4. Convenciones de Diagramacion | renombrar + mover |
| Tipos de Validadores | `146fe4bf-7d90-8097-99fc-e41ffb578ee9` | Notas Sueltas | 6. Guias Practicas | mover |
| Git Ignore Nota | `163fe4bf-7d90-8012-9e3c-e3d6b26b26cc` | Notas Sueltas | (eliminar) | eliminar (obsoleto) |
| Convocatorias Contraloria | `195fe4bf-7d90-8016-8ede-c569459906f0` | Notas Sueltas | (fuera de scope) | dejar como está, no es del proyecto |
| Projections en JPQL | `19ffe4bf-7d90-8013-bb38-fab60507e25d` | Notas Sueltas | 6. Guias Practicas | mover |
| Implementacion Estados | `1b3fe4bf-7d90-80c0-afba-d9679441e6ae` | Notas Sueltas | (eliminar) | eliminar (supersedida por (1)) |
| Implementacion Estados (1) | `1bbfe4bf-7d90-803a-861a-d7d98a561270` | Notas Sueltas | 5. ADRs/ADR-003 | renombrar + mover |
| Error build path is incomplete | `1bbfe4bf-7d90-8002-a301-ee21789ba824` | Notas Sueltas | (eliminar) | eliminar (issue resuelto) |

## 6. Páginas a crear (no existen aún)

| Página | Padre | Tipo |
|---|---|---|
| 1. Empezar aqui (raíz) | root | page |
| 2. Estado Actual (raíz) | root | page (renombrada) |
| 3. Roadmap (raíz) | root | page |
| 4. Referencia Tecnica (raíz) | root | page |
| 5. Arquitectura (raíz) | root | page |
| 6. Guias Practicas (raíz) | root | page |
| 7. Bitacora (raíz) | root | page |
| Convenciones de Diagramacion | 4. Referencia Tecnica | page |
| Contratos de Servicios (carpeta) | 4. Referencia Tecnica | page |
| Contrato: PeliculaServicio | Contratos de Servicios | page |
| Contrato: CompraServicio | Contratos de Servicios | page |
| Contrato: CuponServicio | Contratos de Servicios | page |
| Contrato: CuponClienteServicio | Contratos de Servicios | page |
| Contrato: EntradaServicio | Contratos de Servicios | page |
| Contrato: ColeccionServicio | Contratos de Servicios | page |
| Contrato: ConfiteriaServicio | Contratos de Servicios | page |
| Contrato: ConfiteriaPresentacionServicio | Contratos de Servicios | page |
| Contrato: HistorialPrecioPresentacionServicio | Contratos de Servicios | page |
| Contrato: ClienteServicio | Contratos de Servicios | page |
| Contrato: AdministradorServicio | Contratos de Servicios | page |
| Contrato: AdministradorTeatroServicio | Contratos de Servicios | page |
| Contrato: PersonaServicio | Contratos de Servicios | page |
| Contrato: AuthenticationService | Contratos de Servicios | page |
| Contrato: EmailService | Contratos de Servicios | page |
| Contrato: ImageKitService | Contratos de Servicios | page |
| Contrato: ImagenServicio | Contratos de Servicios | page |
| Contrato: EstadoPeliculaService | Contratos de Servicios | page |
| Decisiones Arquitectonicas (ADRs) | 5. Arquitectura | page |
| ADR-001 Migracion catalogos de error | ADRs | page |
| ADR-002 Tests integrados vs mocks | ADRs | page |
| ADR-003 Implementacion gestion de estados | ADRs | page (mover desde Implementacion Estados (1)) |
| ADR-004 Scheduler @Transactional en lambda | ADRs | page |
| Como agregar un nuevo Servicio | 6. Guias Practicas | page |
| Como agregar una nueva Entidad | 6. Guias Practicas | page |
| Como agregar una nueva Excepcion | 6. Guias Practicas | page |
| Como escribir Tests del proyecto | 6. Guias Practicas | page |

## 7. Cómo revertir (manual)

Para revertir un cambio en Notion:
- **Página movida**: usar Notion UI > "Move to" > seleccionar padre original
- **Página renombrada**: usar `update-page` con `command: update_properties` y `properties: {title: "..."}`
- **Página eliminada**: restaurar desde Trash de Notion (30 días)
- **DB modificada**: editar el esquema con `update-data-source` y restaurar propiedades
- **Vistas de DB**: recrear con `create-view` o `update-view`

## 8. Estado del repo (no se modifica)

- Working tree: limpio (solo cambios de `graphify-out/` sin commitear, no relacionados a este backup)
- HEAD: `0c9b746 chore(graphify): update knowledge graph after transversal services refactor`
- No se commitean cambios en este archivo (referencia informativa de la sesión)
