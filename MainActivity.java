package com.bacbo.sinal;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private final ArrayList<String> hist = new ArrayList<>();
    private LinearLayout history;
    private TextView txtSignal, txtConfidence, txtStats, txtStatus;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        history = findViewById(R.id.history);
        txtSignal = findViewById(R.id.txtSignal);
        txtConfidence = findViewById(R.id.txtConfidence);
        txtStats = findViewById(R.id.txtStats);
        txtStatus = findViewById(R.id.txtStatus);

        findViewById(R.id.btnP).setOnClickListener(v -> add("P"));
        findViewById(R.id.btnB).setOnClickListener(v -> add("B"));
        findViewById(R.id.btnE).setOnClickListener(v -> add("E"));
        findViewById(R.id.btnUndo).setOnClickListener(v -> {
            if (!hist.isEmpty()) { hist.remove(hist.size()-1); refresh(); }
        });
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            hist.clear(); refresh();
        });
        refresh();
    }

    private void add(String s) { hist.add(s); refresh(); }

    private void refresh() {
        history.removeAllViews();
        int p=0,b=0,e=0;
        for (String s: hist) {
            if (s.equals("P")) p++; else if (s.equals("B")) b++; else e++;
            TextView c = new TextView(this);
            c.setText(s);
            c.setTextColor(Color.WHITE);
            c.setTextSize(16);
            c.setGravity(17);
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(s.equals("P") ? Color.rgb(25,210,255) :
                       s.equals("B") ? Color.rgb(255,82,82) : Color.rgb(255,214,40));
            c.setBackground(gd);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(52,52);
            lp.setMargins(5,8,5,8);
            history.addView(c, lp);
        }
        txtStats.setText("P: " + p + "   B: " + b + "   E: " + e);

        if (hist.size() < 8) {
            txtSignal.setText("SEM SINAL");
            txtConfidence.setText("Confiança estatística: —");
            txtStatus.setText("ANÁLISE: introduza pelo menos 8 resultados");
            return;
        }

        // Heurística simples e transparente: frequência recente + tendência de alternância.
        int n = Math.min(12, hist.size());
        int cp=0, cb=0;
        for (int i=hist.size()-n; i<hist.size(); i++) {
            if (hist.get(i).equals("P")) cp++;
            if (hist.get(i).equals("B")) cb++;
        }
        String last = hist.get(hist.size()-1);
        String signal;
        double conf;
        if (cp > cb + 2) { signal = "SINAL: B"; conf = Math.min(72, 50 + (cp-cb)*5); }
        else if (cb > cp + 2) { signal = "SINAL: P"; conf = Math.min(72, 50 + (cb-cp)*5); }
        else if (last.equals("P")) { signal = "SINAL: B"; conf = 52; }
        else if (last.equals("B")) { signal = "SINAL: P"; conf = 52; }
        else { signal = "SEM SINAL"; conf = 50; }

        txtSignal.setText(signal);
        txtConfidence.setText(String.format(Locale.US,
            "Confiança estatística estimada: %.0f%% (não é probabilidade garantida)", conf));
        txtStatus.setText("ANÁLISE: histórico atualizado • " + hist.size() + " entradas");
    }
}
