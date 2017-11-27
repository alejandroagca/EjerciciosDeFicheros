package com.example.usuario.ejerciciosdered;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ejercicio2Activity extends AppCompatActivity implements View.OnClickListener {

    Button btnLanzarAlarmas, btnGuardarAlarma, btnBorrarAlarmas;
    EditText edtIntroduceTiempo, edtIntroduceMensaje;
    Memoria miMemoria;
    File miFichero;
    TextView txvVerAlarmas;
    MediaPlayer mp;
    public static int cuantasAlarmas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio2);
        cuantasAlarmas = 0;
        mp = MediaPlayer.create(this,R.raw.sonido);
        btnGuardarAlarma = findViewById(R.id.btnGuardarAlarma);
        btnGuardarAlarma.setOnClickListener(this);
        btnLanzarAlarmas = findViewById(R.id.btnLanzarAlarmas);
        btnLanzarAlarmas.setOnClickListener(this);
        btnBorrarAlarmas = findViewById(R.id.btnBorrarAlarmas);
        btnBorrarAlarmas.setOnClickListener(this);
        txvVerAlarmas = findViewById(R.id.txvVerAlarmas);
        edtIntroduceMensaje = findViewById(R.id.edtIntroduceMensaje);
        edtIntroduceTiempo = findViewById(R.id.edtIntroduceTiempo);
        miMemoria = new Memoria(getApplicationContext());
        miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "alarmas.txt");
        if (miFichero.exists()) {
            comprobarAlarmas();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnGuardarAlarma){
            guardarAlarma();
        }

        if (v == btnLanzarAlarmas){
        }

        if (v == btnBorrarAlarmas){
            borrarAlarmas();
        }
    }

    private void borrarAlarmas(){
        if (miFichero.exists()) {
            miFichero.delete();
            cuantasAlarmas = 0;
            txvVerAlarmas.setText("");
            Toast.makeText(Ejercicio2Activity.this, "Se ha borrado el fichero alarmas.txt", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Ejercicio2Activity.this, "El fichero alarmas.txt no se ha creado todavia", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarAlarma(){
        if(miMemoria.disponibleEscritura()) {
            if (cuantasAlarmas <5) {
                String alarma = edtIntroduceTiempo.getText().toString() + "," + edtIntroduceMensaje.getText().toString();
                miMemoria.escribirExterna("alarmas.txt", alarma, true, "UTF-8");
                comprobarAlarmas();
                Toast.makeText(Ejercicio2Activity.this, "Alarma guardada", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Ejercicio2Activity.this, "Ya tienes 4 alarmas creadas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void comprobarAlarmas(){
        cuantasAlarmas = 0;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String linea;

        try {
            fis = new FileInputStream(miFichero);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            txvVerAlarmas.setText("");
            while ((linea = br.readLine())!= null) {
                cuantasAlarmas++;
                txvVerAlarmas.setText(txvVerAlarmas.getText().toString() + linea + "\n");
            }

            br.close();
            isr.close();
            fis.close();

        } catch (FileNotFoundException e){
            Toast.makeText(Ejercicio2Activity.this, "El fichero alarmas.txt no se ha creado todavia", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(Ejercicio2Activity.this, "Error de E/S.", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(Ejercicio2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public class MiContador extends CountDownTimer {
        public MiContador(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long minutos = (millisUntilFinished/1000) / 60;

        }

        @Override
        public void onFinish()
        {
            mp.start();


        }
    }
}
