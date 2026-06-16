package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

/**
 * Pantalla de detalle de hechizo.
 * Recibe datos vía Intent extras desde MainActivity.
 * Campos alineados con la Open5e API v1.
 */
public class DetalleHechizo extends AppCompatActivity {

    //Claves de extras (alineadas con campos de la API)
    public static final String EXTRA_SLUG           = "spell_slug";
    public static final String EXTRA_NAME           = "spell_name";
    public static final String EXTRA_DESC           = "spell_desc";
    public static final String EXTRA_HIGHER_LEVEL   = "spell_higher_level";
    public static final String EXTRA_RANGE          = "spell_range";
    public static final String EXTRA_COMPONENTS     = "spell_components";
    public static final String EXTRA_MATERIAL       = "spell_material";
    public static final String EXTRA_CASTING_TIME   = "spell_casting_time";
    public static final String EXTRA_LEVEL          = "spell_level";
    public static final String EXTRA_LEVEL_INT      = "spell_level_int";
    public static final String EXTRA_SCHOOL         = "spell_school";
    public static final String EXTRA_DURATION       = "spell_duration";
    public static final String EXTRA_CONCENTRATION  = "spell_concentration";
    public static final String EXTRA_RITUAL         = "spell_ritual";
    public static final String EXTRA_DND_CLASS      = "spell_dnd_class";
    public static final String EXTRA_IMAGEN_URL     = "spell_imagen_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String name             = intent.getStringExtra(EXTRA_NAME);
        String desc             = intent.getStringExtra(EXTRA_DESC);
        String higherLevel      = intent.getStringExtra(EXTRA_HIGHER_LEVEL);
        String range            = intent.getStringExtra(EXTRA_RANGE);
        String components       = intent.getStringExtra(EXTRA_COMPONENTS);
        String material         = intent.getStringExtra(EXTRA_MATERIAL);
        String castingTime      = intent.getStringExtra(EXTRA_CASTING_TIME);
        String level            = intent.getStringExtra(EXTRA_LEVEL);
        int    levelInt         = intent.getIntExtra(EXTRA_LEVEL_INT, 0);
        String school           = intent.getStringExtra(EXTRA_SCHOOL);
        String duration         = intent.getStringExtra(EXTRA_DURATION);
        boolean concentration   = intent.getBooleanExtra(EXTRA_CONCENTRATION, false);
        boolean ritual          = intent.getBooleanExtra(EXTRA_RITUAL, false);
        String dndClass         = intent.getStringExtra(EXTRA_DND_CLASS);
        String imagenUrl        = intent.getStringExtra(EXTRA_IMAGEN_URL);

        // Badge label a partir de levelInt
        String badgeLabel = (levelInt == 0) ? "TRUCO" : "NIVEL " + levelInt;

        // Layout raíz
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(color(R.color.fondo));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.addView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(color(R.color.seccion_fondo));
        header.setPadding(dp(16), dp(36), dp(16), dp(14));

        // Botón volver
        TextView btnVolver = new TextView(this);
        btnVolver.setText("← VOLVER");
        btnVolver.setTextColor(color(R.color.dorado_claro));
        btnVolver.setTextSize(11);
        btnVolver.setTypeface(Typeface.MONOSPACE);
        btnVolver.setClickable(true);
        btnVolver.setFocusable(true);
        LinearLayout.LayoutParams volverLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        volverLp.setMargins(0, 0, 0, dp(10));
        btnVolver.setLayoutParams(volverLp);
        btnVolver.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        header.addView(btnVolver);

