package edu.oficina.pruebas;

import edu.oficina.servicios.ServicioService;
import static edu.oficina.pruebas.PreparacionPruebas.*;

public final class PruebaServiciosDia4 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            ServicioService servicio=new ServicioService(p.servicios,p.turnos);
            String literal="Información'); DROP TABLE clientes; --";
            servicio.registrar(4,literal);
            servicio.registrar(Integer.MAX_VALUE,"Código máximo");
            verificar(servicio.buscar(4).getNombre().equals(literal) && p.clientes.listar().size()==5,
                    "El nombre con texto SQL es un dato, no una instrucción");
            for (int codigo : new int[]{0,-1,Integer.MIN_VALUE}) {
                rechazar(() -> servicio.registrar(codigo,"Inválido"),"Código de servicio no positivo");
            }
            rechazar(() -> servicio.registrar(5,null),"Nombre null");
            rechazar(() -> servicio.registrar(5,"   "),"Nombre en blanco");
            servicio.cambiarEstado(4,false);
            rechazar(() -> servicio.registrar(4,"Duplicado inactivo"),"Código inactivo sigue siendo único");
            servicio.actualizar(4,"Servicio actualizado");
            verificar(!servicio.buscar(4).isActivo(),"Actualizar nombre no reactiva el servicio");
            servicio.cambiarEstado(4,true);
            var turno=p.turnos.crearPendiente(1001,4);
            rechazar(() -> servicio.cambiarEstado(4,false),"No se desactiva con un pendiente real");
            p.turnos.finalizar(turno.getNumero(),edu.oficina.modelo.Turno.Estado.CANCELADO,java.time.LocalDateTime.now());
            servicio.cambiarEstado(4,false);
            verificar(!p.servicios.buscar(4).isActivo(),"Desactivación permitida después de cancelar");
            p.sql("CREATE TRIGGER falla_servicio_dia4 BEFORE UPDATE ON servicios BEGIN SELECT RAISE(ABORT,'fallo servicio'); END");
            rechazar(() -> servicio.cambiarEstado(4,true),"Fallo de guardado del estado");
            rechazar(() -> servicio.actualizar(4,"No guardado"),"Fallo de guardado del nombre");
            verificar(!servicio.buscar(4).isActivo() && servicio.buscar(4).getNombre().equals("Servicio actualizado"),
                    "SQLite conserva nombre y estado anteriores al fallo");
            p.sql("DROP TRIGGER falla_servicio_dia4");
            rechazar(() -> servicio.actualizar(9999,"No existe"),"Actualizar inexistente");
            rechazar(() -> servicio.cambiarEstado(9999,true),"Activar inexistente");
            var lista=servicio.listar(); lista.clear();
            verificar(servicio.listar().size()==5,"Modificar la lista de resultados no elimina servicios");
            verificar(p.turnos.resumenPorServicio().stream().anyMatch(f -> f.codigoServicio()==Integer.MAX_VALUE
                    && f.total()==0),"Consulta conserva servicio sin turnos en cero");
            System.out.println("SERVICIOS DIA 4 CORRECTOS");
        }
    }
}
