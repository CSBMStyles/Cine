package com.unicine.util.initializer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.unicine.enumeration.TipoSala;

@Component
public class SalaPrecioInit {

    private final Map<TipoSala, Double> preciosBase = new HashMap<>();

    public SalaPrecioInit() {
        initializePreciosBase();
    }

    private void initializePreciosBase() {
        preciosBase.put(TipoSala.DOS_DIMENSIONES, 5000.0);
        preciosBase.put(TipoSala.TRES_DIMENSIONES, 10000.0);
        preciosBase.put(TipoSala.IMAX, 15000.0);
        preciosBase.put(TipoSala.VIP, 30000.0);
        preciosBase.put(TipoSala.DX4, 22000.0);
        preciosBase.put(TipoSala.XD, 20000.0);
    }

    public Double obtenerPrecio(TipoSala tipoSala) {
        return preciosBase.get(tipoSala);
    }
}
