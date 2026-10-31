package edu.oficina.pruebas;

import edu.oficina.modelo.Cliente;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.TurnoService;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Casos límite y operaciones de cliente con DAO y servicio de turnos reales. */
public final class PruebaClientesDia4 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            ClienteService clientes = new ClienteService(p.clientes, p.turnos);
            TurnoService turnos = new TurnoService(p.turnos);
            clientes.registrar(1, "  Zoë O'Connor  ", "00000000");
            clientes.registrar(Integer.MAX_VALUE, "Código máximo", "99999999");
            verificar(clientes.buscar(1).getNombreCompleto().equals("Zoë O'Connor")
                    && clientes.buscar(Integer.MAX_VALUE).getCodigo() == Integer.MAX_VALUE,
                    "Código positivo mínimo/máximo y nombre con acento/apóstrofo");
            for (int codigo : new int[]{0,-1,Integer.MIN_VALUE}) {
                rechazar(() -> clientes.registrar(codigo,"Inválido","12345678"), "Código no positivo " + codigo);
            }
            rechazar(() -> clientes.registrar(2000,null,"12345678"), "Nombre null");
            rechazar(() -> clientes.registrar(2000,"   ","12345678"), "Nombre en blanco");
            for (String telefono : new String[]{null,"","1234567","123456789","12A45678",
                    "+1234567","１２３４５６７８","١٢٣٤٥٦٧٨","1234 678"}) {
                rechazar(() -> clientes.registrar(2000,"Nombre",telefono), "Teléfono inválido: " + telefono);
            }
            verificar(p.clientes.listar().size() == 7, "Los registros inválidos no llegan a SQLite");
            clientes.registrar(1010,"Cliente de estados","00123456");
            clientes.cambiarEstado(1010,false);
            rechazar(() -> clientes.registrar(1010,"Duplicado inactivo","12345678"),
                    "Un código inactivo sigue siendo único");
            clientes.actualizar(1010,"Inactivo actualizado","00001111");
            verificar(!clientes.buscar(1010).isActivo()
                    && !p.clientes.buscar(1010).isActivo(), "Actualizar datos no reactiva al cliente");
            clientes.cambiarEstado(1010,true);
            var pendiente = turnos.generar(1010,1);
            clientes.actualizar(1010,"Nombre con pendiente","00002222");
            rechazar(() -> clientes.cambiarEstado(1010,false), "Pendiente impide desactivar pero permite editar datos");
            turnos.cancelar(pendiente.getNumero());
            clientes.cambiarEstado(1010,false);
            verificar(!p.clientes.buscar(1010).isActivo(), "Desactivación permitida tras cancelar");
            clientes.cambiarEstado(1010,true);
            turnos.generar(1010,2); turnos.atenderSiguiente();
            clientes.cambiarEstado(1010,false);
            verificar(!clientes.buscar(1010).isActivo(), "Desactivación permitida tras atender");
            clientes.cambiarEstado(1010,true);
            p.sql("CREATE TRIGGER falla_estado_dia4 BEFORE UPDATE ON clientes BEGIN SELECT RAISE(ABORT,'fallo cambio estado'); END");
            rechazar(() -> clientes.cambiarEstado(1010,false), "Fallo real de guardado al cambiar estado");
            verificar(clientes.buscar(1010).isActivo() && p.clientes.buscar(1010).isActivo(),
                    "Estado anterior íntegro en BST y SQLite ante fallo");
            p.sql("DROP TRIGGER falla_estado_dia4");
            rechazar(() -> clientes.actualizar(9999,"No existe","12345678"), "Actualizar inexistente");
            rechazar(() -> clientes.cambiarEstado(9999,false), "Cambiar estado de inexistente");
            rechazar(() -> clientes.buscar(-1), "Buscar código no positivo");
            var lista = clientes.listar(); lista.clear();
            verificar(clientes.listar().size() == 8 && p.clientes.listar().size() == 8,
                    "La lista entregada al llamador no es la estructura ni elimina registros");
            System.out.println("CLIENTES DIA 4 CORRECTOS");
        }
    }
}
