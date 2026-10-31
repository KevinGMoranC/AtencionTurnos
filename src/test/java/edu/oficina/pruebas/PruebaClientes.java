package edu.oficina.pruebas;
import edu.oficina.modelo.Cliente;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.servicios.ClienteService;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Prueba el módulo de clientes contra los DAO del integrante 3, sin TurnoService. */
public final class PruebaClientes {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            ClienteService s = new ClienteService(p.clientes, p.turnos);
            verificar(s.buscar(1003).getNombreCompleto().equals("María García"), "Carga del BST desde SQLite");
            s.registrar(1010,"Nuevo cliente","00123456");
            verificar(s.buscar(1010).getTelefono().equals("00123456"), "Teléfono conserva ceros iniciales");
            rechazar(() -> s.registrar(1010,"Duplicado","12345678"), "Código duplicado");
            rechazar(() -> s.registrar(0,"Nombre","12345678"), "Código no positivo");
            rechazar(() -> s.registrar(1011,"  ","12345678"), "Nombre vacío");
            rechazar(() -> s.registrar(1011,"Nombre","1234abcd"), "Teléfono inválido");
            rechazar(() -> s.buscar(9999), "Código inexistente");
            s.actualizar(1001,"Ana actualizada","12345678");
            verificar(p.clientes.buscar(1001).getNombreCompleto().equals("Ana actualizada"), "Actualización persistente");
            s.cambiarEstado(1001,false);
            verificar(!s.buscar(1001).isActivo() && s.listar().size() == 6, "Inactivo permanece en el BST");
            s.cambiarEstado(1001,true);
            p.turnos.crearPendiente(1001,1);
            rechazar(() -> s.cambiarEstado(1001,false), "Desactivar cliente con pendiente");
            verificar(s.buscar(1001).isActivo(), "BST conserva estado ante rechazo");
            p.sql("CREATE TRIGGER falla_cliente BEFORE UPDATE ON clientes BEGIN SELECT RAISE(ABORT,'fallo de prueba'); END");
            rechazar(() -> s.actualizar(1001,"No guardado","87654321"), "Fallo real de escritura");
            verificar(s.buscar(1001).getNombreCompleto().equals("Ana actualizada"), "BST intacto ante fallo de SQLite");
            p.sql("DROP TRIGGER falla_cliente");
            p.sql("CREATE TRIGGER falla_alta BEFORE INSERT ON clientes BEGIN SELECT RAISE(ABORT,'fallo de prueba'); END");
            rechazar(() -> s.registrar(2000,"No guardado","12345678"), "Fallo al registrar");
            rechazar(() -> s.buscar(2000), "No aparece cliente no guardado");
            p.sql("DROP TRIGGER falla_alta");
            new InicializadorBD(p.conexiones).inicializar();
            ClienteService reinicio = new ClienteService(p.clientes,p.turnos);
            verificar(reinicio.listar().size() == 6 && reinicio.buscar(1001).getNombreCompleto().equals("Ana actualizada"), "Reinicio no repone ni duplica datos iniciales");
            System.out.println("PRUEBA CLIENTES CORRECTA");
        }
    }
}
