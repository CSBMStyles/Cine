-- data.sql generado desde dataset.sql con INSERT IGNORE para dev
-- Usado solo con perfil dev: spring.sql.init.mode=always + defer-datasource
-- Idempotente: no falla si datos ya existen
/*
    NOTE: El dataset es necesario para la ejecución de las pruebas unitarias, entonces hay que tener en cuenta unos puntos importantes:

    1. El dataset constituye un orden, de tal manera que se buscaria insertar primero los datos de las tablas que no tienen dependencias y luego las tablas que tienen dependencias de ellas.

    2. Los datos que se insertan tienen igualmente un orden que debe seguirse segun como esta en la base de datos, eso es en caso de que en la consulta no se tenga declarado los atributos de la tabla a insertar, en este archivo si se encuenta, entonces no nos preocupamos por el orden, pero si hay que tener en cuenta que esten todos los que necesitamos.
*/

insert ignore into administrador (cedula, apellido, nombre, correo, password) values (1001000000, "Barrera", "Cristian", "cristiansimelot@gmail.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");

insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1119000000, "Bello", "Jhon", "jhona.belloc@uqvirtual.edu.co", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1228000000, "Camacho", "Maria", "mariaf.camachog@uqvirtual.edu.co", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1337000000, "Barrera", "Cristian", "cristians.barreram@uqvirtual.edu.co", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1446000000, "Barragan", "Alejandro", "henrya.barraganp@uqvirtual.edu.co", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1557000000, "Restrepo", "Rodolfo", "rodolfo.restrepo@uqvirtual.edu.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into administrador_teatro (cedula, apellido, nombre, correo, password) values (1667000800, "Quintero", "Jose", "jose.quintero@uqvirtual.edu.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");

insert ignore into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1009000011, 1, "2001-12-14", "Rodrigez", "Pepe", "pepe@hotmail.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1008000022, 0, "1993-11-02", "Perez", "Juan", "juan@outlook.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1007000033, 0, "2000-07-18", "Gomez", "Luis", "luis@yahoo.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1006000044, 1, "2002-01-06", "Martinez", "Maria", "maria@gmail.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");
insert ignore into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1005000055, 0, "1991-12-18", "Lopez", "Luisa", "luisa@google.com", "2yQDxc1BvJs5J/S2LYLAlv5J3hM2ebZfXLucsoK7BQQIJCQPBlTn9sFL+oOOX/D5");

insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1009000011, "3146832477");
insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1009000011, "3008245984");
insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1008000022, "3176857415");
insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1007000033, "3126845287");
insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1006000044, "3139847645");
insert ignore into cliente_telefonos (cliente_cedula, telefonos) values (1005000055, "3101036478");

insert ignore into ciudad (id, nombre) values (1,"Armenia");
insert ignore into ciudad (id, nombre) values (2,"Pereira");
insert ignore into ciudad (id, nombre) values (3,"Cali");
insert ignore into ciudad (id, nombre) values (4,"Bogota");
insert ignore into ciudad (id, nombre) values (5,"Choco");

insert ignore into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (10, 8, 1, 80, '[ 
            [" ", " ", "D", "D", "D", "D", "D", "D", " ", " "],
            [" ", "D", "D", "D", "D", "D", "D", "D", "D", " "],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]');

insert ignore into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (12, 8, 2, 96, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]');

insert ignore into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (14, 8, 3, 112, '[ 
            ["D", "D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"]
        ]');

insert ignore into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (10, 8, 4, 80, '[ 
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]');

insert ignore into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (14, 8, 5, 112, '[ 
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"]
        ]');

insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1119000000, 5, 1, "3185469257", "Carrera 14 # 4-6 Norte");
insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1228000000, 4, 2, "3185749321", "Calle 3 # 1 A 24 Sur");
insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1228000000, 4, 3, "3124720846", "Calle 16 4-2 Centro");
insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1446000000, 3, 4, "3001247585", "Carrera 7 # B 12-13 Sur");
insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1557000000, 2, 5, "3186347896", "Carrera 9 # 4-7 Oeste");
insert ignore into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1667000800, 1, 6, "3178532410", "Calle 4 # 4-2 Sur");

insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (2, 1, 5, "Atlantis", "XD");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 2, 5, "Floresta", "DOS_DIMENSIONES");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 3, 4, "Gran Plaza Bosa", "IMAX");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 4, 6, "Altavista", "IMAX");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (3, 5, 3, "Multiplaza", "XD");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (4, 6, 2, "Parque Colonia", "DOS_DIMENSIONES");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (5, 7, 1, "Plaza Imperial", "DX4");
insert ignore into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 8, 1, "Colon", "IMAX");

