package com.example.usuario.ejerciciosdered;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnEjercicio1;
    Button btnEjercicio2;
    Button btnEjercicio3;
    Button btnEjercicio4;
    Button btnEjercicio5;
    Button btnEjercicio6;
    Button btnEjercicio7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnEjercicio1 = findViewById(R.id.btnEjercicio1);
        btnEjercicio1.setOnClickListener(this);
        btnEjercicio2 = findViewById(R.id.btnEjercicio2);
        btnEjercicio2.setOnClickListener(this);
        btnEjercicio3 = findViewById(R.id.btnEjercicio3);
        btnEjercicio3.setOnClickListener(this);
        btnEjercicio4 = findViewById(R.id.btnEjercicio4);
        btnEjercicio4.setOnClickListener(this);
        btnEjercicio5 = findViewById(R.id.btnEjercicio5);
        btnEjercicio5.setOnClickListener(this);
        btnEjercicio6 = findViewById(R.id.btnEjercicio6);
        btnEjercicio6.setOnClickListener(this);
        btnEjercicio7 = findViewById(R.id.btnEjercicio7);
        btnEjercicio7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        if (v == btnEjercicio1){
            i = new Intent(this, Ejercicio1Activity.class);
            startActivity(i);
        }

    }
}
