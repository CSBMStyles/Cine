package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.FuncionEsquema;
import com.unicine.entity.theater.DistribucionSilla;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionEsquemaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.transfer.data.DetalleSillaDTO;
import com.unicine.util.parser.DistribucionSillaParser;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;

import jakarta.validation.Valid;

/**
 * Implementacion del servicio de entradas con logica de validacion de sillas
 * y actualizacion del esquema de ocupacion por funcion.
 */
@Service
@Validated
public class EntradaServicioImp implements EntradaServicio {

    private final EntradaRepo entradaRepo;
    private final CompraRepo compraRepo;
    private final FuncionRepo funcionRepo;
    private final FuncionEsquemaRepo funcionEsquemaRepo;
    private final DistribucionSillaParser distribucionSillaParser;

    public EntradaServicioImp(EntradaRepo entradaRepo, CompraRepo compraRepo, FuncionRepo funcionRepo,
            FuncionEsquemaRepo funcionEsquemaRepo, DistribucionSillaParser distribucionSillaParser) {
        this.entradaRepo = entradaRepo;
        this.compraRepo = compraRepo;
        this.funcionRepo = funcionRepo;
        this.funcionEsquemaRepo = funcionEsquemaRepo;
        this.distribucionSillaParser = distribucionSillaParser;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Entrada> entrada) throws Exception {
        if (entrada.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT017);
        }
    }

    private void validarExiste(List<Entrada> entradas) throws Exception {
        if (entradas.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT017);
        }
    }

    private Compra obtenerCompra(Integer codigo) throws Exception {
        Optional<Compra> compra = compraRepo.findById(codigo);
        if (compra.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT018);
        }
        return compra.get();
    }

    private Funcion obtenerFuncion(Integer codigo) throws Exception {
        Optional<Funcion> funcion = funcionRepo.findById(codigo);
        if (funcion.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.ENT013);
        }
        return funcion.get();
    }

    private FuncionEsquema obtenerFuncionEsquema(Integer codigoFuncion) throws Exception {
        Optional<FuncionEsquema> esquema = funcionEsquemaRepo.findByFuncionCodigo(codigoFuncion);
        if (esquema.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.ENT014);
        }
        return esquema.get();
    }

    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DEL001);
        }
    }

    private void validarFuncionCoincideConCompra(Entrada entrada) {
        if (!entrada.getCompra().getFuncion().getCodigo().equals(entrada.getFuncion().getCodigo())) {
            throw new BusinessRuleException(PurchaseErrorCatalog.REG012);
        }
    }

    private void validarSillaDisponible(Entrada entrada) {
        Funcion funcion = entrada.getFuncion();
        DistribucionSilla distribucion = funcion.getSala().getDistribucionSilla();
        String[][] esquema = distribucionSillaParser.parse(distribucion);

        if (!distribucionSillaParser.existeSilla(esquema, entrada.getFila(), entrada.getColumna())) {
            throw new BusinessRuleException(TheaterErrorCatalog.REG007);
        }

        if (!distribucionSillaParser.esSillaDisponible(esquema, entrada.getFila(), entrada.getColumna())) {
            throw new BusinessRuleException(TheaterErrorCatalog.REG007);
        }
    }

    private void validarSillaNoOcupada(Entrada entrada) {
        boolean ocupada = entradaRepo.existsByFilaAndColumnaAndFuncionCodigo(
                entrada.getFila(), entrada.getColumna(), entrada.getFuncion().getCodigo());
        if (ocupada) {
            throw new BusinessRuleException(PurchaseErrorCatalog.REG010);
        }
    }

    private void ocuparSilla(FuncionEsquema esquema, Entrada entrada) {
        esquema.setOcupadas(esquema.getOcupadas() + 1);
        esquema.setDisponibles(esquema.getDisponibles() - 1);
        actualizarEsquemaTemporal(esquema, entrada, true);
        funcionEsquemaRepo.save(esquema);
    }

    private void liberarSilla(FuncionEsquema esquema, Entrada entrada) {
        esquema.setOcupadas(esquema.getOcupadas() - 1);
        esquema.setDisponibles(esquema.getDisponibles() + 1);
        actualizarEsquemaTemporal(esquema, entrada, false);
        funcionEsquemaRepo.save(esquema);
    }

    private void actualizarEsquemaTemporal(FuncionEsquema esquema, Entrada entrada, boolean ocupar) {
        if (esquema.getEsquemaTemporal() == null) {
            return;
        }

        String[][] matriz = distribucionSillaParser.parse(esquema.getEsquemaTemporal());
        if (ocupar) {
            distribucionSillaParser.marcarSillaOcupada(matriz, entrada.getFila(), entrada.getColumna());
        } else {
            distribucionSillaParser.marcarSillaDisponible(matriz, entrada.getFila(), entrada.getColumna());
        }
        esquema.setEsquemaTemporal(distribucionSillaParser.toJson(matriz));
    }

    private void validarRegistro(Entrada entrada) throws Exception {
        Compra compra = obtenerCompra(entrada.getCompra().getCodigo());
        Funcion funcion = obtenerFuncion(entrada.getFuncion().getCodigo());
        entrada.setCompra(compra);
        entrada.setFuncion(funcion);
        validarFuncionCoincideConCompra(entrada);
        validarSillaDisponible(entrada);
        validarSillaNoOcupada(entrada);
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public Entrada registrar(@Valid Entrada entrada) throws Exception {
        validarRegistro(entrada);
        Entrada guardada = entradaRepo.save(entrada);
        ocuparSilla(obtenerFuncionEsquema(guardada.getFuncion().getCodigo()), guardada);
        return guardada;
    }

    @Override
    public Entrada actualizar(@Valid Entrada entrada) throws Exception {
        Optional<Entrada> buscado = entradaRepo.findById(entrada.getCodigo());
        validarExiste(buscado);

        Entrada existente = buscado.get();
        existente.setPrecio(entrada.getPrecio());

        return entradaRepo.save(existente);
    }

    @Override
    public void eliminar(@Valid Entrada entrada, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        Optional<Entrada> buscado = entradaRepo.findById(entrada.getCodigo());
        validarExiste(buscado);
        entradaRepo.delete(entrada);
        liberarSilla(obtenerFuncionEsquema(entrada.getFuncion().getCodigo()), entrada);
    }

    @Override
    public Optional<Entrada> obtener(Integer codigo) throws Exception {
        Optional<Entrada> buscado = entradaRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Entrada> listar() {
        return entradaRepo.findAll();
    }

    @Override
    public List<Entrada> listarPaginado() {
        return entradaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<Entrada> listarPorCompra(Integer codigoCompra) throws Exception {
        obtenerCompra(codigoCompra);
        List<Entrada> entradas = entradaRepo.findByCompraCodigo(codigoCompra);
        validarExiste(entradas);
        return entradas;
    }

    @Override
    public List<Entrada> listarPorFuncion(Integer codigoFuncion) throws Exception {
        obtenerFuncion(codigoFuncion);
        List<Entrada> entradas = entradaRepo.findByFuncionCodigo(codigoFuncion);
        validarExiste(entradas);
        return entradas;
    }

    @Override
    public List<DetalleSillaDTO> obtenerSillasOcupadas(Integer codigoFuncion) throws Exception {
        obtenerFuncion(codigoFuncion);
        return entradaRepo.obtenerSillasOcupadas(codigoFuncion);
    }
}
