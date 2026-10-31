package edu.oficina.modelo;

/** Modelo inicial. La unicidad del código se validará en la capa de negocio. */
public final class Cliente {
    private final int codigo;
    private final String nombreCompleto;
    private final String telefono;
    private final boolean activo;

    public Cliente(int codigo, String nombreCompleto, String telefono, boolean activo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("El código del cliente debe ser positivo.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (telefono == null || !telefono.matches("[0-9]{8}")) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 8 dígitos.");
        }
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto.trim();
        this.telefono = telefono;
        this.activo = activo;
    }

    public int getCodigo() { return codigo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
}
