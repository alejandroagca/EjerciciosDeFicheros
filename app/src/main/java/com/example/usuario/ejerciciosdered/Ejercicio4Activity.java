package com.example.usuario.ejerciciosdered;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class Ejercicio4Activity extends AppCompatActivity implements View.OnClickListener {

    EditText edtDireccion, edtRutaDescarga;
    RadioButton rdbJava, rdbAAHC, rdbVolley;
    Button btnConectar, btnDescargar;
    WebView wbvMostrar;
    RequestQueue mRequestQueue;
    ProgressDialog progreso;
    Memoria miMemoria;
    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio4);
        edtDireccion = (EditText) findViewById(R.id.edtURL);
        edtRutaDescarga = findViewById(R.id.edtRutaDescarga);
        rdbJava = (RadioButton) findViewById(R.id.rdbJava);
        rdbAAHC = (RadioButton) findViewById(R.id.rdbAAHC);
        rdbVolley = (RadioButton) findViewById(R.id.rdbVolley);
        btnConectar = (Button) findViewById(R.id.btnConectar);
        btnConectar.setOnClickListener(this);
        btnDescargar = findViewById(R.id.btnDescargar);
        btnDescargar.setOnClickListener(this);
        wbvMostrar = (WebView) findViewById(R.id.wbvMostrar);
        progreso = new ProgressDialog(Ejercicio4Activity.this);
        mRequestQueue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        miMemoria = new Memoria(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        String url = edtDireccion.getText().toString();
        if (isNetworkAvailable()) {
            if (v == btnConectar) {
                if (rdbJava.isChecked()) {
                    TareaAsincrona tareaAsincrona = new TareaAsincrona();
                    tareaAsincrona.execute(url);
                } else if (rdbAAHC.isChecked()) {
                    AAHC(url);
                } else {
                    volley(url);
                }
            }


            if (v == btnDescargar) {
                if(edtRutaDescarga.getText().toString().length() > 0) {
                    if (miMemoria.disponibleEscritura()) {
                        if (rdbJava.isChecked()) {
                            TareaAsincronaDescarga tareaAsincronaDescarga = new TareaAsincronaDescarga();
                            tareaAsincronaDescarga.execute(url);
                        } else if (rdbAAHC.isChecked()) {
                            AAHCDescarga(url);
                        } else {
                            volleyDescarga(url);
                        }
                    } else {
                        Toast.makeText(this, "No se puede escribir en la memoria externa", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Introduce un nombre para el fichero", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
        }

    }




    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class TareaAsincrona extends AsyncTask<String, Void, Resultado> {

        @Override
        protected Resultado doInBackground(String... strings) {
            return  Conexion.conectarJava(strings[0]);
        }

        protected void onPreExecute() {
            progreso = new ProgressDialog(Ejercicio4Activity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if(rdbJava.isChecked()) {
                progreso.setMessage("Conectando con JAVA. . .");
            }
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Resultado resultado) {
            super.onPostExecute(resultado);
            if (resultado.getCodigo())
                wbvMostrar.loadDataWithBaseURL(null, resultado.getContenido(),"text/html", "UTF-8", null);
            else
                wbvMostrar.loadDataWithBaseURL(null, resultado.getMensaje(),"text/html", "UTF-8", null);
            progreso.dismiss();
        }
    }

    private class TareaAsincronaDescarga extends AsyncTask<String, Void, Resultado> {

        @Override
        protected Resultado doInBackground(String... strings) {
            return  Conexion.conectarJava(strings[0]);
        }

        protected void onPreExecute() {
            progreso = new ProgressDialog(Ejercicio4Activity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if(rdbJava.isChecked()) {
                progreso.setMessage("Conectando con JAVA. . .");
            }
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Resultado resultado) {
            super.onPostExecute(resultado);
            if (resultado.getCodigo()){
                miMemoria.escribirExterna(edtRutaDescarga.getText().toString(), resultado.getContenido(), false,"UTF-8");
            Toast.makeText(Ejercicio4Activity.this, "Fichero descargado", Toast.LENGTH_SHORT).show();
            }

            else{
                Toast.makeText(Ejercicio4Activity.this, "Error: " + resultado.getMensaje(), Toast.LENGTH_SHORT).show();
            }
            progreso.dismiss();
        }
    }


    private void AAHC(String texto) {
        progreso = new ProgressDialog(Ejercicio4Activity.this);
        RestClient.get(texto, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progreso.show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
                wbvMostrar.loadDataWithBaseURL(null, responseString.toString(), "text/html", "UTF-8", null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progreso.dismiss();
                wbvMostrar.loadDataWithBaseURL(null, responseString.toString(), "text/html", "UTF-8", null);
            }
        });
    }

    private void AAHCDescarga(String texto) {
        progreso = new ProgressDialog(Ejercicio4Activity.this);
        RestClient.get(texto, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Descargando . . .");
                progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progreso.show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
                Toast.makeText(Ejercicio4Activity.this, "Error:: " + statusCode, Toast.LENGTH_SHORT).show();            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progreso.dismiss();
                miMemoria.escribirExterna(edtRutaDescarga.getText().toString(), responseString, false,"UTF-8");
                Toast.makeText(Ejercicio4Activity.this, "Fichero descargado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void volley(String url) {
        progreso = new ProgressDialog(Ejercicio4Activity.this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Conectando...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mRequestQueue.cancelAll(TAG);
            }
        });
        progreso.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.dismiss();
                        wbvMostrar.loadDataWithBaseURL(null, response.toString(), "text/html", "UTF-8", null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String mensaje = "Error";
                        progreso.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError)
                            mensaje = "Timeout Error: " + error.getMessage();
                        else {
                            NetworkResponse errorResponse = error.networkResponse;
                            if (errorResponse != null && errorResponse.data != null)
                                try {
                                    mensaje = "Error: " + errorResponse.statusCode + " " + "\n" + new
                                            String(errorResponse.data, "UTF-8");
                                    Log.e("Error", mensaje);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    mensaje = "Error sin información";
                                }
                        }
                        wbvMostrar.loadDataWithBaseURL(null, mensaje, "text/html", "UTF-8", null);
                    }


                });

        stringRequest.setTag(TAG);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1));

        mRequestQueue.add(stringRequest);
    }

    private void volleyDescarga(String url) {
        progreso = new ProgressDialog(Ejercicio4Activity.this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Conectando...");
        progreso.setCancelable(true);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mRequestQueue.cancelAll(TAG);
            }
        });
        progreso.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.dismiss();
                        miMemoria.escribirExterna(edtRutaDescarga.getText().toString(), response, false,"UTF-8");
                        Toast.makeText(Ejercicio4Activity.this, "Fichero descargado", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String mensaje = "Error";
                        progreso.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError)
                            mensaje = "Timeout Error: " + error.getMessage();
                        else {
                            NetworkResponse errorResponse = error.networkResponse;
                            if (errorResponse != null && errorResponse.data != null)
                                try {
                                    mensaje = "Error: " + errorResponse.statusCode + " " + "\n" + new
                                            String(errorResponse.data, "UTF-8");
                                    Log.e("Error", mensaje);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    mensaje = "Error sin información";
                                }
                        }
                        Toast.makeText(Ejercicio4Activity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }


                });

        stringRequest.setTag(TAG);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1));

        mRequestQueue.add(stringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

}
