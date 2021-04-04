package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText fechaEditText, horaEditText, asuntoEditText;
    private ImageButton fechaImageButton, horaImageButton;
    private Button guardarAsunto;
    private RecyclerView asuntosRecyclerView;

    private ListaAgendaAdapter listaAgendaAdapter;

    private static final String CERO = "0";
    private static final String BARRA = "/";
    private static final String DOS_PUNTOS = ":";
    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Variables para obtener la hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fechaEditText = findViewById(R.id.fechaEditText);
        horaEditText = findViewById(R.id.horaEditText);
        asuntoEditText = findViewById(R.id.asuntoEditText);

        fechaImageButton = findViewById(R.id.fechaImageButton);
        horaImageButton = findViewById(R.id.horaImageButton);

        guardarAsunto = findViewById(R.id.guardarAsuntoButton);

        asuntosRecyclerView = findViewById(R.id.asuntosRecyclerView);
        listaAgendaAdapter = new ListaAgendaAdapter(this);
        asuntosRecyclerView.setAdapter(listaAgendaAdapter);
        asuntosRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        asuntosRecyclerView.setLayoutManager(layoutManager);

        fechaImageButton.setOnClickListener(this);
        horaImageButton.setOnClickListener(this);
        guardarAsunto.setOnClickListener(this);

        mostrarAsuntos();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fechaImageButton:
                obtenerFecha();
                break;
            case R.id.horaImageButton:
                obtenerHora();
                break;
            case R.id.guardarAsuntoButton:
                registrarAsunto();
                mostrarAsuntos();
                break;
        }
    }

    private void registrarAsunto() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "agenda", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        String hora, dia, asunto;
        dia = fechaEditText.getText().toString();
        hora =  horaEditText.getText().toString();
        asunto = asuntoEditText.getText().toString();

        if(!dia.isEmpty() && !hora.isEmpty() && !asunto.isEmpty()) {
            ContentValues registo = new ContentValues();
            registo.put("dia",  dia);
            registo.put("hora", hora);
            registo.put("asunto", asunto);

            //baseDeDatos.execSQL("INSERT INTO agenda(dia, hora, asunto) VALUES(" + dia + "," + hora + "," + asunto + ")");

            baseDeDatos.insert("agenda", null, registo);

            baseDeDatos.close();
            fechaEditText.setText(R.string.formato_fecha);
            horaEditText.setText(R.string.formato_hora);
            asuntoEditText.setText("");
            Toast.makeText(this, "Asunto registrado", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(this, "Asegurese de llenar el formulario", Toast.LENGTH_LONG).show();
        }

    }

    public void mostrarAsuntos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "agenda", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        Cursor fila = baseDeDatos.rawQuery("SELECT * FROM agenda order by id desc", null);
        ArrayList<Asunto> listaAsuntos= new ArrayList<>();
        Asunto asunto;
        if(fila.moveToFirst()) {
            do {
                asunto = new Asunto();
                asunto.setFecha(fila.getString(1));
                asunto.setHora(fila.getString(2));
                asunto.setAsunto(fila.getString(3));
                listaAsuntos.add(asunto);
            }while (fila.moveToNext());
        }
        baseDeDatos.close();
        listaAgendaAdapter.adicionarAsuntos(listaAsuntos);
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fechaEditText.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                horaEditText.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }
}
