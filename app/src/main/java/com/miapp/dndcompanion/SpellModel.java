package com.miapp.dndcompanion;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de hechizo que mapea  los campos de la Open5e API v1.
 *
 * Campos de la API usados:
 *   slug, name, desc, higher_level, range, components, material,
 *   casting_time, level, level_int, school, duration,
 *   concentration (string "yes"/"no"), ritual (string "yes"/"no"),
 *   dnd_class, requires_concentration, can_be_cast_as_ritual
 *
 * Los 6 hechizos con imagen (desde Supabase) se marcan con imagenUrl.
 * El resto trae imagenUrl = null y DetalleHechizo muestra placeholder.
 */
public class SpellModel {

    // Campos de la API
    public String slug;
    public String name;
    public String desc;
    public String higherLevel;
    public String range;
    public String components;
    public String material;
    public String castingTime;
    public String level;
    public int    levelInt;
    public String school;
    public String duration;
    public boolean requiresConcentration;
    public boolean canBeCastAsRitual;
    public String dndClass;

    // Campo extra de la app (imagen personalizada)
    public String imagenUrl;

    // Badge visual (calculado a partir de level_int)
    /** Devuelve la etiqueta del badge: "TRUCO", "NIVEL 1" … "NIVEL 9" */
    public String getBadgeLabel() {
        if (levelInt == 0) return "TRUCO";
        return "NIVEL " + levelInt;
    }

    // Constructor completo
    public SpellModel(String slug, String name, String desc, String higherLevel,
                      String range, String components, String material,
                      String castingTime, String level, int levelInt,
                      String school, String duration,
                      boolean requiresConcentration, boolean canBeCastAsRitual,
                      String dndClass, String imagenUrl) {
        this.slug                  = slug;
        this.name                  = name;
        this.desc                  = desc;
        this.higherLevel           = higherLevel;
        this.range                 = range;
        this.components            = components;
        this.material              = material;
        this.castingTime           = castingTime;
        this.level                 = level;
        this.levelInt              = levelInt;
        this.school                = school;
        this.duration              = duration;
        this.requiresConcentration = requiresConcentration;
        this.canBeCastAsRitual     = canBeCastAsRitual;
        this.dndClass              = dndClass;
        this.imagenUrl             = imagenUrl;
    }

    // Hechizos hardcoded con imagen personalizada (Supabase)
    // Estos 6 se muestran siempre en la pantalla principal.
    // Los campos coinciden exactamente con lo que devuelve la API para esos slugs.
    public static List<SpellModel> getHechizosFijos() {
        List<SpellModel> lista = new ArrayList<>();

        lista.add(new SpellModel(
                "frostbite",
                "Descarga de Escarcha",
                "Lanzas un rayo de frío contra una criatura. Realiza un ataque de hechizo a distancia. " +
                        "Si impacta, inflige 1d8 de daño de frío y la velocidad del objetivo se reduce en " +
                        "10 pies hasta el inicio de tu siguiente turno.\n\n" +
                        "El daño aumenta en 1d8 cuando alcanzas nivel 5 (2d8), nivel 11 (3d8) y nivel 17 (4d8).",
                "",
                "60 pies", "V, S", "", "Acción", "Cantrip", 0,
                "Evocation", "Instantáneo", false, false,
                "Druid, Sorcerer, Warlock, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/frostbolt.png"
        ));

        lista.add(new SpellModel(
                "mage-hand",
                "Mano Mágica",
                "Una mano espectral flotante aparece en un punto elegido. Puede recoger o manipular " +
                        "objetos, abrir puertas, depositar objetos y usar herramientas sencillas.\n\n" +
                        "No puede atacar, activar objetos mágicos ni cargar más de 10 libras.",
                "",
                "30 pies", "V, S", "", "Acción", "Cantrip", 0,
                "Conjuration", "1 minuto", false, false,
                "Bard, Sorcerer, Warlock, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/magehand.png"
        ));

        lista.add(new SpellModel(
                "shield",
                "Escudo",
                "Una barrera invisible de fuerza mágica aparece para protegerte. Se activa cuando " +
                        "eres atacado o cuando una criatura te lanza Proyectil Mágico.\n\n" +
                        "+5 a la CA hasta el inicio de tu siguiente turno. " +
                        "Eres inmune a Proyectil Mágico este turno.",
                "",
                "Personal", "V, S", "", "Reacción", "1st-level", 1,
                "Abjuration", "1 ronda", false, false,
                "Sorcerer, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/shield.png"
        ));

        lista.add(new SpellModel(
                "magic-missile",
                "Misiles Mágicos",
                "Creas tres dardos brillantes de fuerza mágica que impactan automáticamente. " +
                        "Cada dardo inflige 1d4+1 de daño de fuerza.\n\n" +
                        "En niveles superiores: el hechizo crea un dardo adicional por cada nivel " +
                        "por encima del 1.",
                "Cuando lanzas este hechizo usando un espacio de hechizo de 2do nivel o superior, " +
                        "el hechizo crea un dardo adicional por cada nivel por encima del 1.",
                "120 pies", "V, S", "", "Acción", "1st-level", 1,
                "Evocation", "Instantáneo", false, false,
                "Sorcerer, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/magicmissil.png"
        ));

        lista.add(new SpellModel(
                "mage-armor",
                "Armadura de Magia",
                "Tocas a una criatura dispuesta y la envuelves en protección mágica. " +
                        "Su CA se convierte en 13 + modificador de Destreza.\n\n" +
                        "El hechizo finaliza si el objetivo equipa armadura.",
                "",
                "Toque", "V, S, M", "Trozo de cuero curtido", "Acción", "1st-level", 1,
                "Abjuration", "8 horas", false, false,
                "Sorcerer, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/magearmor.png"
        ));

        lista.add(new SpellModel(
                "fireball",
                "Bola de Fuego",
                "Un destello brillante explota en un punto elegido. Cada criatura en el área " +
                        "realiza salvación de Destreza CD 14.\n\n" +
                        "Falla: 8d6 daño de fuego.\nÉxito: mitad del daño.\n\n" +
                        "En niveles superiores: +1d6 por cada nivel por encima del 3.",
                "Cuando lanzas este hechizo usando un espacio de hechizo de 4to nivel o superior, " +
                        "el daño aumenta en 1d6 por cada nivel de espacio por encima del 3.",
                "150 pies", "V, S, M", "Una pequeña bola de guano de murciélago y azufre",
                "Acción", "3rd-level", 3,
                "Evocation", "Instantáneo", false, false,
                "Sorcerer, Wizard",
                "https://fecpedqshkgkbxxukbeg.supabase.co/storage/v1/object/public/Spells/fireball.png"
        ));

        return lista;
    }
}