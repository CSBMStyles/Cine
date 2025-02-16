/*
    NOTE: El dataset es necesario para la ejecución de las pruebas unitarias, entonces hay que tener en cuenta unos puntos importantes:

    1. El dataset constituye un orden, de tal manera que se buscaria insertar primero los datos de las tablas que no tienen dependencias y luego las tablas que tienen dependencias de ellas.

    2. Los datos que se insertan tienen igualmente un orden que debe seguirse segun como esta en la base de datos, eso es en caso de que en la consulta no se tenga declarado los atributos de la tabla a insertar, en este archivo si se encuenta, entonces no nos preocupamos por el orden, pero si hay que tener en cuenta que esten todos los que necesitamos.
*/

insert into administrador (cedula, apellido, nombre, correo, password) values (1001000000, "Barrera", "Cristian", "cristianbarrera100@gmail.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");

insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1119000000, "Bello", "Jhon", "jhona.belloc@uqvirtual.edu.co", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1228000000, "Camacho", "Maria", "mariaf.camachog@uqvirtual.edu.co", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1337000000, "Barrera", "Cristian", "cristians.barreram@uqvirtual.edu.co", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1446000000, "Barragan", "Alejandro", "henrya.barraganp@uqvirtual.edu.co", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1557000000, "Restrepo", "Rodolfo", "rodolfo.restrepo@uqvirtual.edu.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into administrador_teatro (cedula, apellido, nombre, correo, password) values (1667000800, "Quintero", "Jose", "jose.quintero@uqvirtual.edu.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");

insert into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1009000011, 1, "2001-12-14", "Rodrigez", "Pepe", "pepe@hotmail.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1008000022, 0, "1993-11-02", "Perez", "Juan", "juan@outlook.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1007000033, 0, "2000-07-18", "Gomez", "Luis", "luis@yahoo.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1006000044, 1, "2002-01-06", "Martinez", "Maria", "maria@gmail.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");
insert into cliente (cedula, estado, fecha_nacimiento, apellido, nombre, correo, password) values (1005000055, 0, "1991-12-18", "Lopez", "Luisa", "luisa@google.com", "7CJQQBV0lJS3TyRHOD20EgawSfsCplOLL4LQUmpgya3NZU5v0XTlnGt48FIUGH+D");

insert into ciudad (id, nombre) values (1,"Armenia");
insert into ciudad (id, nombre) values (2,"Pereira");
insert into ciudad (id, nombre) values (3,"Cali");
insert into ciudad (id, nombre) values (4,"Bogota");
insert into ciudad (id, nombre) values (5,"Choco");

insert into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (10, 8, 1, 80, '[ 
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

insert into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (12, 8, 2, 96, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]');

insert into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (14, 8, 3, 112, '[ 
            ["D", "D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"]
        ]');

insert into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (10, 8, 4, 80, '[ 
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]');

insert into distribucion_silla (columnas, filas, id, total_sillas, esquema) values (14, 8, 5, 112, '[ 
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D", " ", "D", "D"]
        ]');

insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1119000000, 5, 1, "3185469257", "Carrera 14 # 4-6 Norte");
insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1228000000, 4, 2, "3185749321", "Calle 3 # 1 A 24 Sur");
insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1228000000, 4, 3, "3124720846", "Calle 16 4-2 Centro");
insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1446000000, 3, 4, "3001247585", "Carrera 7 # B 12-13 Sur");
insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1557000000, 2, 5, "3186347896", "Carrera 9 # 4-7 Oeste");
insert into teatro (administrador_teatro_cedula, ciudad_id, id, telefono, direccion) values (1667000800, 1, 6, "3178532410", "Calle 4 # 4-2 Sur");

insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (2, 1, 5, "Atlantis", "XD");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 2, 5, "Floresta", "DOS_DIMENSIONES");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 3, 4, "Gran Plaza Bosa", "IMAX");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 4, 6, "Altavista", "IMAX");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (3, 5, 3, "Multiplaza", "XD");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (4, 6, 2, "Parque Colonia", "DOS_DIMENSIONES");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (5, 7, 1, "Plaza Imperial", "DX4");
insert into sala (distribucion_silla_id, id, teatro_id, nombre, tipo_sala) values (1, 8, 1, "Colon", "IMAX");

