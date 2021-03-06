package com.example.usuario.ejerciciosdered;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;

public class Ejercicio5Activity extends AppCompatActivity  implements  View.OnClickListener{

    Button btnDescargarImagenes, btnImagenAnterior, btnImagenSiguiente;
    ImageView imgEstablecerImagen;
    EditText edtRutaAlFichero;
    List<String> rutasAImagenes;
    int numeroDeImagenes;
    int imagenSeleccionada;
    boolean imagenesCargadasConExito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio5);
        numeroDeImagenes = 0;
        imagenSeleccionada = 0;
        imagenesCargadasConExito = false;
        btnDescargarImagenes = findViewById(R.id.btnDescargarImagenes);
        btnDescargarImagenes.setOnClickListener(this);
        btnImagenAnterior = findViewById(R.id.btnImagenAnterior);
        btnImagenAnterior.setOnClickListener(this);
        btnImagenSiguiente = findViewById(R.id.btnImagenSiguiente);
        btnImagenSiguiente.setOnClickListener(this);
        imgEstablecerImagen = findViewById(R.id.imgEstablecerImagen);
        edtRutaAlFichero = findViewById(R.id.edtRutaAlFichero);

    }

    @Override
    public void onClick(View v) {
        if (v == btnDescargarImagenes){
            descargaFichero(edtRutaAlFichero.getText().toString());
        }


            if (v == btnImagenAnterior) {
                if(imagenesCargadasConExito)
                retrocederImagen();
                else
                    Toast.makeText(Ejercicio5Activity.this, "No hay imagenes que cargar", Toast.LENGTH_SHORT).show();
            }

            if (v == btnImagenSiguiente) {
            if(imagenesCargadasConExito) {
                pasarImagen();
            }
                else {
                Toast.makeText(Ejercicio5Activity.this, "No hay imagenes que cargar", Toast.LENGTH_SHORT).show();
            }
            }
        }



    private void establecerImagen(String ruta) {
        OkHttpClient client = new OkHttpClient();
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();

        picasso.with(this).load(ruta)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imgEstablecerImagen);

    }

    private void descargaFichero(String url)
    {

        rutasAImagenes = new ArrayList<String>();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(/* Context */ this) {
        ProgressDialog pd;

                @Override
                public void onStart() {

                    imagenSeleccionada = 0;
                    numeroDeImagenes = 0;
                    pd = new ProgressDialog(Ejercicio5Activity.this);
                    pd.setTitle("Por favor espere...");
                    pd.setMessage("AsyncHttpResponseHadler está en progreso");
                    pd.setIndeterminate(false);
                    pd.setCancelable(false);
                    pd.show();
                    Toast.makeText(Ejercicio5Activity.this, "Descargando fichero con las rutas de las imagenes...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Toast.makeText(Ejercicio5Activity.this, "Se ha producido un error en la descarga del fichero", Toast.LENGTH_SHORT).show();
                    imagenesCargadasConExito = false;
                    imgEstablecerImagen.setImageResource(R.drawable.error);
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    Toast.makeText(Ejercicio5Activity.this, "El fichero con las rutas a las imagenes se han descargado con exito", Toast.LENGTH_SHORT).show();
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(file);
                        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                        String linea;
                        while ((linea = in.readLine()) != null) {
                            numeroDeImagenes++;
                            rutasAImagenes.add(linea);
                        }
                        in.close();
                        fis.close();
                        imagenesCargadasConExito = true;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    pd.dismiss();
                    if (imagenesCargadasConExito){
                        establecerImagen(rutasAImagenes.get(imagenSeleccionada));
                    }
                }

            });
        }


    private void pasarImagen(){
        if (imagenSeleccionada + 1 < numeroDeImagenes) {
            imagenSeleccionada++;
            establecerImagen(rutasAImagenes.get(imagenSeleccionada));
        }
    }

    private void retrocederImagen(){
        if (imagenSeleccionada -1 >= 0) {
            imagenSeleccionada--;
            establecerImagen(rutasAImagenes.get(imagenSeleccionada));
        }
    }

}
