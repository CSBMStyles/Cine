package com.unicine.entity.image.interfaced;

public interface Imagenable {
    String getNombre();
    
    String getCarpetaPrefijo();

    // SECTION: Subcarpeta opcional para organizar imagenes por categoria
    
    default String getSubCarpeta() { return null; }

    // !SECTION
}