        // Fila badge + escuela
        LinearLayout filaBadge = new LinearLayout(this);
        filaBadge.setOrientation(LinearLayout.HORIZONTAL);
        filaBadge.setGravity(Gravity.CENTER_VERTICAL);
        filaBadge.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        int[] badgeColors = getBadgeColors(levelInt);
        TextView badgeView = new TextView(this);
        badgeView.setText(badgeLabel);
        badgeView.setTextColor(badgeColors[1]);
        badgeView.setBackgroundColor(badgeColors[0]);
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(10), dp(4), dp(10), dp(4));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(8), 0);
        badgeView.setLayoutParams(bLp);
        filaBadge.addView(badgeView);

        // Escuela
        if (school != null && !school.isEmpty()) {
            TextView escuelaView = new TextView(this);
            escuelaView.setText(capitalize(school));
            escuelaView.setTextColor(color(R.color.texto_secundario));
            escuelaView.setTextSize(10);
            escuelaView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            filaBadge.addView(escuelaView);
        }

        // Badges Concentración y Ritual
        if (concentration) {
            TextView c = badge("◎ CONC.");
            filaBadge.addView(c);
        }
        if (ritual) {
            TextView r = badge(" ® RITUAL");
            filaBadge.addView(r);
        }
        header.addView(filaBadge);

        // Nombre
        TextView nomView = new TextView(this);
        nomView.setText(name != null ? name : "");
        nomView.setTextSize(26);
        nomView.setTextColor(color(R.color.dorado));
        nomView.setTypeface(Typeface.create("serif", Typeface.BOLD));
        nomView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nomLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLp.setMargins(0, dp(8), 0, 0);
        nomView.setLayoutParams(nomLp);
        header.addView(nomView);

        // Clases que pueden usar el hechizo
        if (dndClass != null && !dndClass.isEmpty()) {
            TextView claseView = new TextView(this);
            claseView.setText(dndClass);
            claseView.setTextColor(color(R.color.texto_secundario));
            claseView.setTextSize(10);
            claseView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams claseLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            claseLp.setMargins(0, dp(4), 0, 0);
            claseView.setLayoutParams(claseLp);
            header.addView(claseView);
        }

        root.addView(header);
        root.addView(separadorDorado());

        //Imagen desde URL (Glide) o placeholder
        ImageView imgHechizo = new ImageView(this);
        imgHechizo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgHechizo.setBackgroundColor(color(R.color.stat_fondo));
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(200));
        imgHechizo.setLayoutParams(imgLp);

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(this)
                    .load(imagenUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgHechizo);
        } else {
            imgHechizo.setImageResource(R.drawable.ic_launcher_foreground);
        }
        root.addView(imgHechizo);
        root.addView(separadorDorado());

        //Tabla de propiedades (campos de la API)
        LinearLayout props = new LinearLayout(this);
        props.setOrientation(LinearLayout.HORIZONTAL);
        props.setBackgroundColor(color(R.color.stat_fondo));
        props.setPadding(dp(8), dp(14), dp(8), dp(14));

        props.addView(propCol("TIEMPO", castingTime));
        props.addView(propSep());
        props.addView(propCol("ALCANCE", range));
        props.addView(propSep());
        props.addView(propCol("DURACIÓN", duration));
        props.addView(propSep());
        props.addView(propCol("COMP.", components));
        root.addView(props);
        root.addView(separadorBorde());

        // Material (si existe)
        if (material != null && !material.isEmpty()) {
            LinearLayout matBox = new LinearLayout(this);
            matBox.setOrientation(LinearLayout.VERTICAL);
            matBox.setBackgroundColor(color(R.color.fondo));
            matBox.setPadding(dp(16), dp(10), dp(16), dp(10));

            TextView matLabel = labelTexto("✦  MATERIAL");
            matBox.addView(matLabel);

            TextView matVal = new TextView(this);
            matVal.setText(capitalize(material));
            matVal.setTextColor(color(R.color.texto_secundario));
            matVal.setTextSize(13);
            matVal.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            matBox.addView(matVal);
            root.addView(matBox);
            root.addView(separadorBorde());
        }

        // Descripción
        LinearLayout secDesc = new LinearLayout(this);
        secDesc.setOrientation(LinearLayout.VERTICAL);
        secDesc.setPadding(dp(16), dp(16), dp(16), dp(16));
        secDesc.setBackgroundColor(color(R.color.fondo));

        secDesc.addView(labelTexto("✦  DESCRIPCIÓN"));
        secDesc.addView(separadorBorde());

        TextView descView = new TextView(this);
        descView.setText(desc != null ? desc : "");
        descView.setTextColor(color(R.color.texto));
        descView.setTextSize(14);
        descView.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descLp.setMargins(0, dp(10), 0, 0);
        descView.setLayoutParams(descLp);
        secDesc.addView(descView);

        // En niveles superiores
        if (higherLevel != null && !higherLevel.isEmpty()) {
            View sep = new View(this);
            LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            sepLp.setMargins(0, dp(14), 0, dp(14));
            sep.setLayoutParams(sepLp);
            sep.setBackgroundColor(color(R.color.borde));
            secDesc.addView(sep);

            secDesc.addView(labelTexto("✦  EN NIVELES SUPERIORES"));

            TextView hlView = new TextView(this);
            hlView.setText(higherLevel);
            hlView.setTextColor(color(R.color.texto_secundario));
            hlView.setTextSize(13);
            hlView.setLineSpacing(dp(3), 1.0f);
            LinearLayout.LayoutParams hlLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            hlLp.setMargins(0, dp(8), 0, 0);
            hlView.setLayoutParams(hlLp);
            secDesc.addView(hlView);
        }

        root.addView(secDesc);

        // Espacio al final para scroll cómodo
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(40)));
        root.addView(spacer);

        setContentView(scrollView);
    }

    /** Badge para nivel de hechizo */
    private int[] getBadgeColors(int levelInt) {
        switch (levelInt) {
            case 0:  return new int[]{color(R.color.badge_truco),  color(R.color.badge_truco_texto)};
            case 1:  return new int[]{color(R.color.badge_nivel1), color(R.color.badge_nivel1_texto)};
            case 2:  return new int[]{color(R.color.badge_nivel2), color(R.color.badge_nivel2_texto)};
            default: return new int[]{color(R.color.badge_nivel3), color(R.color.badge_nivel3_texto)};
        }
    }

    private TextView badge(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(color(R.color.dorado_claro));
        tv.setTextSize(8);
        tv.setBackgroundColor(color(R.color.boton_bg));
        tv.setPadding(dp(6), dp(3), dp(6), dp(3));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(4), 0, 0, 0);
        tv.setLayoutParams(lp);
        return tv;
    }

    private TextView labelTexto(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(color(R.color.dorado));
        tv.setTextSize(10);
        tv.setTypeface(Typeface.create("serif", Typeface.BOLD));
        tv.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(6));
        tv.setLayoutParams(lp);
        return tv;
    }

    private LinearLayout propCol(String etiqueta, String valor) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView et = new TextView(this);
        et.setText(etiqueta);
        et.setTextColor(color(R.color.dorado_claro));
        et.setTextSize(7);
        et.setGravity(Gravity.CENTER);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView val = new TextView(this);
        val.setText(valor != null ? valor : "-");
        val.setTextColor(color(R.color.texto));
        val.setTextSize(10);
        val.setGravity(Gravity.CENTER);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        col.addView(et);
        col.addView(val);
        return col;
    }

    private View propSep() {
        View v = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1),
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, dp(4), 0, dp(4));
        v.setLayoutParams(lp);
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
    }

    private View separadorBorde() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    private int color(int res) {
        return androidx.core.content.ContextCompat.getColor(this, res);
    }
}