insert into horario (id, fecha_fin, fecha_inicio) values (1, "2025-12-14T06:00:00", "2025-12-14T05:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (2, "2025-12-15T09:00:00", "2025-12-15T08:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (3, "2025-12-16T12:00:00", "2025-12-16T11:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (4, "2025-12-17T15:00:00", "2025-12-17T14:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (5, "2025-12-22T18:00:00", "2025-12-22T17:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (6, "2025-12-24T15:00:00", "2025-12-24T14:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (7, "2025-12-24T21:00:00", "2025-12-24T20:00:00");
insert into horario (id, fecha_fin, fecha_inicio) values (8, "2025-12-24T23:00:00", "2025-12-24T22:00:00");

insert into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, estado, sinopsis) values (1, 4.2, 18, "Pinocho", "https://www.youtube.com/embed/TITv1TNi5mI", "PREVENTA", "En un pueblo italiano, el títere de madera Pinocho cobra vida gracias al Hada Azul. Pinocho se esfuerza por comportarse como un niño de carne y hueso, pero su vida da un giro al abandonar a su padre para unirse a un circo.");
insert into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, estado, sinopsis) values (2, 3.5, null, "Dragon Ball: Super Hero", "https://www.youtube.com/embed/lXLPVQ-WrU4", "CARTELERA", "La malvada organización Red Ribbon Army se reforma con nuevos y más poderosos androides, Gamma {1} y Gamma {2} para buscar venganza.");
insert into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, estado, sinopsis) values (3, 4.0, 18, "Smile", "https://www.youtube.com/embed/yhKiQGJop_8", "CARTELERA", "Después de ser testigo de un extraño y traumático accidente que involucró a una paciente, la Dr. Rose Cotter (Sosie Bacon) empieza a experimentar sucesos aterradores que no puede explicarse. A medida que el terror comienza a apoderarse de su vida, Rose debe enfrentarse a su pasado para sobrevivir y escapar de su horrible nueva realidad.");
insert into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, estado, sinopsis) values (4, 4.5, null, "Minions", "https://www.youtube.com/embed/W27moupirnI", "CARTELERA", "En los años 70, Gru crece siendo un gran admirador de <<Los salvajes seis>>, un supergrupo de villanos. Para demostrarles que puede ser malvado, Gru idea un plan con la esperanza de formar parte de la banda. Por suerte, cuenta con la ayuda de sus fieles seguidores, los Minions, siempre dispuestos a sembrar el caos.");
insert into pelicula (id, puntuacion, restriccion_edad, nombre, url_trailer, estado, sinopsis) values (5, 4.1, 18, "Encanto", "https://www.youtube.com/embed/SAH_W9q_brE", "PREVENTA", "En lo alto de las montañas de Colombia hay un lugar encantado llamado Encanto. Aquí, en una casa mágica, vive la extraordinaria familia Madrigal donde todos tienen habilidades fantásticas.");

insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Tom Hanks", "Gepetto");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Cynthia Erivo", "Hada Azul");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(1, "Luke Evans", "Cocinero");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Masako Nozawa", "Son Gohan");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Toshio Furukawa", "Picolo");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Ryō Horikawa", "Vegeta");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(2, "Yūko Minaguchi", "Pan");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(3, "Sosie Bacon", "Rose Cotter");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(3, "Jessie T Usher", "Trevor");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Steve Carell", "Gru");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Alan Arkin", "Wild Knuckles");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(4, "Michelle Yeoh", "Master Chow");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "Stephanie Beatriz", "Mirabel");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "María Cecilia Botero", "Abuela Alma");
insert into pelicula_repartos (pelicula_id, repartos, repartos_key) values(5, "John Leguizamo", "Bruno");

insert into pelicula_imagenes (pelicula_id, imagenes, imagenes_key) values(1, "https://ik.imagekit.io/vfreipue8w/unicine/peliculas/Pinocho/DSC_9462.webp","67aea76d432c476416d2dd46");
insert into pelicula_imagenes (pelicula_id, imagenes, imagenes_key) values(2, "https://res.cloudinary.com/dwu4xtiun/image/upload/v1667775208/unicine/peliculas/Dragon_Ball_Super_Super_Hero_kgaa1r.jpg","unicine/peliculas/Dragon_Ball_Super_Super_Hero_kgaa1r");
insert into pelicula_imagenes (pelicula_id, imagenes, imagenes_key) values(3, "https://res.cloudinary.com/dwu4xtiun/image/upload/v1667775212/unicine/peliculas/Smile_dl13uz.jpg","unicine/peliculas/Smile_dl13uz");
insert into pelicula_imagenes (pelicula_id, imagenes, imagenes_key) values(4, "https://res.cloudinary.com/dwu4xtiun/image/upload/v1667775203/unicine/peliculas/Minions_gqwkoe.jpg","unicine/peliculas/Minions_gqwkoe");
insert into pelicula_imagenes (pelicula_id, imagenes, imagenes_key) values(5, "https://res.cloudinary.com/dwu4xtiun/image/upload/v1667775197/unicine/peliculas/Encanto_fhr4vu.jpg","unicine/peliculas/Encanto_fhr4vu");

