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

    Button btnLanzarAlarmas, btnGuardarAlarma, btnBorrarAlarmas, btnVerAlarmas;
    EditText edtIntroduceTiempo, edtIntroduceMensaje;
    Memoria miMemoria;
    File miFichero;
    TextView txvVerMensaje;
    MediaPlayer mp;
    MiContador temporizador;
    int cuantasAlarmas;
    String [] mensajesAlarmas;
    int [] tiemposAlarmas;
    int turno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio2);
        cuantasAlarmas = 0;
        mensajesAlarmas = new String[5];
        tiemposAlarmas = new int[5];
        turno = 0;
        mp = MediaPlayer.create(this,R.raw.sonido);
        btnGuardarAlarma = findViewById(R.id.btnGuardarAlarma);
        btnGuardarAlarma.setOnClickListener(this);
        btnLanzarAlarmas = findViewById(R.id.btnLanzarAlarmas);
        btnLanzarAlarmas.setOnClickListener(this);
        btnBorrarAlarmas = findViewById(R.id.btnBorrarAlarmas);
        btnBorrarAlarmas.setOnClickListener(this);
        btnVerAlarmas = findViewById(R.id.btnVerAlarmas);
        btnVerAlarmas.setOnClickListener(this);
        txvVerMensaje = findViewById(R.id.txvVerMensaje);
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
            lanzarAlarmas();
        }

        if (v == btnBorrarAlarmas){
            borrarAlarmas();
        }

        if (v == btnVerAlarmas){
            verAlarmas();
        }
    }

    private void borrarAlarmas(){
        comprobarAlarmas();
        if (miFichero.exists()) {
            miFichero.delete();
            cuantasAlarmas = 0;
            txvVerMensaje.setText("");
            Toast.makeText(Ejercicio2Activity.this, "Se ha borrado el fichero alarmas.txt", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarAlarma(){
        if(miMemoria.disponibleEscritura()) {
            if (cuantasAlarmas <5) {
                if (edtIntroduceTiempo.getText().toString().length() > 0 && edtIntroduceMensaje.getText().toString().length() > 0){
                    String alarma = edtIntroduceTiempo.getText().toString() + "," + edtIntroduceMensaje.getText().toString();
                miMemoria.escribirExterna("alarmas.txt", alarma, true, "UTF-8");
                comprobarAlarmas();
                Toast.makeText(Ejercicio2Activity.this, "Alarma guardada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Ejercicio2Activity.this, "Alguno de los campos no ha sido rellenado", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(Ejercicio2Activity.this, "Ya tienes 5 alarmas creadas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void verAlarmas(){
        comprobarAlarmas();
        txvVerMensaje.setText("");
        for (int i = 0; i < cuantasAlarmas; i++){
            txvVerMensaje.setText(txvVerMensaje.getText().toString() + "Alarma: " + mensajesAlarmas[i] + "\t   Tiempo: " + tiemposAlarmas[i]  + "\n");
        }
    }

    private void comprobarAlarmas(){
        cuantasAlarmas = 0;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String linea;
        String [] estaAlarma;

        try {
            fis = new FileInputStream(miFichero);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            while ((linea = br.readLine())!= null) {
                estaAlarma = linea.split(",");
                tiemposAlarmas[cuantasAlarmas] = Integer.parseInt(estaAlarma[0]);
                mensajesAlarmas[cuantasAlarmas++] = estaAlarma[1];
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

    private void lanzarAlarmas(){
        turno = 0;
        comprobarAlarmas();
            if (cuantasAlarmas > 0) {
                temporizador = new MiContador((long) tiemposAlarmas[turno] * 60 * 1000, (long) 1000.0);
                temporizador.start();
                btnVerAlarmas.setEnabled(false);
                btnBorrarAlarmas.setEnabled(false);
                btnLanzarAlarmas.setEnabled(false);
                btnGuardarAlarma.setEnabled(false);

            }

    }

    public class MiContador extends CountDownTimer {
        public MiContador(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long minutos = (millisUntilFinished/1000) / 60;
            long segundos = (millisUntilFinished/1000) % 60;
            txvVerMensaje.setText("La alarma " + mensajesAlarmas[turno] + " sonará en " + minutos + ":" + segundos );
        }

        @Override
        public void onFinish()
        {
            mp.start();
            turno++;
            if (turno < cuantasAlarmas) {
                temporizador = new MiContador((long) tiemposAlarmas[turno] * 60 * 1000, (long) 1000.0);
                temporizador.start();
            }
            else{
                txvVerMensaje.setText("Todas las alarmas han finalizado");
                btnVerAlarmas.setEnabled(true);
                btnBorrarAlarmas.setEnabled(true);
                btnLanzarAlarmas.setEnabled(true);
                btnGuardarAlarma.setEnabled(true);
            }

        }
    }
}
