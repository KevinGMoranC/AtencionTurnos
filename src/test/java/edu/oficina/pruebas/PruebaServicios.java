package edu.oficina.pruebas;
import edu.oficina.modelo.*;
import edu.oficina.servicios.ServicioService;
import static edu.oficina.pruebas.PreparacionPruebas.*;
public final class PruebaServicios {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            ServicioService s=new ServicioService(p.servicios,p.turnos);
            s.registrar(4,"Nuevo servicio");
            verificar(s.listar().size()==4,"Registro y listado de servicios");
            rechazar(() -> s.registrar(4,"Duplicado"),"Código duplicado");
            rechazar(() -> s.registrar(-1,"Inválido"),"Código negativo");
            rechazar(() -> s.registrar(5,"  "),"Nombre vacío");
            rechazar(() -> s.buscar(99),"Servicio inexistente");
            s.actualizar(4,"Servicio actualizado"); s.cambiarEstado(4,false);
            verificar(!s.buscar(4).isActivo() && s.buscar(4).getNombre().equals("Servicio actualizado"),"Actualización y desactivación");
            s.cambiarEstado(4,true);
            p.turnos.crearPendiente(1001,4);
            rechazar(() -> s.cambiarEstado(4,false),"Servicio con pendiente");
            verificar(s.buscar(4).isActivo(),"Se conserva activo ante rechazo");
            p.sql("CREATE TRIGGER falla_servicio BEFORE UPDATE ON servicios BEGIN SELECT RAISE(ABORT,'fallo de prueba'); END");
            rechazar(() -> s.actualizar(4,"No guardado"),"Fallo de actualización");
            verificar(s.buscar(4).getNombre().equals("Servicio actualizado"),"Nombre persistente intacto");
            System.out.println("PRUEBA SERVICIOS CORRECTA");
        }
    }
}