insert ignore into horario (id, fecha_fin, fecha_inicio) values (1, "2026-12-14T06:00:00", "2026-12-14T05:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (2, "2026-12-15T09:00:00", "2026-12-15T08:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (3, "2026-12-16T12:00:00", "2026-12-16T11:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (4, "2026-12-17T15:00:00", "2026-12-17T14:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (5, "2026-12-22T18:00:00", "2026-12-22T17:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (6, "2026-12-24T15:00:00", "2026-12-24T14:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (7, "2026-12-24T21:00:00", "2026-12-24T20:00:00");
insert ignore into horario (id, fecha_fin, fecha_inicio) values (8, "2026-12-24T23:00:00", "2026-12-24T22:00:00");

insert ignore into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, sinopsis) values (1, 4.2, 18, "Pinocho", "https://www.youtube.com/embed/TITv1TNi5mI", "En un pueblo italiano, el títere de madera Pinocho cobra vida gracias al Hada Azul. Pinocho se esfuerza por comportarse como un niño de carne y hueso, pero su vida da un giro al abandonar a su padre para unirse a un circo.");
insert ignore into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, sinopsis) values (2, 3.5, null, "Dragon Ball: Super Hero", "https://www.youtube.com/embed/lXLPVQ-WrU4", "La malvada organización Red Ribbon Army se reforma con nuevos y más poderosos androides, Gamma {1} y Gamma {2} para buscar venganza.");
insert ignore into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, sinopsis) values (3, 4.0, 18, "Smile", "https://www.youtube.com/embed/yhKiQGJop_8", "Después de ser testigo de un extraño y traumático accidente que involucró a una paciente, la Dr. Rose Cotter (Sosie Bacon) empieza a experimentar sucesos aterradores que no puede explicarse. A medida que el terror comienza a apoderarse de su vida, Rose debe enfrentarse a su pasado para sobrevivir y escapar de su horrible nueva realidad.");
insert ignore into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, sinopsis) values (4, 4.5, null, "Minions", "https://www.youtube.com/embed/W27moupirnI", "En los años 70, Gru crece siendo un gran admirador de <<Los salvajes seis>>, un supergrupo de villanos. Para demostrarles que puede ser malvado, Gru idea un plan con la esperanza de formar parte de la banda. Por suerte, cuenta con la ayuda de sus fieles seguidores, los Minions, siempre dispuestos a sembrar el caos.");
insert ignore into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, sinopsis) values (5, 4.1, 18, "Encanto", "https://www.youtube.com/embed/SAH_W9q_brE", "En lo alto de las montañas de Colombia hay un lugar encantado llamado Encanto. Aquí, en una casa mágica, vive la extraordinaria familia Madrigal donde todos tienen habilidades fantásticas.");

insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Tom Hanks", "Gepetto");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Cynthia Erivo", "Hada Azul");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Luke Evans", "Cocinero");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Masako Nozawa", "Son Gohan");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Toshio Furukawa", "Picolo");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Ryō Horikawa", "Vegeta");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Yūko Minaguchi", "Pan");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(3, "Sosie Bacon", "Rose Cotter");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(3, "Jessie T Usher", "Trevor");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Steve Carell", "Gru");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Alan Arkin", "Wild Knuckles");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Michelle Yeoh", "Master Chow");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "Stephanie Beatriz", "Mirabel");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "María Cecilia Botero", "Abuela Alma");
insert ignore into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "John Leguizamo", "Bruno");

insert ignore into pelicula_generos (generos, pelicula_id) values (1, 1);
insert ignore into pelicula_generos (generos, pelicula_id) values (2, 1);
insert ignore into pelicula_generos (generos, pelicula_id) values (4, 1);
insert ignore into pelicula_generos (generos, pelicula_id) values (4, 2);
insert ignore into pelicula_generos (generos, pelicula_id) values (5, 2);
insert ignore into pelicula_generos (generos, pelicula_id) values (1, 2);
insert ignore into pelicula_generos (generos, pelicula_id) values (3, 3);
insert ignore into pelicula_generos (generos, pelicula_id) values (6, 3);
insert ignore into pelicula_generos (generos, pelicula_id) values (8, 3);
insert ignore into pelicula_generos (generos, pelicula_id) values (8, 4);
insert ignore into pelicula_generos (generos, pelicula_id) values (9, 4);
insert ignore into pelicula_generos (generos, pelicula_id) values (9, 5);
insert ignore into pelicula_generos (generos, pelicula_id) values (10, 5);
insert ignore into pelicula_generos (generos, pelicula_id) values (12, 5);

