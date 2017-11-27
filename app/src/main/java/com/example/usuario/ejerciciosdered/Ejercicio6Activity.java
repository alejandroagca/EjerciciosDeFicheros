package com.example.usuario.ejerciciosdered;

import android.app.ProgressDialog;
import android.nfc.FormatException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Ejercicio6Activity extends AppCompatActivity implements View.OnClickListener{

    RadioButton rbtDaE;
    Button convertir;
    EditText etDolares;
    EditText etEuros;
    double cambio;
    boolean ficheroDescargadoConExito;
    TextView txvResultadoDeCambio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio6);
        rbtDaE = (RadioButton) findViewById(R.id.radioButtonDaE);
        etDolares = (EditText) findViewById(R.id.editTextDolares);
        etEuros = (EditText) findViewById(R.id.editTextEuros);
        convertir = (Button) findViewById(R.id.buttonConvertir);
        convertir.setOnClickListener(this);
        txvResultadoDeCambio = findViewById(R.id.txvResultadoDeCambio);
        descargaFichero("http://alumno.mobi/~alumno/superior/aguilar/cambio.txt");
    }

    public double Convertir(double cantidad)
    {
        double calculo;
            if (rbtDaE.isChecked()) {
                calculo = (cantidad * cambio);

            } else {
                calculo = (cantidad / cambio);
            }
            return calculo;
    }

    public void onClick(View view)
    {

            if (view == convertir) {
                try {
                    if (ficheroDescargadoConExito) {
                        if (rbtDaE.isChecked()) {

                            double aEuros = Double.parseDouble(etDolares.getText().toString());
                            etEuros.setText(String.valueOf(Math.rint(Convertir(aEuros) * 100) / 100));
                        } else {

                            double aDolares = Double.parseDouble(etEuros.getText().toString());
                            etDolares.setText(String.valueOf(Math.rint(Convertir(aDolares) * 100) / 100));
                        }
                    } else {
                        Toast.makeText(Ejercicio6Activity.this, "No se puede realizar la conversion porque no se descargó cambio.txt con exito", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e)
                {
                    Toast.makeText(Ejercicio6Activity.this, "Ha ocurrido un error en la lectura de los números", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(Ejercicio6Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }


    private void descargaFichero(String url)
    {


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(/* Context */ this) {
            ProgressDialog pd;

            @Override
            public void onStart() {

                pd = new ProgressDialog(Ejercicio6Activity.this);
                pd.setTitle("Por favor espere...");
                pd.setMessage("AsyncHttpResponseHadler está en progreso");
                pd.setIndeterminate(false);
                pd.setCancelable(false);
                pd.show();
                Toast.makeText(Ejercicio6Activity.this, "Descargando cambio.txt...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(Ejercicio6Activity.this, "Se ha producido un error al descargar el fichero cambio.txt", Toast.LENGTH_SHORT).show();
                txvResultadoDeCambio.setText("El fichero cambio.txt no se ha descargado correctamente");
                ficheroDescargadoConExito = false;
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Toast.makeText(Ejercicio6Activity.this, "El fichero cambio.txt se ha descargado correctamente", Toast.LENGTH_SHORT).show();
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                    BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                    String linea;
                    while ((linea = in.readLine()) != null) {
                        cambio = Double.parseDouble(linea);
                    }
                    in.close();
                    fis.close();
                    ficheroDescargadoConExito = true;
                    txvResultadoDeCambio.setText("El valor del fichero cambio.txt descargado es: " + String.valueOf(cambio));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                pd.dismiss();
            }

        });
    }
}
