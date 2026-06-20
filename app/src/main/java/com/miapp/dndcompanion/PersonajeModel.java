package com.miapp.dndcompanion;

/**
 * Modelo de Personaje.
 * Incluye la tabla oficial de XP de D&D 5e (PHB pág. 15) para el sistema de niveles.
 */
public class PersonajeModel {

    // Identificación en Firestore
    public String id; // document id en la subcolección "personajes"

    //  Datos básicos
    public String nombre;
    public String raza;
    public String clase;
    public String alineacion;
    public int nivel;
    public long xp; // experiencia acumulada total

    // Atributos base
    public int fue, des, con, intel, sab, car;

    public boolean activo; // si es el personaje mostrado en Inicio

    public PersonajeModel() {
        // Constructor vacío para Firestore
    }

    public PersonajeModel(String nombre, String raza, String clase, String alineacion,
                          int nivel, long xp, int fue, int des, int con,
                          int intel, int sab, int car, boolean activo) {
        this.nombre = nombre;
        this.raza = raza;
        this.clase = clase;
        this.alineacion = alineacion;
        this.nivel = nivel;
        this.xp = xp;
        this.fue = fue;
        this.des = des;
        this.con = con;
        this.intel = intel;
        this.sab = sab;
        this.car = car;
        this.activo = activo;
    }

    // Tabla oficial de XP necesaria por nivel (D&D 5e, PHB)
    // Índice 0 = nivel 1, índice 19 = nivel 20
    public static final long[] XP_THRESHOLDS = {
            0,      // Nivel 1
            300,    // Nivel 2
            900,    // Nivel 3
            2700,   // Nivel 4
            6500,   // Nivel 5
            14000,  // Nivel 6
            23000,  // Nivel 7
            34000,  // Nivel 8
            48000,  // Nivel 9
            64000,  // Nivel 10
            85000,  // Nivel 11
            100000, // Nivel 12
            120000, // Nivel 13
            140000, // Nivel 14
            165000, // Nivel 15
            195000, // Nivel 16
            225000, // Nivel 17
            265000, // Nivel 18
            305000, // Nivel 19
            355000  // Nivel 20
    };

    /** Devuelve el nivel correspondiente a una cantidad de XP, según la tabla oficial. */
    public static int calcularNivelPorXp(long xpTotal) {
        int nivel = 1;
        for (int i = 0; i < XP_THRESHOLDS.length; i++) {
            if (xpTotal >= XP_THRESHOLDS[i]) {
                nivel = i + 1;
            } else {
                break;
            }
        }
        return Math.min(nivel, 20);
    }

    /** XP necesaria para alcanzar el siguiente nivel. -1 si ya es nivel 20 (máximo). */
    public static long xpParaSiguienteNivel(int nivelActual) {
        if (nivelActual >= 20) return -1;
        return XP_THRESHOLDS[nivelActual]; // índice = nivelActual porque el array es 0-based
    }

    /** XP requerida para el nivel actual (umbral inferior). */
    public static long xpUmbralNivelActual(int nivelActual) {
        int idx = Math.max(0, Math.min(nivelActual - 1, XP_THRESHOLDS.length - 1));
        return XP_THRESHOLDS[idx];
    }

    /** Progreso (0.0 a 1.0) dentro del nivel actual, para barras de progreso. */
    public static float progresoNivelActual(long xpTotal, int nivelActual) {
        if (nivelActual >= 20) return 1.0f;
        long base = xpUmbralNivelActual(nivelActual);
        long siguiente = xpParaSiguienteNivel(nivelActual);
        if (siguiente <= base) return 1.0f;
        float progreso = (float) (xpTotal - base) / (float) (siguiente - base);
        return Math.max(0f, Math.min(1f, progreso));
    }


    /** Modificador de característica: (valor - 10) / 2, redondeado hacia abajo. */
    public static int modificador(int valorAtributo) {
        return (int) Math.floor((valorAtributo - 10) / 2.0);
    }

    /** Iniciativa = modificador de Destreza. */
    public int getIniciativa() {
        return modificador(des);
    }

    /** CA estimada simple = 10 + mod DES (sin armadura equipada). */
    public int getCaEstimada() {
        return 10 + modificador(des);
    }

    /** PG estimados simples = nivel * (6 + mod CON), mínimo 1 por nivel. */
    public int getPgEstimados() {
        int porNivel = 6 + modificador(con);
        if (porNivel < 1) porNivel = 1;
        return nivel * porNivel;
    }

    /** Texto formateado de un modificador, ej: "+2" o "-1". */
    public static String formatMod(int mod) {
        return mod >= 0 ? "+" + mod : String.valueOf(mod);
    }
}