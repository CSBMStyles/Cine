-- Limpieza post-test concurrente (4.4.1): el test corre sin transaccion
-- (NOT_SUPPORTED) para que los hilos vean el dataset commiteado.
-- Deja la BD vacia para que el @Sql de cada test reinserte sin duplicados.
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM entrada;
DELETE FROM compra_confiteria;
DELETE FROM compra;
DELETE FROM cupon_cliente;
DELETE FROM cupon;
DELETE FROM coleccion;
DELETE FROM comentario;
DELETE FROM funcion_esquema;
DELETE FROM funcion;
DELETE FROM horario;
DELETE FROM pelicula_disposicion;
DELETE FROM pelicula_generos;
DELETE FROM pelicula_repartos;
DELETE FROM pelicula;
DELETE FROM sala;
DELETE FROM teatro;
DELETE FROM distribucion_silla;
DELETE FROM ciudad;
DELETE FROM cliente_telefonos;
DELETE FROM cliente;
DELETE FROM administrador_teatro;
DELETE FROM administrador;
DELETE FROM imagen;
DELETE FROM confiteria_presentacion;
DELETE FROM confiteria;
SET FOREIGN_KEY_CHECKS = 1;
