package edu.oficina.modelo;

/** Modelo inicial; todavía no implementa CRUD ni validación de códigos duplicados. */
public final class Servicio {
    private final int codigo;
    private final String nombre;
    private final boolean activo;

    public Servicio(int codigo, String nombre, boolean activo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("El código del servicio debe ser positivo.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        this.codigo = codigo;
        this.nombre = nombre.trim();
        this.activo = activo;
    }

    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
}
