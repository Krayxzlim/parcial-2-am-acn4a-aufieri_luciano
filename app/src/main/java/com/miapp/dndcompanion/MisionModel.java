package com.miapp.dndcompanion;

/**
 * Modelo de Misión.
 * Estados: "disponible", "activa", "completada", "cancelada".
 */
public class MisionModel {

    public String id; // document id en Firestore
    public String nombre;
    public String descripcion;
    public String recompensa; // texto original, ej: "100 XP"
    public long xpRecompensa; // valor numérico extraído, ej: 100
    public String estado;     // "disponible" | "activa" | "completada" | "cancelada"

    public MisionModel() {
        // Constructor vacío requerido por Firestore
    }

    public MisionModel(String nombre, String descripcion, String recompensa,
                       long xpRecompensa, String estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.recompensa = recompensa;
        this.xpRecompensa = xpRecompensa;
        this.estado = estado;
    }

    /** Extrae el valor numérico de XP de un texto tipo "100 XP" → 100L */
    public static long extraerXp(String recompensaTexto) {
        if (recompensaTexto == null) return 0L;
        String soloNumeros = recompensaTexto.replaceAll("[^0-9]", "");
        if (soloNumeros.isEmpty()) return 0L;
        try {
            return Long.parseLong(soloNumeros);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}