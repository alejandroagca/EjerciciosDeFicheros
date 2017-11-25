package com.example.usuario.ejerciciosdered;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Ejercicio1Activity extends AppCompatActivity implements View.OnClickListener{

    Button btnGuardarContacto, btnListarContactos,btnBorrarContactos;
    EditText edtIntroduceNombre, edtIntroduceTelefono, edtIntroduceCorreo;
    TextView txvMisContactos;
    static final String NOMBREFICHERO = "agenda.txt";
    Memoria miMemoria;
    File miFichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio1);

        txvMisContactos = findViewById(R.id.txvMisContactos);

        btnGuardarContacto = findViewById(R.id.btnGuardarContacto);
        btnGuardarContacto.setOnClickListener(this);

        btnListarContactos = findViewById(R.id.btnListarContacto);
        btnListarContactos.setOnClickListener(this);

        btnBorrarContactos = findViewById(R.id.btnBorrarContactos);
        btnBorrarContactos.setOnClickListener(this);

        edtIntroduceNombre = findViewById(R.id.edtIntroduceNombre);
        edtIntroduceTelefono = findViewById(R.id.edtIntroduceTelefono);
        edtIntroduceCorreo = findViewById(R.id.edtIntroduceCorreo);

        miMemoria = new Memoria(getApplicationContext());

        miFichero = new File(getApplicationContext().getFilesDir(), NOMBREFICHERO);

    }


    @Override
    public void onClick(View v) {
        if (v == btnGuardarContacto){

            String texto;

            if (edtIntroduceNombre.getText().toString().length() > 0 && edtIntroduceTelefono.getText().toString().length() > 0 && edtIntroduceCorreo.getText().toString().length()> 0){
            texto = edtIntroduceNombre.getText().toString() + "," + edtIntroduceTelefono.getText().toString() + "," + edtIntroduceCorreo.getText().toString();
            miMemoria.escribirInterna(NOMBREFICHERO, texto, true, "UTF-8");
            }

            else{
                Toast.makeText(this, "No has rellenado alguno de los datos requeridos", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnListarContactos){
            listarContactos();
        }

        if (v == btnBorrarContactos){
            borrarAgenda();
        }
    }




    public void listarContactos(){

        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String linea;
        String[] datosDelContacto;

        try {
            fis = new FileInputStream(miFichero);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            txvMisContactos.setText("");

            while ((linea = br.readLine()) != null) {
                datosDelContacto = linea.split(",");
                txvMisContactos.setText(txvMisContactos.getText().toString() + "Nombre: " + datosDelContacto[0] + "\n");
                txvMisContactos.setText(txvMisContactos.getText().toString() + "Teléfono: " + datosDelContacto[1] + "\n");
                txvMisContactos.setText(txvMisContactos.getText().toString() + "Correo: " + datosDelContacto[2] + "\n\n");
            }

            br.close();
            isr.close();
            fis.close();

        } catch (FileNotFoundException e){
            Toast.makeText(this, "La agenda no se ha creado todavia", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error de E/S.", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void borrarAgenda(){
        if (miFichero.exists()){
            miFichero.delete();
            txvMisContactos.setText("");
            Toast.makeText(this, "Agenda borrada", Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(this, "La agenda no se ha creado todavia", Toast.LENGTH_SHORT).show();
        }
    }
}
