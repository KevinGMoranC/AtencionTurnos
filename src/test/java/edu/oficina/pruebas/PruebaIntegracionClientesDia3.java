package edu.oficina.pruebas;

import edu.oficina.modelo.Cliente;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.TurnoService;
import edu.oficina.servicios.ConsultaService;
import java.util.ArrayList;
import java.util.Collections;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Integración de clientes con el servicio real de turnos y sus DAO. */
public final class PruebaIntegracionClientesDia3 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            ClienteService clientes = new ClienteService(p.clientes, p.turnos);
            TurnoService turnos = new TurnoService(p.turnos);
            ConsultaService consultas = new ConsultaService(p.turnos, p.clientes);
            clientes.registrar(1015, "Ana O'Connor", "00123456");
            verificar(p.clientes.buscar(1015).getNombreCompleto().equals("Ana O'Connor"),
                    "Registro real y parámetros SQL con apóstrofo");
            clientes.actualizar(1015, "Ana actualizada", "00001111");
            var pendiente = turnos.generar(1015, 1);
            rechazar(() -> clientes.cambiarEstado(1015, false), "ClienteService ve pendientes creados por TurnoService");
            verificar(clientes.buscar(1015).isActivo() && p.clientes.buscar(1015).isActivo(),
                    "Base y BST conservan estado ante rechazo");
            turnos.cancelar(pendiente.getNumero());
            clientes.cambiarEstado(1015, false);
            verificar(!clientes.buscar(1015).isActivo(), "Desactivación permitida después de cancelar");
            new InicializadorBD(p.conexiones).inicializar();
            ClienteService recargado = new ClienteService(p.clientes, p.turnos);
            verificar(!recargado.buscar(1015).isActivo()
                    && recargado.buscar(1015).getTelefono().equals("00001111"),
                    "Reconstrucción del BST conserva datos y cliente inactivo");
            var claves = recargado.listar().stream().map(Cliente::getCodigo).toList();
            var ordenadas = new ArrayList<>(claves); Collections.sort(ordenadas);
            verificar(claves.equals(ordenadas) && claves.size() == 6, "Inorden completo después de reiniciar");
            verificar(consultas.historialCliente(1015).get(0).getNumero() == pendiente.getNumero(),
                    "El cliente inactivo conserva historial");
            recargado.cambiarEstado(1015, true);
            verificar(turnos.generar(1015, 2).getNumero() == 2, "Cliente reactivado puede solicitar otro turno");
            System.out.println("INTEGRACION CLIENTES DIA 3 CORRECTA");
        }
    }
}
