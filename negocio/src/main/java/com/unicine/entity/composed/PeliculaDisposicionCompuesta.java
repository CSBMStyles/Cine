package com.unicine.entity.composed;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PeliculaDisposicionCompuesta implements Serializable {

    private Integer ciudad;

    private Integer pelicula;
}

