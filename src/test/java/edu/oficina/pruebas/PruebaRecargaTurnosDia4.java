package edu.oficina.pruebas;

import edu.oficina.dao.TurnoRepositorio;
import edu.oficina.modelo.Turno;
import edu.oficina.servicios.TurnoService;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

public final class PruebaRecargaTurnosDia4 {
    private static final class Controlado implements TurnoRepositorio {
        private final TurnoRepositorio real;
        private boolean falla;
        private List<Turno> alternativa;
        private Controlado(TurnoRepositorio real) { this.real=real; }
        public List<Turno> listarPendientes() throws SQLException {
            if (falla) throw new SQLException("Fallo de lectura de pendientes");
            return alternativa==null ? real.listarPendientes() : alternativa;
        }
        public boolean tienePendienteCliente(int c) throws SQLException { return real.tienePendienteCliente(c); }
        public boolean tienePendienteServicio(int s) throws SQLException { return real.tienePendienteServicio(s); }
        public Turno crearPendiente(int c,int s) throws SQLException { return real.crearPendiente(c,s); }
        public Turno finalizar(long n,Turno.Estado e,LocalDateTime f) throws SQLException { return real.finalizar(n,e,f); }
    }
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            Controlado repo=new Controlado(p.turnos);
            TurnoService servicio=new TurnoService(repo);
            servicio.generar(1001,1);
            p.turnos.crearPendiente(1002,2);
            p.turnos.finalizar(1,Turno.Estado.ATENDIDO,LocalDateTime.now());
            servicio.recargarDesdePersistencia();
            verificar(servicio.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(2L)),
                    "Recarga desde base omite finalizados e incorpora nuevos pendientes");
            verificar(servicio.atenderSiguiente().getNumero()==2, "FIFO reconstruido desde SQLite");
            servicio.generar(1003,3);
            repo.falla=true;
            try { servicio.recargarDesdePersistencia(); throw new AssertionError("Faltó SQLException"); }
            catch (SQLException esperado) { }
            verificar(servicio.consultarSiguiente().getNumero()==3, "Error de consulta conserva la cola anterior");
            repo.falla=false;
            repo.alternativa=List.of(new Turno(100,1004,1,LocalDateTime.now(),Turno.Estado.PENDIENTE,null),
                    new Turno(101,1004,2,LocalDateTime.now(),Turno.Estado.PENDIENTE,null));
            rechazar(servicio::recargarDesdePersistencia, "Duplicado detectado durante reconstrucción");
            verificar(servicio.consultarSiguiente().getNumero()==3 && servicio.listarPendientes().size()==1,
                    "No se publica una cola construida a medias");
            repo.alternativa=List.of(new Turno(100,1004,1,LocalDateTime.now(),Turno.Estado.CANCELADO,LocalDateTime.now()));
            rechazar(servicio::recargarDesdePersistencia, "La recarga rechaza un turno finalizado");
            verificar(servicio.consultarSiguiente().getNumero()==3, "Se conserva pendiente ante dato inválido");
            repo.alternativa=null;
            servicio.recargarDesdePersistencia(); servicio.recargarDesdePersistencia();
            verificar(servicio.listarPendientes().size()==1, "Recargas repetidas no duplican nodos");
            servicio.cancelar(3); servicio.recargarDesdePersistencia();
            verificar(servicio.consultarSiguiente()==null && p.escalar("SELECT count(*) FROM turnos")==3,
                    "Recarga vacía no borra historial persistido");
            System.out.println("RECARGA TURNOS DIA 4 CORRECTA");
        }
    }
}
