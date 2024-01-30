package com.example.telemedicinaapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.telemedicinaapp.Modelo.Autenticacion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        validarhora();
    }

    public void btregistrarCita(View view) {
        // Formatear la fecha seleccionada
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String fecha = sdf.format(selectedDate.getTime());
        // Obtener la hora seleccionada
        String hora = mostrarHoraSeleccionada();

        TextView mensaje = findViewById(R.id.tw_MsgRegistro);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.13/registrocitas.php";
        String token = Autenticacion.getToken(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
        response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("error")) {
                    mensaje.setText(jsonResponse.getString("error"));
                    mensaje.setTextColor(ContextCompat.getColor(this, R.color.rojo));
                }
                if (jsonResponse.has("success")) {
                    mensaje.setText(jsonResponse.getString("success"));
                    mensaje.setTextColor(ContextCompat.getColor(this, R.color.verde));
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(RegistroCitaActivity.this, UsuarioMainActivity.class);
                        startActivity(intent);
                    }, 5000); // Retraso 5 segundos
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
        error -> {
            if (error.networkResponse != null) {
                Toast.makeText(getApplicationContext(), "Error: " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error de red ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para enviar en la solicitud POST
                Map<String, String> params = new HashMap<>();
                params.put("fecha", fecha);
                params.put("hora", hora);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                // Especificando que el contenido es form-url-encoded
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        queue.add(stringRequest);
        //Toast.makeText(getApplicationContext(), "Fecha seleccionada: " + fecha + " Hora: " + hora, Toast.LENGTH_SHORT).show();
    }

    private String mostrarHoraSeleccionada() {
        int hora = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? timePicker.getHour() : timePicker.getCurrentHour();
        int minuto = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? timePicker.getMinute() : timePicker.getCurrentMinute();

        return String.format(Locale.getDefault(), "%02d:%02d:00", hora, minuto);
    }

    private void validarhora() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(8); // Establecer la hora inicial a las 8 a.m.
            timePicker.setMinute(0); // Establecer los minutos iniciales a 0
        } else {
            timePicker.setCurrentHour(8);
            timePicker.setCurrentMinute(0);
        }

        // Agregar un listener para manejar el cambio de tiempo
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Redondear los minutos al múltiplo de 30 más cercano
                int roundedMinutes;
                if(minute > 0 && minute < 31) {
                    roundedMinutes = 30;
                } else {
                    roundedMinutes = 0;
                }
                timePicker.setMinute(roundedMinutes);

                // Verificar si la hora está fuera de los rangos permitidos
                if ((hourOfDay < 8 || (hourOfDay == 12 && minute > 0) || hourOfDay >= 13 && hourOfDay < 15) ||
                        (hourOfDay >= 18)) {
                    // Establecer la hora en el límite más cercano dentro de los rangos permitidos
                    if (hourOfDay < 8) {
                        hourOfDay = 8;
                    } else if (hourOfDay == 12 && minute > 0) {
                        hourOfDay = 12;
                    } else if (hourOfDay >= 13 && hourOfDay < 15) {
                        hourOfDay = 15;
                    } else if (hourOfDay >= 18) {
                        hourOfDay = 18;
                    }
                    timePicker.setHour(hourOfDay);
                }
            }
        });

    }
}