-- Registros que tienen funciones
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (1, 3, "ESTRENO", "2026-12-16T11:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (2, 3, "CARTELERA", "2026-12-24T20:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (2, 4, "CARTELERA", "2026-12-24T14:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (2, 5, "CARTELERA", "2026-12-22T17:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (3, 4, "ESTRENO", "2026-12-17T14:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (4, 1, "ESTRENO", "2026-12-14T05:00:00");
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (4, 2, "ESTRENO", "2026-12-15T08:00:00");

-- Registros sin funciones activas
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (1, 1, "PREVENTA", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (1, 2, "PREVENTA", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (1, 4, "PENDIENTE", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (1, 5, "PENDIENTE", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (2, 1, "PENDIENTE", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (2, 2, "PREVENTA", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (3, 1, "FUERA_CARTELERA", null);
insert ignore into pelicula_disposicion (ciudad_id, pelicula_id, estado_pelicula, fecha_funcion_inicial) values (3, 2, "PENDIENTE", null);

insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (1, 1, 1, 7000, 6, "DOBLADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (2, 2, 2, 6500, 5, "SUBTITULADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (3, 3, 3, 6800, 4, "DOBLADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (4, 4, 4, 6800, 3, "SUBTITULADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (5, 5, 5, 7100, 2, "DOBLADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (6, 6, 4, 6800, 1, "SUBTITULADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (7, 7, 3, 10000, 1, "DOBLADO");
insert ignore into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (8, 8, 3, 12000, 1, "SUBTITULADO");

insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (1, '[ 
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]', 80, 0, 0, 1);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (2, '[ 
            ["D", "D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "O", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"]
        ]', 96, 0, 0, 2);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (3, '[ 
            [" ", " ", "D", "D", "D", "D", "D", "D", " ", " "],
            [" ", "D", "D", "O", "O", "D", "D", "D", "D", " "],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]', 112, 0, 0, 3);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (4, '[ 
            [" ", " ", "D", "D", "D", "D", "D", "D", " ", " "],
            [" ", "D", "D", "D", "D", "D", "D", "D", "D", " "],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "O", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]', 80, 0, 0, 4);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (5, '[ 
            [" ", " ", "D", "D", "D", "D", "D", "D", " ", " "],
            [" ", "D", "D", "D", "D", "D", "D", "D", "D", " "],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "O", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]', 112, 0, 0, 5);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (6, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "O", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]', 96, 0, 0, 6);
insert ignore into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (7, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]', 112, 0, 0, 7);

insert ignore into cupon (descuento, id, fecha_vencimiento, criterio, descripcion) values (0.15, 1, "2026-12-25T20:00:00", "Primer registro", "Cupon del 15% de descuento por registrarse por primera vez en nuestra plataforma");
insert ignore into cupon (descuento, id, fecha_vencimiento, criterio, descripcion) values (0.1, 2, "2026-12-19T15:45:00", "Primera compra", "Cupon del 10% de descuento por realizar una primera compra por medio de nuestra plataforma");
insert ignore into cupon (descuento, id, fecha_vencimiento, criterio, descripcion) values (0.25, 4, "2026-11-30T10:00:00", "Sin asignaciones", "Cupon del 25% de descuento sin asignaciones a clientes");

insert ignore into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1005000055, 1, 1, 1);
insert ignore into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1006000044, 2, 0, 2);
insert ignore into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1006000044, 1, 1, 3);
insert ignore into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1007000033, 2, 1, 4);
insert ignore into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1008000022, 1, 0, 5);

insert ignore into confiteria (id, nombre, descripcion, categoria) values (1, "Combo Mega", "Combo grande con crispeta, gaseosa y snack", "COMBO");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (2, "Combo Sencillo", "Combo basico con crispeta y gaseosa", "COMBO");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (3, "Combo Pareja", "Combo para dos personas", "COMBO");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (4, "Crispeta Grande", "Crispeta tamaño grande", "SNACK");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (5, "Crispeta Mediana", "Crispeta tamaño mediano", "SNACK");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (6, "Coca Cola", "Gaseosa sabor cola", "BEBIDA");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (7, "Agua", "Agua embotellada", "BEBIDA");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (8, "Perro Caliente", "Perro caliente tradicional", "SNACK");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (9, "Nachos con Queso", "Nachos cubiertos con queso", "SNACK");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (10, "Chocorramo", "Pastel de chocolate", "DULCE");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (11, "Supercoco", "Dulce de coco", "DULCE");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (12, "Jet", "Chocolatina Jet", "DULCE");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (13, "Helado de Arequipe", "Helado suave sabor arequipe", "OTROS");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (14, "Cafe", "Cafe caliente", "BEBIDA");
insert ignore into confiteria (id, nombre, descripcion, categoria) values (15, "De Toditos Rojo", "Snack De Toditos sabor picante", "SNACK");

insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (1, 1, 1, "UNIDAD", 32000, 32000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (2, 2, 1, "UNIDAD", 18000, 18000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (3, 3, 1, "UNIDAD", 28000, 28000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (4, 4, 1, "UNIDAD", 15000, 15000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (5, 5, 1, "UNIDAD", 10000, 10000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (6, 6, 600, "ML", 8000, 8000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (7, 7, 500, "ML", 4000, 4000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (8, 7, 1, "L", 7000, 7000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (9, 8, 1, "UNIDAD", 12000, 12000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (10, 9, 1, "UNIDAD", 7000, 7000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (11, 10, 1, "UNIDAD", 3500, 3500, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (12, 11, 1, "UNIDAD", 2000, 2000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (13, 12, 1, "UNIDAD", 2000, 2000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (14, 13, 1, "UNIDAD", 7000, 7000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (15, 14, 1, "UNIDAD", 6000, 6000, null);
insert ignore into confiteria_presentacion (id, confiteria_id, porcion, unidad_medida, precio, precio_base, fecha_expiracion_temporal) values (16, 15, 1, "UNIDAD", 3000, 3000, null);


insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67c243ec432c4764163416e0", "https://ik.imagekit.io/vfreipue8w/unicine/personas/clientes/Pepe-Rodrigez", null, null, 1009000011, null, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67c24524432c4764163a0e68", "https://ik.imagekit.io/vfreipue8w/unicine/personas/clientes/Juan-Perez", null, null, 1008000022, null, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67c2459f432c4764163c6ad9", "https://ik.imagekit.io/vfreipue8w/unicine/personas/clientes/Luis-Gomez", null, null, 1007000033, null, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67c24605432c4764163e48db", "https://ik.imagekit.io/vfreipue8w/unicine/personas/clientes/Maria-Martinez", null, null, 1006000044, null, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67c6527c432c4764165ab4e3", "https://ik.imagekit.io/vfreipue8w/unicine/personas/clientes/Luisa-Lopez", null, null, 1005000055, null, null);


insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bba774432c4764163eeda3", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Pinocho/Pinocho-1", null, null, null, 1, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bba775432c4764163ef577", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Pinocho/Pinocho-2", null, null, null, 1, null);

insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bbc062432c476416cd0d17", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Dragon-Ball-Super-Hero/Dragon-Ball-1", null, null, null, 2, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bbc0ce432c476416cfc268", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Dragon-Ball-Super-Hero/Dragon-Ball-Super-Heroes-2", null, null, null, 2, null);

insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf8f97432c47641698a85b", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Smile/Smile-1", null, null, null, 3, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf90e1432c4764169e833a", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Smile/Smile-2", null, null, null, 3, null);

insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf93db432c476416b496ce", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Minions/Minions-1", null, null, null, 4, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf9453432c476416b66395", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Minions/Minions-2", null, null, null, 4, null);

insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf9570432c476416bac374", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Encanto/Encanto-1", null, null, null, 5, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67bf95db432c476416bd3216", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Encanto/Encanto-2", null, null, null, 5, null);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("67ccc3e2432c47641609d9e1", "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Encanto/DSC-3672-M", null, null, null, 5, null);

insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883bb5c7cd75eb86c7472", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/combo/Combo-Mega/Combo-Mega", null, null, null, null, 1);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b85c7cd75eb86c6072", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/combo/Combo-Sencillo/Combo-Simple", null, null, null, null, 2);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883bd5c7cd75eb86c81e1", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/combo/Combo-Pareja/Combo-Parejas", null, null, null, null, 3);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b65c7cd75eb86c59cc", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/snack/Crispeta-Grande/Crispetas-Grandes", null, null, null, null, 4);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b55c7cd75eb86c55be", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/snack/Crispeta-Mediana/Crispetas-Medianas", null, null, null, null, 5);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883be5c7cd75eb86c84ee", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/bebida/Coca-Cola/Coca-Cola", null, null, null, null, 6);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b75c7cd75eb86c5be7", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/bebida/Agua/Botella-Agua", null, null, null, null, 7);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b45c7cd75eb86c5360", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/snack/Perro-Caliente/Perro-Caliente", null, null, null, null, 8);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883ba5c7cd75eb86c69d6", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/snack/Nachos-Con-Queso/Doritos-Queso", null, null, null, null, 9);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b35c7cd75eb86c4f6e", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/dulce/Chocorramo/Chocorramo", null, null, null, null, 10);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b25c7cd75eb86c4c1d", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/dulce/Supercoco/Super-Coco", null, null, null, null, 11);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b15c7cd75eb86c491b", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/dulce/Jet/Chocolatina-Jet", null, null, null, null, 12);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b95c7cd75eb86c631f", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/otros/Helado-De-Arequipe/Helado-Arequipe", null, null, null, null, 13);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883b75c7cd75eb86c5e0c", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/bebida/Cafe/Cafe", null, null, null, null, 14);
insert ignore into imagen (id, url, administrador_cedula, administrador_teatro_cedula, cliente_cedula, pelicula_id, confiteria_id) values ("6a3883bc5c7cd75eb86c7c66", "https://ik.imagekit.io/vfreipue8w/unicine/confiterias/snack/De-Toditos-Rojo/De-Toditos-Rojo", null, null, null, null, 15);


insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1008000022, 1, 6, 1, 17000, "2026-12-20T18:32:25", "2026-12-21T20:00:00", "NEQUI", true);
insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1007000033, 2, 5, 2, 59800, "2026-12-15T14:47:41", "2026-12-15T20:00:00", "VISA", true);
insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1006000044, 3, 4, 3, 24000, "2026-12-16T19:12:04", "2026-12-20T20:00:00", "NEQUI", true);
insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1005000055, 4, 3, 4, 54800, "2026-12-17T15:32:07", "2026-12-25T20:00:00", "MASTERCARD", true);
insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1008000022, 5, 2, 5, 72000, "2026-12-16T20:30:12", "2026-12-29T20:00:00", "DAVIPLATA", false);
insert ignore into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago, estado) values (1005000055, null, 2, 6, 11000, "2026-12-18T10:00:00", "2026-12-30T20:00:00", "NEQUI", true);

insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (1, 5, 1, 6000, 2);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (1, 4, 2, 15000, 1);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (2, 2, 3, 29900, 2);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (3, 1, 4, 24000, 1);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (4, 3, 5, 54800, 1);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (5, 1, 6, 24000, 3);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (6, 7, 7, 4000, 1);
insert ignore into compra_confiteria (compra_id, presentacion_id, id, precio, unidades) values (6, 8, 8, 7000, 1);

insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (5, 1, 2, 6, 1, 17000);
insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (4, 2, 4, 5, 2, 59800);
insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (3, 3, 5, 4, 3, 24000);
insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (4, 4, 2, 3, 4, 54800);
insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (5, 4, 2, 3, 5, 54800);
insert ignore into entrada (columna, compra_id, fila, funcion_id, id, precio) values (5, 5, 3, 2, 6, 72000);

insert ignore into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio, notificacion_activa) values (1009000011, 1, 4.0, "VISTO", true);
insert ignore into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio, notificacion_activa) values (1008000022, 1, 3.0, "EN_ESPERA", false);
insert ignore into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio, notificacion_activa) values (1008000022, 2, 5.0, "VISTO", true);
insert ignore into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio, notificacion_activa) values (1007000033, 3, 4.0, "FAVORITO", true);
insert ignore into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio, notificacion_activa) values (1006000044, 3, 1.0, "VISTO", true);

-- Actualizar funcion_esquema con las entradas precargadas del dataset
update funcion_esquema set ocupadas = 1, disponibles = disponibles - 1 where funcion_id = 2;
update funcion_esquema set ocupadas = 2, disponibles = disponibles - 2 where funcion_id = 3;
update funcion_esquema set ocupadas = 1, disponibles = disponibles - 1 where funcion_id = 4;
update funcion_esquema set ocupadas = 1, disponibles = disponibles - 1 where funcion_id = 5;
update funcion_esquema set ocupadas = 1, disponibles = disponibles - 1 where funcion_id = 6;
