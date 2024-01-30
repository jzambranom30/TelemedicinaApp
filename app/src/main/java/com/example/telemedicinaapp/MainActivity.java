package com.example.telemedicinaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.telemedicinaapp.Modelo.Autenticacion;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputLayout username;
    TextInputLayout password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.txt_usuario);
        password = findViewById(R.id.txt_hora);
    }

    private boolean validarEntradas(){
        String usuario = username.getEditText().getText().toString().trim();
        String clave = password.getEditText().getText().toString().trim();
        boolean res = true;

        if (usuario.isEmpty()) {
            username.setError("El usuario es obligatorio");
            res = false;
        } else if(validarCorreo(usuario)) {
            username.setError(null);
        } else {
            username.setError("Usuario inválido (Solo se admiten con los dominios '@uteq.edu.ec' o '@msuteq.edu.ec')");
            res = false;
        }

        if (clave.isEmpty()) {
            password.setError("La contraseña es obligatoria");
            res = false;
        } else {
            password.setError(null);
        }

        return res;
    }
    public void btLogin(View view) {
        String usuario = username.getEditText().getText().toString().trim();
        String clave = password.getEditText().getText().toString().trim();

        if(validarEntradas()){
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://apptelemedicina.000webhostapp.com/login.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.has("username")) {
                                username.setError(jsonResponse.getString("username"));
                            }
                            if (jsonResponse.has("password")) {
                                password.setError(jsonResponse.getString("password"));
                                password.getEditText().setText("");
                            }
                            if (jsonResponse.has("token")) {

                                username.getEditText().setText("");
                                password.getEditText().setText("");

                                String token = jsonResponse.getString("token");
                                Autenticacion.saveToken(getApplicationContext(), token);
                                Intent intent= new Intent(MainActivity.this, UsuarioMainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Iniciando Sesión .......!", Toast.LENGTH_SHORT).show();
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
                    params.put("username", usuario);
                    params.put("password", clave);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    // Especificando que el contenido es form-url-encoded
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            queue.add(stringRequest);
        }
    }

    public void btRegistro(View view){
        Intent intent= new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
    }

    private boolean validarCorreo(String correo) {
        // Expresión regular para validar el formato del correo electrónico
        String patronCorreo = "^[a-zA-Z0-9._-]+@(uteq\\.edu\\.ec|msuteq\\.edu\\.ec)$";

        // Comprobamos si el correo coincide con el patrón
        if (correo.matches(patronCorreo)) {
            return true;
        } else {
            return false;
        }
    }
}