insert into pelicula_generos (generos, pelicula_id) values (1, 1);
insert into pelicula_generos (generos, pelicula_id) values (2, 1);
insert into pelicula_generos (generos, pelicula_id) values (4, 1);
insert into pelicula_generos (generos, pelicula_id) values (4, 2);
insert into pelicula_generos (generos, pelicula_id) values (5, 2);
insert into pelicula_generos (generos, pelicula_id) values (1, 2);
insert into pelicula_generos (generos, pelicula_id) values (3, 3);
insert into pelicula_generos (generos, pelicula_id) values (6, 3);
insert into pelicula_generos (generos, pelicula_id) values (8, 3);
insert into pelicula_generos (generos, pelicula_id) values (8, 4);
insert into pelicula_generos (generos, pelicula_id) values (9, 4);
insert into pelicula_generos (generos, pelicula_id) values (9, 5);
insert into pelicula_generos (generos, pelicula_id) values (10, 5);
insert into pelicula_generos (generos, pelicula_id) values (12, 5);

insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (1, 1, 1, 7000, 6, "DOBLADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (2, 2, 2, 6500, 5, "SUBTITULADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (3, 3, 3, 6800, 4, "DOBLADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (4, 4, 4, 6800, 3, "SUBTITULADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (5, 5, 5, 7100, 2, "DOBLADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (6, 6, 4, 6800, 1, "SUBTITULADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (7, 7, 3, 10000, 1, "DOBLADO");
insert into funcion (horario_id, id, pelicula_id, precio, sala_id, formato) values (8, 8, 3, 12000, 1, "SUBTITULADO");

insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (1, '[ 
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"],
            ["D", "D", "D", "D", "D", "D", "D", "D", "D", "D"]
        ]', 80, 0, 0, 1);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (2, '[ 
            ["D", "D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"],
            ["D", "D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D", "D"]
        ]', 96, 0, 0, 2);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (3, '[ 
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
        ]', 112, 0, 0, 3);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (4, '[ 
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
        ]', 80, 0, 0, 4);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (5, '[ 
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
        ]', 112, 0, 0, 5);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (6, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]', 96, 0, 0, 6);
insert into funcion_esquema (id, esquema_temporal, disponibles, mantenimiento, ocupadas, funcion_id) values (7, '[ 
            ["D", "D", " ", " ", "D", "D", "D", "D", " ", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"],
            ["D", "D", " ", "D", "D", "D", "D", "D", "D", " ", "D", "D"]
        ]', 112, 0, 0, 7);

insert into cliente_imagenes (cliente_cedula, imagenes, imagenes_key) values (1009000011,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1668099880/unicine/clientes/cliente1_zfhe3z.jpg", "unicine/clientes/cliente1_zfhe3z");
insert into cliente_imagenes (cliente_cedula, imagenes, imagenes_key) values (1008000022,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1668099880/unicine/clientes/cliente2_rpyvof.jpg", "unicine/clientes/cliente2_rpyvof");
insert into cliente_imagenes (cliente_cedula, imagenes, imagenes_key) values (1007000033,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1668099880/unicine/clientes/cliente3_nad9sh.jpg", "unicine/clientes/cliente3_nad9sh");
insert into cliente_imagenes (cliente_cedula, imagenes, imagenes_key) values (1006000044,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1668100350/unicine/clientes/cliente4_jspbf9.jpg", "unicine/clientes/cliente4_jspbf9");
insert into cliente_imagenes (cliente_cedula, imagenes, imagenes_key) values (1005000055,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1668099880/unicine/clientes/cliente5_akhhrd.jpg", "unicine/clientes/cliente5_akhhrd");

insert into cliente_telefonos (cliente_cedula, telefonos) values (1009000011, "3146832477");
insert into cliente_telefonos (cliente_cedula, telefonos) values (1009000011, "3008245984");
insert into cliente_telefonos (cliente_cedula, telefonos) values (1008000022, "3176857415");
insert into cliente_telefonos (cliente_cedula, telefonos) values (1007000033, "3126845287");
insert into cliente_telefonos (cliente_cedula, telefonos) values (1006000044, "3139847645");
insert into cliente_telefonos (cliente_cedula, telefonos) values (1005000055, "3101036478");

insert into cupon (descuento, id, fecha_vencimiento, criterio, descripcion) values (0.15, 1, "2022-12-25T20:00:00", "Primer registro", "Cupon del 15% de descuento por registrarse por primera vez en nuestra plataforma");
insert into cupon (descuento, id, fecha_vencimiento, criterio, descripcion) values (0.1, 2, "2022-12-19T15:45:00", "Primera compra", "Cupon del 10% de descuento por realizar una primera compra por medio de nuestra plataforma");

insert into confiteria (id, precio, nombre) values (1, 15000, "Combo para Niños");
insert into confiteria (id, precio, nombre) values (2, 49900, "Combo para Pareja");
insert into confiteria (id, precio, nombre) values (3, 29800, "Crispeta + Dos Gaseosas");
insert into confiteria (id, precio, nombre) values (4, 19900, "Gaseosa + Perro Caliente + Crispeta + KitKat");
insert into confiteria (id, precio, nombre) values (5, 6000, "Nevado de Arequipe");

insert into confiteria_imagenes (confiteria_id, imagenes, imagenes_key) values (1,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1667927564/unicine/confiteria/combo_ni%C3%B1os_ydpbay.jpg", "unicine/confiteria/combo_ni%C3%B1os_ydpbay");
insert into confiteria_imagenes (confiteria_id, imagenes, imagenes_key) values (2,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1667927565/unicine/confiteria/combo_para_dos_r5rvxp.jpg", "unicine/confiteria/combo_para_dos_r5rvxp");
insert into confiteria_imagenes (confiteria_id, imagenes, imagenes_key) values (3,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1667927564/unicine/confiteria/crispeta_2gaseosas_vnrpli.jpg", "unicine/confiteria/crispeta_2gaseosas_vnrpli");
insert into confiteria_imagenes (confiteria_id, imagenes, imagenes_key) values (4,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1667927565/unicine/confiteria/combo_para_dos_r5rvxp.jpg", "unicine/confiteria/combo_para_dos_r5rvxp");
insert into confiteria_imagenes (confiteria_id, imagenes, imagenes_key) values (5,"https://res.cloudinary.com/dwu4xtiun/image/upload/v1667927565/unicine/confiteria/nevado_arequipe_afpfeo.jpg", "unicine/confiteria/nevado_arequipe_afpfeo");

insert into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1005000055, 1, 1, 1);
insert into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1006000044, 2, 0, 2);
insert into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1006000044, 1, 1, 3);
insert into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1007000033, 2, 1, 4);
insert into cupon_cliente (cliente_cedula, cupon_id, estado, id) values (1008000022, 1, 0, 5);

insert into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago) values (1008000022, 1, 6, 1, 17000, "2024-12-20T18:32:25", "2024-12-21T20:00:00", "NEQUI");
insert into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago) values (1007000033, 2, 5, 2, 59800, "2024-12-15T14:47:41", "2024-12-15T20:00:00", "VISA");
insert into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago) values (1006000044, 3, 4, 3, 24000, "2024-12-16T19:12:04", "2024-12-20T20:00:00", "NEQUI");
insert into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago) values (1005000055, 4, 3, 4, 54800, "2024-12-17T15:32:07", "2024-12-25T20:00:00", "MASTERCARD");
insert into compra (cliente_cedula, cupon_cliente_id, funcion_id, id, valor_total, fecha_compra, fecha_pelicula, medio_pago) values (1008000022, 5, 2, 5, 72000, "2024-12-16T20:30:12", "2024-12-29T20:00:00", "DAVIPLATA");

insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (1, 5, 1, 6000, 2);
insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (1, 4, 2, 15000, 1);
insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (2, 2, 3, 29900, 2);
insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (3, 1, 4, 24000, 1);
insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (4, 3, 5, 54800, 1);
insert into compra_confiteria (compra_id, confiteria_id, id, precio, unidades) values (5, 1, 6, 24000, 3);

insert into entrada (columna, compra_id, fila, id, precio) values (5, 1, 2, 1, 17000);
insert into entrada (columna, compra_id, fila, id, precio) values (4, 2, 4, 2, 59800);
insert into entrada (columna, compra_id, fila, id, precio) values (3, 3, 5, 3, 24000);
insert into entrada (columna, compra_id, fila, id, precio) values (4, 4, 2, 4, 54800);
insert into entrada (columna, compra_id, fila, id, precio) values (5, 4, 2, 5, 54800);
insert into entrada (columna, compra_id, fila, id, precio) values (5, 5, 3, 6, 72000);

insert into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio) values (1009000011, 1, 4.0, "VISTO");
insert into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio) values (1008000022, 1, 3.0, "EN_ESPERA");
insert into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio) values (1008000022, 2, 5.0, "VISTO");
insert into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio) values (1007000033, 3, 4.0, "FAVORITO");
insert into coleccion (cliente_cedula, pelicula_id, puntuacion, estado_pelicula_propio) values (1006000044, 3, 1.0, "VISTO");