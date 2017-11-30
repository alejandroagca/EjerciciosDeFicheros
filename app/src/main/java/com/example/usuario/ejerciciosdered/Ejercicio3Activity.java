package com.example.usuario.ejerciciosdered;

import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_MULTIPLE;

public class Ejercicio3Activity extends AppCompatActivity implements View.OnClickListener{

    MaterialCalendarView mcvCalendario;
    ArrayList<Calendar> diasSeleccionados;
    Button btnCalcularDiasLectivos;
    TextView txvEsLectivo;
    ArrayList<Calendar> diasComprendidos;
    ArrayList<Date> diasFestivos;
    Memoria miMemoria;
    File miFichero;
    SimpleDateFormat formato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio3);
        miMemoria = new Memoria(getApplicationContext());
        diasFestivos = new ArrayList<Date>();
        diasSeleccionados = new ArrayList<Calendar>();
        diasComprendidos = new ArrayList<Calendar>();

        formato = new SimpleDateFormat("dd-MM-yyyy");

        anadirDiasFestivos();

        btnCalcularDiasLectivos = findViewById(R.id.btnCalcularDiasLectivos);
        btnCalcularDiasLectivos.setOnClickListener(this);

        txvEsLectivo = findViewById(R.id.txvEsLectivo);

        mcvCalendario = (MaterialCalendarView) findViewById(R.id.mcvCalendario);
        mcvCalendario.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(2017, 8, 15))
                .setMaximumDate(CalendarDay.from(2018, 5, 25))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        mcvCalendario.setSelectionMode(SELECTION_MODE_MULTIPLE);
        mcvCalendario.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (diasSeleccionados.size() > 1){
                    Toast.makeText(Ejercicio3Activity.this, "Solo puedes seleccionar dos dias. Empieza de nuevo", Toast.LENGTH_SHORT).show();
                    widget.clearSelection();
                    diasSeleccionados.clear();
                }
                else{
                    diasSeleccionados.add(date.getCalendar());
                }
            }

        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnCalcularDiasLectivos) {
            if (diasSeleccionados.size() == 2) {
                if (miMemoria.disponibleEscritura()) {
                    miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "diasLectivos.txt");
                    miFichero.delete();
                    if (diasSeleccionados.get(0).before(diasSeleccionados.get(1))) {
                        calcularDiasComprendidos(diasSeleccionados.get(0), diasSeleccionados.get(1));
                    } else {
                        calcularDiasComprendidos(diasSeleccionados.get(1), diasSeleccionados.get(0));

                    }
                    Toast.makeText(Ejercicio3Activity.this, "Los dias lectivos se han guardado con exito", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Ejercicio3Activity.this, "La escritura en la tarjeta externa no esta disponible", Toast.LENGTH_SHORT).show();
                }
            }

            else{
                Toast.makeText(Ejercicio3Activity.this, "Tienes que seleccionar dos dias", Toast.LENGTH_SHORT).show();
            }
        }
        diasSeleccionados.clear();
        mcvCalendario.clearSelection();
    }

    public void calcularDiasComprendidos(Calendar diaInicio, Calendar diaFin){

        while (diaInicio.compareTo(diaFin) <= 0){
            diasComprendidos.add(diaInicio);
            diaInicio.add(Calendar.DAY_OF_YEAR,1);
        }

        esLectivo();
    }

    public void esLectivo() {
        boolean festivo;
        Calendar diaFestivo = Calendar.getInstance();

        for (int i = 0; i < diasComprendidos.size(); i++) {
            festivo = false;
            if ((diasComprendidos.get(i).get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) || (diasComprendidos.get(i).get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)){
                festivo = true;
            }
            else{
                for (int j = 0; j < diasFestivos.size(); j++){
                    diaFestivo.setTime(diasFestivos.get(j));
                    if(diasComprendidos.get(i).get(Calendar.DAY_OF_YEAR) == diaFestivo.get(Calendar.DAY_OF_YEAR) ){
                        festivo = true;
                    }
                }
            }
            if (festivo == false){
                guardarEnExterna(diasComprendidos.get(i).getTime());
                if (i == 0){
                    txvEsLectivo.setText("El día " + formato.format(diasComprendidos.get(i)) + " es lectivo");
                }
            }
            else {
                if (i == 0){
                    txvEsLectivo.setText("El día " + formato.format(diasComprendidos.get(i)) + " no es lectivo");
                }
            }
        }
        diasComprendidos.clear();
    }

    public void guardarEnExterna(Date fecha){
        String laFecha = formato.format(fecha);
            if(miMemoria.disponibleEscritura()) {
                        miMemoria.escribirExterna("diasLectivos.txt",laFecha , true, "UTF-8");
            }
            else{
                Toast.makeText(Ejercicio3Activity.this, "La escritura no está disponible", Toast.LENGTH_SHORT).show();
            }
    }



    public void anadirDiasFestivos(){

        try{
            diasFestivos.add(formato.parse("12-10-2017"));
            diasFestivos.add(formato.parse("01-11-2017"));
            diasFestivos.add(formato.parse("06-12-2017"));
            diasFestivos.add(formato.parse("08-12-2017"));
            diasFestivos.add(formato.parse("25-12-2017"));
            diasFestivos.add(formato.parse("26-12-2017"));
            diasFestivos.add(formato.parse("27-12-2017"));
            diasFestivos.add(formato.parse("28-12-2017"));
            diasFestivos.add(formato.parse("29-12-2017"));
            diasFestivos.add(formato.parse("01-01-2018"));
            diasFestivos.add(formato.parse("02-01-2018"));
            diasFestivos.add(formato.parse("03-01-2018"));
            diasFestivos.add(formato.parse("04-01-2018"));
            diasFestivos.add(formato.parse("05-01-2018"));
            diasFestivos.add(formato.parse("26-02-2018"));
            diasFestivos.add(formato.parse("27-02-2018"));
            diasFestivos.add(formato.parse("28-02-2018"));
            diasFestivos.add(formato.parse("01-03-2018"));
            diasFestivos.add(formato.parse("02-03-2018"));
            diasFestivos.add(formato.parse("26-03-2018"));
            diasFestivos.add(formato.parse("27-03-2018"));
            diasFestivos.add(formato.parse("28-03-2018"));
            diasFestivos.add(formato.parse("29-03-2018"));
            diasFestivos.add(formato.parse("30-03-2018"));
            diasFestivos.add(formato.parse("01-05-2018"));



        }catch (Exception e){

        }
    }

}
