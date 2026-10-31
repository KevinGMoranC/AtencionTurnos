package edu.oficina.pruebas;

import edu.oficina.modelo.Cliente;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.TurnoService;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Verifica el invariante completo BST = filas persistidas después de cada operación. */
public final class ValidacionFinalClientes {
    private record Fila(int codigo,String nombre,String telefono,boolean activo) { }
    private static List<Fila> filas(List<Cliente> clientes) {
        return clientes.stream().map(c -> new Fila(c.getCodigo(),c.getNombreCompleto(),
                c.getTelefono(),c.isActivo())).toList();
    }
    private static void coherente(ClienteService clientes,PreparacionPruebas p) throws Exception {
        verificar(filas(clientes.listar()).equals(filas(p.clientes.listar())),
                "Todos los campos y el orden del BST coinciden con SQLite");
    }
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            ClienteService clientes=new ClienteService(p.clientes,p.turnos);
            coherente(clientes,p);
            for (int codigo=2000;codigo<2006;codigo++) {
                clientes.registrar(codigo,"Cliente "+codigo,"00123456"); coherente(clientes,p);
                clientes.actualizar(codigo,"Editado "+codigo,"00001111"); coherente(clientes,p);
                if (codigo%2==1) { clientes.cambiarEstado(codigo,false); coherente(clientes,p); }
            }
            TurnoService turnos=new TurnoService(p.turnos);
            var t=turnos.generar(2000,1);
            rechazar(() -> clientes.cambiarEstado(2000,false),"Pendiente no altera el invariante al rechazar desactivación");
            coherente(clientes,p);
            turnos.cancelar(t.getNumero()); clientes.cambiarEstado(2000,false); coherente(clientes,p);
            p.sql("CREATE TRIGGER falla_final_clientes BEFORE UPDATE ON clientes BEGIN SELECT RAISE(ABORT,'fallo final'); END");
            rechazar(() -> clientes.actualizar(2000,"No guardado","87654321"),"Escritura fallida");
            coherente(clientes,p); p.sql("DROP TRIGGER falla_final_clientes");
            clientes.recargarDesdePersistencia(); coherente(clientes,p);
            coherente(new ClienteService(p.clientes,p.turnos),p);
            verificar(!clientes.buscar(2000).isActivo() && p.turnos.historialCliente(2000).size()==1,
                    "Cliente inactivo permanece con su historial");
            System.out.println("VALIDACION FINAL CLIENTES CORRECTA");
        }
    }
}
