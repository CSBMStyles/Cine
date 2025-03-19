package com.unicine.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration // Marca esta clase como un componente de configuración para ser procesado por el contenedor de Spring
public class TaskSchedulerConfig { // Define una clase de configuración para la programación de tareas

    @Bean // Indica que este método produce un bean que será administrado por Spring
    public TaskScheduler taskScheduler() { 

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler(); // Crea una instancia de programador de tareas con grupo de hilos

        scheduler.setPoolSize(10); // Establece el número de hilos en el grupo en 5, lo que permite ejecutar esa cantidad de tareas simultáneamente
        scheduler.setThreadNamePrefix("Movie State: "); // Establece un prefijo para los nombres de los hilos para una mejor identificación en los registros la razon el "-" es para que no se repita el nombre del hilo

        return scheduler; // Devuelve el bean del programador configurado
    }
}
