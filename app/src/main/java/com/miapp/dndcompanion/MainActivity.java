package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutDisponibles, layoutActivas, layoutSpells;
    TextView txtResultado, txtTipoDado;
    int contador = 0;
    Random random = new Random();
    ArrayList<String> historial = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutDisponibles = findViewById(R.id.layoutDisponibles);
        layoutActivas     = findViewById(R.id.layoutActivas);
        layoutSpells      = findViewById(R.id.layoutSpells);
        txtResultado      = findViewById(R.id.txtResultado);
        txtTipoDado       = findViewById(R.id.txtTipoDado);

        // Misiones
        agregarMisionDisponible("Entrega especial",
                "Entrega un paquete sellado al Gremio de Comerciantes.", "100 XP");
        agregarMisionDisponible("Caza de bandidos",
                "Elimina a los bandidos del camino del norte.", "200 XP");

        // Hechizos
        agregarSpell("Descarga de Escarcha", "TRUCO",
                "Conjuración", "Acción", "60 pies", "1 criatura", "Instantáneo", false, false,
                "Lanzas un rayo de frío contra una criatura. Realiza un ataque de hechizo a distancia. Si impacta, inflige 1d8 de daño de frío y la velocidad del objetivo se reduce en 10 pies hasta el inicio de tu siguiente turno.\n\nEl daño aumenta en 1d8 cuando alcanzas nivel 5 (2d8), nivel 11 (3d8) y nivel 17 (4d8).");

        agregarSpell("Mano Mágica", "TRUCO",
                "Conjuración", "Acción", "30 pies", "Un objeto", "1 minuto", false, false,
                "Una mano espectral flotante aparece en un punto elegido. Puede recoger o manipular objetos, abrir puertas, depositar objetos y usar herramientas sencillas.\n\nNo puede atacar, activar objetos mágicos ni cargar más de 10 libras.");

        agregarSpell("Escudo", "NIVEL 1",
                "Abjuración", "Reacción", "Personal", "Tú mismo", "1 ronda", false, false,
                "Una barrera invisible de fuerza mágica aparece para protegerte. Se activa cuando eres atacado o cuando una criatura te lanza Proyectil Mágico.\n\n+5 a la CA hasta el inicio de tu siguiente turno. Eres inmune a Proyectil Mágico este turno.");

        agregarSpell("Misiles Mágicos", "NIVEL 1",
                "Evocación", "Acción", "120 pies", "Una o más criaturas", "Instantáneo", false, false,
                "Creas tres dardos brillantes de fuerza mágica que impactan automáticamente. Cada dardo inflige 1d4+1 de daño de fuerza.\n\n*En niveles superiores:* el hechizo crea un dardo adicional por cada nivel por encima del 1.");

        agregarSpell("Armadura de Magia", "NIVEL 2",
                "Abjuración", "Acción", "Toque", "Una criatura dispuesta", "8 horas", false, false,
                "Tocas a una criatura dispuesta y la envuelves en protección mágica. Su CA se convierte en 13 + modificador de Destreza.\n\nEl hechizo finaliza si el objetivo equipa armadura.");

        agregarSpell("Bola de Fuego", "NIVEL 3",
                "Evocación", "Acción", "150 pies", "Esfera 20 pies", "Instantáneo", true, false,
                "Un destello brillante explota en un punto elegido. Cada criatura en el área realiza salvación de Destreza CD 14.\n\nFalla: 8d6 daño de fuego.\nÉxito: mitad del daño.\n\n*En niveles superiores:* +1d6 por cada nivel por encima del 3.");

        // Dados
        findViewById(R.id.btnD4).setOnClickListener(v  -> tirar(4));
        findViewById(R.id.btnD6).setOnClickListener(v  -> tirar(6));
        findViewById(R.id.btnD8).setOnClickListener(v  -> tirar(8));
        findViewById(R.id.btnD10).setOnClickListener(v -> tirar(10));
        findViewById(R.id.btnD12).setOnClickListener(v -> tirar(12));
        findViewById(R.id.btnD20).setOnClickListener(v -> tirar(20));
        findViewById(R.id.btnAleatorio).setOnClickListener(v -> {
            int[] dados = {4, 6, 8, 10, 12, 20};
            tirar(dados[random.nextInt(dados.length)]);
        });
        findViewById(R.id.btnHistorial).setOnClickListener(v -> mostrarHistorial());

        //Agregar misión
        findViewById(R.id.btnAgregar).setOnClickListener(v -> {
            contador++;
            agregarMisionDisponible("Nueva misión " + contador, "Una misión misteriosa te aguarda.", "50 XP");
        });

        //Menú inferior
        configurarMenu();
    }

    private void configurarMenu() {
        findViewById(R.id.menuInicio).setOnClickListener(v -> { /* ya estamos aquí */ });

        findViewById(R.id.menuInventario).setOnClickListener(v -> {
            startActivity(new Intent(this, InventarioActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.menuNotas).setOnClickListener(v -> {
            startActivity(new Intent(this, NotasActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.menuAjustes).setOnClickListener(v ->
                mostrarDialogoEstetico("Ajustes",
                        "⚙️  Los ajustes estarán\ndisponibles en la próxima versión.")
        );
    }

    private void tirar(int caras) {
        int resultado = random.nextInt(caras) + 1;
        txtResultado.setText(String.valueOf(resultado));
        txtTipoDado.setText("(d" + caras + ")");
        historial.add(0, "d" + caras + "|" + resultado);
        if (historial.size() > 20) historial.remove(historial.size() - 1);
    }

    private void mostrarHistorial() {

        LinearLayout raiz = new LinearLayout(this);
        raiz.setOrientation(LinearLayout.VERTICAL);
        raiz.setBackgroundResource(R.drawable.seccion_bg);

        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.VERTICAL);
        encabezado.setGravity(Gravity.CENTER);
        encabezado.setPadding(dp(16), dp(16), dp(16), dp(12));
        encabezado.setBackgroundColor(color(R.color.seccion_fondo));

        LinearLayout filaTitulo = new LinearLayout(this);
        filaTitulo.setOrientation(LinearLayout.HORIZONTAL);
        filaTitulo.setGravity(Gravity.CENTER_VERTICAL);

        TextView estrella1 = txt("✦", 14, R.color.dorado, false);
        estrella1.setPadding(0, 0, dp(8), 0);

        TextView titulo = txt("HISTORIAL DE TIRADAS", 14, R.color.dorado, true);
        titulo.setFontVariationSettings(null);
        titulo.setTypeface(Typeface.create("serif", Typeface.BOLD));

        TextView estrella2 = txt("✦", 14, R.color.dorado, false);
        estrella2.setPadding(dp(8), 0, 0, 0);

        filaTitulo.addView(estrella1);
        filaTitulo.addView(titulo);
        filaTitulo.addView(estrella2);
        encabezado.addView(filaTitulo);

        TextView subtitulo = txt(
                historial.isEmpty() ? "Sin tiradas registradas" :
                        "Últimas " + Math.min(historial.size(), 15) + " tiradas",
                10, R.color.texto_secundario, false);
        subtitulo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, dp(4), 0, 0);
        subtitulo.setLayoutParams(subLp);
        encabezado.addView(subtitulo);

        raiz.addView(encabezado);

        raiz.addView(separadorDorado());

        ScrollView scroll = new ScrollView(this);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(300));
        scroll.setLayoutParams(scrollLp);
        scroll.setBackgroundColor(color(R.color.fondo));

        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        lista.setPadding(0, dp(4), 0, dp(4));

        if (historial.isEmpty()) {
            LinearLayout vacio = new LinearLayout(this);
            vacio.setOrientation(LinearLayout.VERTICAL);
            vacio.setGravity(Gravity.CENTER);
            vacio.setPadding(0, dp(40), 0, dp(40));

            TextView ico = txt("🎲", 36, R.color.borde, false);
            ico.setGravity(Gravity.CENTER);
            ico.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView msg = txt("Aún no tiraste ningún dado", 13, R.color.texto_secundario, false);
            msg.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams msgLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            msgLp.setMargins(0, dp(8), 0, 0);
            msg.setLayoutParams(msgLp);

            vacio.addView(ico);
            vacio.addView(msg);
            lista.addView(vacio);
        } else {
            int mostrar = Math.min(historial.size(), 15);
            for (int i = 0; i < mostrar; i++) {
                String[] p = historial.get(i).split("\\|");
                String tipoDado = p.length > 0 ? p[0] : "?";
                String valStr   = p.length > 1 ? p[1] : "?";
                int val = 0;
                try { val = Integer.parseInt(valStr); } catch (Exception ignored) {}

                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setGravity(Gravity.CENTER_VERTICAL);
                fila.setPadding(dp(16), dp(10), dp(16), dp(10));
                fila.setBackgroundColor(i % 2 == 0
                        ? color(R.color.seccion_fondo)
                        : color(R.color.fondo));
                TextView numView = txt("#" + (i + 1), 10, R.color.borde, false);
                numView.setGravity(Gravity.CENTER);
                numView.setTypeface(Typeface.MONOSPACE);
                LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(dp(28),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                numLp.setMargins(0, 0, dp(10), 0);
                numView.setLayoutParams(numLp);

                TextView badgeDado = new TextView(this);
                badgeDado.setText(tipoDado);
                badgeDado.setTextColor(color(R.color.dorado));
                badgeDado.setTextSize(11);
                badgeDado.setTypeface(Typeface.create("serif", Typeface.BOLD));
                badgeDado.setBackgroundColor(color(R.color.boton_bg));
                badgeDado.setPadding(dp(8), dp(3), dp(8), dp(3));
                LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                badgeLp.setMargins(0, 0, dp(12), 0);
                badgeDado.setLayoutParams(badgeLp);

                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                int colorRes;
                String sufijo = "";
                if (val == 1) {
                    colorRes = 0;
                    sufijo = "  💀";
                } else if (tipoDado.equals("d20") && val == 20) {
                    colorRes = R.color.dorado;
                    sufijo = "  ✦";
                } else {
                    colorRes = R.color.texto;
                }
                TextView resView = new TextView(this);
                resView.setText(valStr + sufijo);
                resView.setTextSize(20);
                resView.setTypeface(Typeface.DEFAULT_BOLD);
                resView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                if (colorRes == 0) resView.setTextColor(Color.parseColor("#C05050"));
                else resView.setTextColor(color(colorRes));

                fila.addView(numView);
                fila.addView(badgeDado);
                fila.addView(spacer);
                fila.addView(resView);
                lista.addView(fila);
            }
        }

        scroll.addView(lista);
        raiz.addView(scroll);

        raiz.addView(separadorDorado());

        Button cerrar = new Button(this);
        cerrar.setText("✦  CERRAR  ✦");
        cerrar.setTextColor(color(R.color.dorado));
        cerrar.setTextSize(12);
        cerrar.setTypeface(Typeface.create("serif", Typeface.BOLD));
        cerrar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                color(R.color.boton_bg)));
        LinearLayout.LayoutParams cerrarLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        cerrarLp.setMargins(dp(16), dp(10), dp(16), dp(16));
        cerrar.setLayoutParams(cerrarLp);
        raiz.addView(cerrar);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(raiz)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        cerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void mostrarTarjetaHechizo(String nombre, String nivel, String escuela,
                                       String tiempo, String alcance, String objetivo,
                                       String duracion, boolean concentracion, boolean ritual,
                                       String descripcion) {
        int badgeBg, badgeTexto;
        switch (nivel) {
            case "TRUCO":  badgeBg = R.color.badge_truco;  badgeTexto = R.color.badge_truco_texto;  break;
            case "NIVEL 1":badgeBg = R.color.badge_nivel1; badgeTexto = R.color.badge_nivel1_texto; break;
            case "NIVEL 2":badgeBg = R.color.badge_nivel2; badgeTexto = R.color.badge_nivel2_texto; break;
            default:       badgeBg = R.color.badge_nivel3; badgeTexto = R.color.badge_nivel3_texto; break;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.seccion_bg);

        LinearLayout cab = new LinearLayout(this);
        cab.setOrientation(LinearLayout.VERTICAL);
        cab.setPadding(dp(16), dp(16), dp(16), dp(12));
        cab.setBackgroundColor(color(R.color.seccion_fondo));

        LinearLayout filaBadge = new LinearLayout(this);
        filaBadge.setOrientation(LinearLayout.HORIZONTAL);
        filaBadge.setGravity(Gravity.CENTER_VERTICAL);

        TextView badgeView = new TextView(this);
        badgeView.setText(nivel);
        badgeView.setTextColor(color(badgeTexto));
        badgeView.setBackgroundColor(color(badgeBg));
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(10), dp(4), dp(10), dp(4));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(8), 0);
        badgeView.setLayoutParams(bLp);

        TextView escuelaView = txt(escuela.toUpperCase(), 10, R.color.texto_secundario, false);
        escuelaView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        filaBadge.addView(badgeView);
        filaBadge.addView(escuelaView);

        if (concentracion) {
            TextView c = new TextView(this);
            c.setText("◎ CONC.");
            c.setTextColor(color(R.color.dorado_claro));
            c.setTextSize(8);
            c.setBackgroundColor(color(R.color.boton_bg));
            c.setPadding(dp(6), dp(3), dp(6), dp(3));
            filaBadge.addView(c);
        }
        if (ritual) {
            TextView r = new TextView(this);
            r.setText(" ® ");
            r.setTextColor(color(R.color.dorado));
            r.setTextSize(8);
            filaBadge.addView(r);
        }
        cab.addView(filaBadge);

        TextView nomView = txt(nombre, 20, R.color.dorado, true);
        nomView.setTypeface(Typeface.create("serif", Typeface.BOLD));
        LinearLayout.LayoutParams nomLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLp.setMargins(0, dp(6), 0, 0);
        nomView.setLayoutParams(nomLp);
        cab.addView(nomView);
        card.addView(cab);

        card.addView(separadorDorado());

        LinearLayout props = new LinearLayout(this);
        props.setOrientation(LinearLayout.HORIZONTAL);
        props.setBackgroundColor(color(R.color.stat_fondo));
        props.setPadding(dp(12), dp(10), dp(12), dp(10));
        props.addView(propCol("TIEMPO",   tiempo));
        props.addView(propSep());
        props.addView(propCol("ALCANCE",  alcance));
        props.addView(propSep());
        props.addView(propCol("OBJETIVO", objetivo));
        props.addView(propSep());
        props.addView(propCol("DURACIÓN", duracion));
        card.addView(props);

        card.addView(separadorBorde());

        ScrollView descScroll = new ScrollView(this);
        descScroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(220)));

        TextView descView = new TextView(this);
        descView.setText(descripcion);
        descView.setTextColor(color(R.color.texto));
        descView.setTextSize(13);
        descView.setPadding(dp(16), dp(14), dp(16), dp(14));
        descView.setLineSpacing(dp(3), 1.0f);
        descScroll.addView(descView);
        card.addView(descScroll);

        card.addView(separadorDorado());

        Button cerrar = new Button(this);
        cerrar.setText("✦  CERRAR TARJETA  ✦");
        cerrar.setTextColor(color(R.color.dorado));
        cerrar.setTextSize(12);
        cerrar.setTypeface(Typeface.create("serif", Typeface.BOLD));
        cerrar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                color(R.color.boton_bg)));
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        cLp.setMargins(dp(16), dp(10), dp(16), dp(16));
        cerrar.setLayoutParams(cLp);
        card.addView(cerrar);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(card)
                .create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void agregarSpell(String nombre, String nivel, String escuela,
                              String tiempo, String alcance, String objetivo,
                              String duracion, boolean concentracion, boolean ritual,
                              String descripcion) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(6), 0, dp(6));
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setClickable(true);
        item.setFocusable(true);
        item.setBackground(getDrawable(android.R.drawable.list_selector_background));

        int badgeBg, badgeTexto;
        switch (nivel) {
            case "TRUCO":  badgeBg = R.color.badge_truco;  badgeTexto = R.color.badge_truco_texto;  break;
            case "NIVEL 1":badgeBg = R.color.badge_nivel1; badgeTexto = R.color.badge_nivel1_texto; break;
            case "NIVEL 2":badgeBg = R.color.badge_nivel2; badgeTexto = R.color.badge_nivel2_texto; break;
            default:       badgeBg = R.color.badge_nivel3; badgeTexto = R.color.badge_nivel3_texto; break;
        }

        TextView badgeView = new TextView(this);
        badgeView.setText(nivel);
        badgeView.setTextColor(color(badgeTexto));
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(8), dp(4), dp(8), dp(4));
        badgeView.setBackgroundColor(color(badgeBg));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(10), 0);
        badgeView.setLayoutParams(bLp);

        TextView txtNombre = txt(nombre, 13, R.color.texto, false);
        txtNombre.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView arrow = txt("›", 16, R.color.dorado_borde, false);

        item.addView(badgeView);
        item.addView(txtNombre);
        item.addView(arrow);

        item.setOnClickListener(v -> mostrarTarjetaHechizo(
                nombre, nivel, escuela, tiempo, alcance, objetivo,
                duracion, concentracion, ritual, descripcion));

        layoutSpells.addView(item);
    }
    private void agregarMisionDisponible(String nombre, String descripcion, String recompensa) {
        if (layoutDisponibles.getChildCount() > 0) agregarSeparador(layoutDisponibles);

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(10), 0, dp(10));
        item.setGravity(Gravity.CENTER_VERTICAL);

        TextView icono = new TextView(this);
        icono.setText("📜");
        icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView tn = txt(nombre, 13, R.color.texto, true);
        TextView td = txt(descripcion, 11, R.color.texto_secundario, false);
        TextView tx = txt("Recompensa:  " + recompensa, 11, R.color.dorado_claro, false);
        info.addView(tn); info.addView(td); info.addView(tx);

        Button btn = new Button(this);
        btn.setText("ACEPTAR");
        btn.setTextSize(10);
        btn.setTextColor(color(R.color.dorado));
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        btn.setOnClickListener(v -> agregarMisionActiva(nombre, recompensa, item));

        item.addView(icono); item.addView(info); item.addView(btn);
        layoutDisponibles.addView(item);
    }

    private void agregarMisionActiva(String nombre, String recompensa, LinearLayout eliminar) {
        layoutDisponibles.removeView(eliminar);
        if (layoutActivas.getChildCount() > 0) agregarSeparador(layoutActivas);

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(10), 0, dp(10));
        item.setGravity(Gravity.CENTER_VERTICAL);

        TextView icono = new TextView(this);
        icono.setText("⚔️");
        icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.addView(txt(nombre, 13, R.color.texto, true));
        info.addView(txt("Recompensa:  " + recompensa, 11, R.color.dorado_claro, false));

        TextView badge = new TextView(this);
        badge.setText("EN CURSO");
        badge.setTextColor(color(R.color.dorado));
        badge.setTextSize(9);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));
        badge.setBackgroundColor(color(R.color.boton_bg));

        item.addView(icono); item.addView(info); item.addView(badge);
        layoutActivas.addView(item);
    }
    private void mostrarDialogoEstetico(String titulo, String mensaje) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));

        TextView t = txt("✦  " + titulo.toUpperCase() + "  ✦", 14, R.color.dorado, true);
        t.setTypeface(Typeface.create("serif", Typeface.BOLD));
        t.setGravity(Gravity.CENTER);
        root.addView(t);

        root.addView(separadorDorado());

        TextView m = txt(mensaje, 13, R.color.texto, false);
        m.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLp.setMargins(0, dp(12), 0, dp(12));
        m.setLayoutParams(mLp);
        root.addView(m);

        root.addView(separadorDorado());

        Button b = new Button(this);
        b.setText("ACEPTAR");
        b.setTextColor(color(R.color.dorado));
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(42));
        bLp.setMargins(0, dp(12), 0, 0);
        b.setLayoutParams(bLp);
        root.addView(b);

        AlertDialog d = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(root).create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        b.setOnClickListener(v -> d.dismiss());
        d.show();
    }
    /** dp a px */
    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    /** getColor */
    private int color(int res) {
        return getResources().getColor(res);
    }

    /** TextView con los parámetros más comunes*/
    private TextView txt(String texto, float sp, int colorRes, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(sp);
        tv.setTextColor(color(colorRes));
        if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    /**Separador línea dorada*/
    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
    }

    /**Separador línea gris*/
    private View separadorBorde() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    /**separador (misiones, hechizos)*/
    private void agregarSeparador(LinearLayout parent) {
        View sep = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, dp(3), 0, dp(3));
        sep.setLayoutParams(lp);
        sep.setBackgroundColor(color(R.color.borde));
        parent.addView(sep);
    }

    /**columna para tarjeta de hechizo*/
    private LinearLayout propCol(String etiqueta, String valor) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView et = txt(etiqueta, 7, R.color.dorado_claro, false);
        et.setGravity(Gravity.CENTER);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView val = txt(valor, 10, R.color.texto, false);
        val.setGravity(Gravity.CENTER);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        col.addView(et);
        col.addView(val);
        return col;
    }

    /**Separador vertical*/
    private View propSep() {
        View v = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1),
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, dp(4), 0, dp(4));
        v.setLayoutParams(lp);
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }
}