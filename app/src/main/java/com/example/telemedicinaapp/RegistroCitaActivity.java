package com.example.telemedicinaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistroCitaActivity extends AppCompatActivity {

    TimePicker timePicker;
    CalendarView calendarView;
    // Usar Calendar para mantener la fecha seleccionada
    Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cita);

        calendarView = findViewById(R.id.calendarView2);
        // Establecer la fecha mínima a la fecha actual
        calendarView.setMinDate(System.currentTimeMillis());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Actualizar selectedDate con la nueva fecha seleccionada
            selectedDate.set(year, month, dayOfMonth);
        });

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(8); // Establecer la hora inicial a las 8 a.m.
        } else {
            timePicker.setCurrentHour(8);
        }
    }

    public void btregistrarCita(View view) {
        // Formatear la fecha seleccionada
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fecha = sdf.format(selectedDate.getTime());

        // Obtener la hora seleccionada
        String hora = mostrarHoraSeleccionada();

        // Mostrar la fecha y la hora seleccionadas con un Toast
        Toast.makeText(getApplicationContext(), "Fecha seleccionada: " + fecha + " Hora: " + hora, Toast.LENGTH_SHORT).show();
    }

    private String mostrarHoraSeleccionada() {
        int hora = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? timePicker.getHour() : timePicker.getCurrentHour();
        int minuto = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? timePicker.getMinute() : timePicker.getCurrentMinute();

        return String.format(Locale.getDefault(), "%02d:%02d:00", hora, minuto);
    }
}