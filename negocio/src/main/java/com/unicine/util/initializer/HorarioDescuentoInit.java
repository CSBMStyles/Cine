package com.unicine.util.initializer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class HorarioDescuentoInit {

    private final  Map<String, Double> descuentoDia = new HashMap<>();

    public HorarioDescuentoInit() {
        initializeDescuentoDia();
    }

    private void initializeDescuentoDia() {
        descuentoDia.put("LUN", 0.5); // 50% de descuento
        descuentoDia.put("MAR", 0.2); // 20% de descuento 
        descuentoDia.put("MIÉ", 0.4); // 40% de descuento
        descuentoDia.put("JUE", 0.2); // 20% de descuento
        descuentoDia.put("VIE", 0.0); // 0% de descuento
        descuentoDia.put("SÁB", 0.0); // 0% de descuento
        descuentoDia.put("DOM", 0.2); // 20% de descuento
    }

    public Double obtenerDescuento(String dia) {
        return descuentoDia.get(dia);
    }
}
