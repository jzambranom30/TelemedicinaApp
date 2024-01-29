package com.example.telemedicinaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.telemedicinaapp.Adaptador.AdaptadorCita;
import com.example.telemedicinaapp.Modelo.Cita;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UsuarioMainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private RequestQueue queue;
    private ListView lstOp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_main);

        TextView respuesta = findViewById(R.id.txt_Mensaje);
        Bundle bundle = getIntent().getExtras();

        //VISTA
        lstOp = (ListView) findViewById(R.id.lstCita);
        View header = getLayoutInflater().inflate(R.layout.ly_titulo_cita_usuario, null);
        lstOp.addHeaderView(header);

        queue = Volley.newRequestQueue(this); // Inicializa la RequestQueue
        String url = "http://192.168.1.13/datoscita.php";

        if (bundle != null) {
            String token = bundle.getString("Token");
            if (token != null) {
                datousuario(url, token, respuesta);
            }
        }
    }

    private void datousuario(String url, String token, TextView respuesta) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject objPersona = new JSONObject(response);
                        JSONArray datosPersona = objPersona.getJSONArray("persona");
                        String user = "";
                        for(int i=0; i< datosPersona.length();i++){
                            JSONObject usuario = datosPersona.getJSONObject(i);
                            user = usuario.getString("nombre") + " " + usuario.getString("apellido");
                        }

                        respuesta.setText("Bienvenido, " + user);

                        ArrayList<Cita> lstCita = new ArrayList<>();
                        Object citasObject = objPersona.get("citas"); // Obtén el objeto "citas" como un Object

                        // Verifica si "citas" es un JSONArray
                        if (citasObject instanceof JSONArray) {
                            JSONArray datosCita = (JSONArray) citasObject;
                            lstCita = Cita.JsonObjectsBuild(datosCita);
                        }
                        // Continúa con la creación del adaptador y la asignación al ListView como antes
                        AdaptadorCita adaptadorCita = new AdaptadorCita(this, lstCita);
                        lstOp.setAdapter(adaptadorCita);



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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            logout(false);
            finishAffinity(); // Cierra todas las actividades y finaliza la aplicación
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Presione nuevamente para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void logout( Boolean res) {
        String url = "http://192.168.1.13/logout.php";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String token = bundle.getString("Token");
            if (token != null) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                if (jsonResponse.has("error")) {
                                    Toast.makeText(getApplicationContext(), jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonResponse.has("success")) {
                                    if(res){
                                        Intent intent = new Intent(UsuarioMainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), jsonResponse.getString("success"), Toast.LENGTH_SHORT).show();
                                    }
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
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };

                queue.add(stringRequest);
            }
        }
    }

    public void btSalir(View view) {
        logout(true);
    }

    public void btRegistrarCita(View view) {
        Intent intent = new Intent(UsuarioMainActivity.this, RegistroCitaActivity.class);
        startActivity(intent);
    }

}