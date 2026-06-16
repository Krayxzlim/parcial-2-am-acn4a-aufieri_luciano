package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpellListActivity extends AppCompatActivity {

    private static final String API_BASE =
            "https://api.open5e.com/v1/spells/?limit=100&document__slug=wotc-srd";

    private final ExecutorService executor    = Executors.newSingleThreadExecutor();
    private final Handler         mainHandler = new Handler(Looper.getMainLooper());

    // Vistas
    private LinearLayout layoutLista;
    private TextView     txtEstado;
    private EditText     editBuscar;
    private Spinner      spinnerNivel, spinnerEscuela, spinnerClase;

    // Datos
    private final List<SpellModel> todos     = new ArrayList<>();
    private final List<SpellModel> filtrados = new ArrayList<>();

    // Filtros activos
    private String busqueda    = "";
    private int    nivelFiltro = -1;   // -1 = todos
    private String escuelaFiltro = ""; // "" = todas
    private String claseFiltro   = ""; // "" = todas

    // Opciones de filtros
    private static final String[] NIVELES_LABELS = {
            "Todos los niveles","Truco","Nivel 1","Nivel 2","Nivel 3",
            "Nivel 4","Nivel 5","Nivel 6","Nivel 7","Nivel 8","Nivel 9"
    };
    private static final int[] NIVELES_VALUES = {-1,0,1,2,3,4,5,6,7,8,9};

    private static final String[] ESCUELAS = {
            "Todas las escuelas","Abjuration","Conjuration","Divination",
            "Enchantment","Evocation","Illusion","Necromancy","Transmutation"
    };

    private static final String[] CLASES = {
            "Todas las clases","bard","cleric","druid",
            "paladin","ranger","sorcerer","warlock","wizard"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildLayout());
        cargarHechizos();
    }

    //Layout
    private View buildLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(color(R.color.fondo));

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(color(R.color.seccion_fondo));
        header.setPadding(dp(16), dp(36), dp(16), dp(12));

        TextView btnVolver = new TextView(this);
        btnVolver.setText("← VOLVER");
        btnVolver.setTextColor(color(R.color.dorado_claro));
        btnVolver.setTextSize(11);
        btnVolver.setTypeface(Typeface.MONOSPACE);
        btnVolver.setClickable(true);
        btnVolver.setFocusable(true);
        LinearLayout.LayoutParams vLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        vLp.setMargins(0, 0, 0, dp(8));
        btnVolver.setLayoutParams(vLp);
        btnVolver.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        header.addView(btnVolver);

        TextView titulo = new TextView(this);
        titulo.setText("📖  GRIMORIO ARCANO");
        titulo.setTextColor(color(R.color.dorado));
        titulo.setTextSize(18);
        titulo.setTypeface(Typeface.create("serif", Typeface.BOLD));
        titulo.setGravity(Gravity.CENTER);
        header.addView(titulo);

        TextView sub = new TextView(this);
        sub.setText("TODOS LOS HECHIZOS · OPEN5E API");
        sub.setTextColor(color(R.color.texto_secundario));
        sub.setTextSize(9);
        sub.setLetterSpacing(0.1f);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, dp(4), 0, 0);
        sub.setLayoutParams(subLp);
        header.addView(sub);

        root.addView(header);

        // Panel de filtros
        LinearLayout filtros = new LinearLayout(this);
        filtros.setOrientation(LinearLayout.VERTICAL);
        filtros.setBackgroundColor(color(R.color.seccion_fondo));
        filtros.setPadding(dp(10), dp(10), dp(10), dp(10));

        // Buscador por nombre
        editBuscar = new EditText(this);
        editBuscar.setHint("🔍  Buscar por nombre...");
        editBuscar.setHintTextColor(color(R.color.texto_secundario));
        editBuscar.setTextColor(color(R.color.texto));
        editBuscar.setTextSize(13);
        editBuscar.setBackgroundResource(R.drawable.stat_bg);
        editBuscar.setPadding(dp(12), dp(8), dp(12), dp(8));
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editLp.setMargins(0, 0, 0, dp(8));
        editBuscar.setLayoutParams(editLp);
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                busqueda = s.toString().trim().toLowerCase();
                aplicarFiltros();
            }
        });
        filtros.addView(editBuscar);

        // Fila spinners: Nivel + Escuela
        LinearLayout fila1 = new LinearLayout(this);
        fila1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams fila1Lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fila1Lp.setMargins(0, 0, 0, dp(6));
        fila1.setLayoutParams(fila1Lp);

        spinnerNivel = spinnerFiltro(NIVELES_LABELS, pos -> {
            nivelFiltro = NIVELES_VALUES[pos];
            aplicarFiltros();
        });
        LinearLayout.LayoutParams nivelLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nivelLp.setMargins(0, 0, dp(4), 0);
        spinnerNivel.setLayoutParams(nivelLp);
        fila1.addView(spinnerNivel);

        spinnerEscuela = spinnerFiltro(ESCUELAS, pos -> {
            escuelaFiltro = pos == 0 ? "" : ESCUELAS[pos].toLowerCase();
            aplicarFiltros();
        });
        spinnerEscuela.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        fila1.addView(spinnerEscuela);
        filtros.addView(fila1);

        // Spinner clase (fila completa)
        spinnerClase = spinnerFiltro(CLASES, pos -> {
            claseFiltro = pos == 0 ? "" : CLASES[pos];
            aplicarFiltros();
        });
        spinnerClase.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        filtros.addView(spinnerClase);

        root.addView(filtros);

        // Separador dorado
        View sepOro = new View(this);
        sepOro.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        sepOro.setBackgroundColor(color(R.color.dorado_borde));
        root.addView(sepOro);

        // Estado (cargando / error / sin resultados)
        txtEstado = new TextView(this);
        txtEstado.setText("🔮  Cargando hechizos...");
        txtEstado.setTextColor(color(R.color.texto_secundario));
        txtEstado.setTextSize(13);
        txtEstado.setGravity(Gravity.CENTER);
        txtEstado.setPadding(0, dp(24), 0, dp(8));
        root.addView(txtEstado);

        // Lista scrolleable
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        layoutLista = new LinearLayout(this);
        layoutLista.setOrientation(LinearLayout.VERTICAL);
        layoutLista.setPadding(0, 0, 0, dp(24));
        scroll.addView(layoutLista);
        root.addView(scroll);

        return root;
    }

    //Carga desde la API
    private void cargarHechizos() {
        txtEstado.setText("🔮  Cargando hechizos...");
        txtEstado.setVisibility(View.VISIBLE);
        layoutLista.removeAllViews();
        todos.clear();

        executor.execute(() -> {
            // Carga hasta 300 hechizos (3 páginas de 100)
            List<SpellModel> acumulados = new ArrayList<>();
            String nextUrl = API_BASE;

            try {
                while (nextUrl != null && acumulados.size() < 300) {
                    String json = fetchUrl(nextUrl);
                    if (json == null) break;

                    JSONObject obj     = new JSONObject(json);
                    JSONArray  results = obj.getJSONArray("results");
                    String     next    = obj.isNull("next") ? null : obj.getString("next");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject s = results.getJSONObject(i);
                        acumulados.add(new SpellModel(
                                s.optString("slug"),
                                s.optString("name"),
                                s.optString("desc"),
                                s.optString("higher_level"),
                                s.optString("range"),
                                s.optString("components"),
                                s.optString("material"),
                                s.optString("casting_time"),
                                s.optString("level"),
                                s.optInt("level_int", 0),
                                s.optString("school"),
                                s.optString("duration"),
                                s.optBoolean("requires_concentration", false),
                                s.optBoolean("can_be_cast_as_ritual", false),
                                s.optString("dnd_class"),
                                null  // sin imagen personalizada
                        ));
                    }
                    nextUrl = next;
                }

                final List<SpellModel> resultado = new ArrayList<>(acumulados);
                mainHandler.post(() -> {
                    todos.addAll(resultado);
                    aplicarFiltros();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    txtEstado.setText("⚠️  No se pudo conectar a la API.\nVerificá tu conexión.");
                    txtEstado.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void aplicarFiltros() {
        filtrados.clear();
        for (SpellModel m : todos) {
            // Nombre
            if (!busqueda.isEmpty() && !m.name.toLowerCase().contains(busqueda))
                continue;
            // Nivel
            if (nivelFiltro >= 0 && m.levelInt != nivelFiltro)
                continue;
            // Escuela
            if (!escuelaFiltro.isEmpty() &&
                    !m.school.toLowerCase().contains(escuelaFiltro))
                continue;
            // Clase
            if (!claseFiltro.isEmpty() &&
                    !m.dndClass.toLowerCase().contains(claseFiltro))
                continue;
            filtrados.add(m);
        }
        renderLista();
    }

    //Renderizar
    private void renderLista() {
        layoutLista.removeAllViews();

        if (filtrados.isEmpty()) {
            txtEstado.setText(todos.isEmpty()
                    ? "🔮  Cargando hechizos..."
                    : "✦  Sin resultados para los filtros aplicados");
            txtEstado.setVisibility(View.VISIBLE);
            return;
        }
        txtEstado.setVisibility(View.GONE);

        // Contador
        TextView counter = new TextView(this);
        counter.setText(filtrados.size() + " hechizos encontrados");
        counter.setTextColor(color(R.color.texto_secundario));
        counter.setTextSize(10);
        counter.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cLp.setMargins(0, dp(8), 0, dp(4));
        counter.setLayoutParams(cLp);
        layoutLista.addView(counter);

        for (int i = 0; i < filtrados.size(); i++) {
            SpellModel spell = filtrados.get(i);

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setPadding(dp(12), dp(12), dp(12), dp(12));
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setBackgroundColor(i % 2 == 0
                    ? color(R.color.seccion_fondo) : color(R.color.fondo));
            item.setClickable(true);
            item.setFocusable(true);

            // Badge nivel
            int[] bc = getBadgeColors(spell.levelInt);
            TextView badge = new TextView(this);
            badge.setText(spell.getBadgeLabel());
            badge.setTextColor(bc[1]);
            badge.setBackgroundColor(bc[0]);
            badge.setTextSize(8);
            badge.setPadding(dp(7), dp(3), dp(7), dp(3));
            LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            badgeLp.setMargins(0, 0, dp(10), 0);
            badge.setLayoutParams(badgeLp);
            item.addView(badge);

            // Info
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView nom = new TextView(this);
            nom.setText(spell.name);
            nom.setTextColor(color(R.color.texto));
            nom.setTextSize(13);
            nom.setTypeface(Typeface.DEFAULT_BOLD);
            info.addView(nom);

            // Escuela · Tiempo de lanzamiento
            String subTxt = capitalize(spell.school);
            if (spell.castingTime != null && !spell.castingTime.isEmpty())
                subTxt += "  ·  " + spell.castingTime;
            if (spell.requiresConcentration) subTxt += "  ·  Conc.";
            if (spell.canBeCastAsRitual)     subTxt += "  ·  Ritual";

            TextView subView = new TextView(this);
            subView.setText(subTxt);
            subView.setTextColor(color(R.color.texto_secundario));
            subView.setTextSize(10);
            info.addView(subView);

            item.addView(info);

            // Flecha
            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextColor(color(R.color.dorado_borde));
            arrow.setTextSize(18);
            item.addView(arrow);

            final SpellModel spellFinal = spell;
            item.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
                new Handler(Looper.getMainLooper()).postDelayed(
                        () -> abrirDetalle(spellFinal), 120);
            });

            layoutLista.addView(item);

            // Separador fino
            if (i < filtrados.size() - 1) {
                View sep = new View(this);
                sep.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
                sep.setBackgroundColor(color(R.color.borde));
                layoutLista.addView(sep);
            }
        }
    }

    // Abrir DetalleHechizo con extras
    private void abrirDetalle(SpellModel spell) {
        Intent intent = new Intent(this, DetalleHechizo.class);
        intent.putExtra(DetalleHechizo.EXTRA_SLUG,         spell.slug);
        intent.putExtra(DetalleHechizo.EXTRA_NAME,         spell.name);
        intent.putExtra(DetalleHechizo.EXTRA_DESC,         spell.desc);
        intent.putExtra(DetalleHechizo.EXTRA_HIGHER_LEVEL, spell.higherLevel);
        intent.putExtra(DetalleHechizo.EXTRA_RANGE,        spell.range);
        intent.putExtra(DetalleHechizo.EXTRA_COMPONENTS,   spell.components);
        intent.putExtra(DetalleHechizo.EXTRA_MATERIAL,     spell.material);
        intent.putExtra(DetalleHechizo.EXTRA_CASTING_TIME, spell.castingTime);
        intent.putExtra(DetalleHechizo.EXTRA_LEVEL,        spell.level);
        intent.putExtra(DetalleHechizo.EXTRA_LEVEL_INT,    spell.levelInt);
        intent.putExtra(DetalleHechizo.EXTRA_SCHOOL,       spell.school);
        intent.putExtra(DetalleHechizo.EXTRA_DURATION,     spell.duration);
        intent.putExtra(DetalleHechizo.EXTRA_CONCENTRATION,spell.requiresConcentration);
        intent.putExtra(DetalleHechizo.EXTRA_RITUAL,       spell.canBeCastAsRitual);
        intent.putExtra(DetalleHechizo.EXTRA_DND_CLASS,    spell.dndClass);
        intent.putExtra(DetalleHechizo.EXTRA_IMAGEN_URL,   spell.imagenUrl);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    private Spinner spinnerFiltro(String[] opciones, SpinnerCallback callback) {
        Spinner sp = new Spinner(this);
        sp.setBackgroundResource(R.drawable.stat_bg);
        ArrayAdapter<String> adpt = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);
        adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adpt);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                callback.onSelected(pos);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
        return sp;
    }

    interface SpinnerCallback { void onSelected(int pos); }

    private int[] getBadgeColors(int levelInt) {
        switch (levelInt) {
            case 0:  return new int[]{color(R.color.badge_truco),  color(R.color.badge_truco_texto)};
            case 1:  return new int[]{color(R.color.badge_nivel1), color(R.color.badge_nivel1_texto)};
            case 2:  return new int[]{color(R.color.badge_nivel2), color(R.color.badge_nivel2_texto)};
            default: return new int[]{color(R.color.badge_nivel3), color(R.color.badge_nivel3_texto)};
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String fetchUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Accept","application/json");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            return sb.toString();
        } catch (Exception e) { return null; }
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    private int color(int res) {
        return androidx.core.content.ContextCompat.getColor(this, res);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}