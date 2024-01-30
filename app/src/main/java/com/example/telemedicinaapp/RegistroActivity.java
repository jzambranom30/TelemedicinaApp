package com.example.telemedicinaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    TextInputLayout nombre;
    TextInputLayout apellido;
    TextInputLayout edad;
    TextInputLayout cedula;
    TextInputLayout cargo;
    TextInputLayout departamento;
    TextInputLayout email;
    TextInputLayout clave;
    RadioGroup tipousuario;
    TextView mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = findViewById(R.id.txt_nombre);
        apellido = findViewById(R.id.txt_apellido);
        email = findViewById(R.id.txt_usuario);
        clave = findViewById(R.id.txt_hora);
        cedula = findViewById(R.id.txt_cedula);
        edad = findViewById(R.id.txt_edad);
        cargo = findViewById(R.id.txt_cargo);
        departamento = findViewById(R.id.txt_departamento);
        tipousuario = findViewById(R.id.rgTipoUsuario);
        mensaje = findViewById(R.id.txt_TipoUsuario);
    }
    private boolean validarEntradas() {
        String nombrev = nombre.getEditText().getText().toString().trim();
        String apellidov = apellido.getEditText().getText().toString().trim();
        String edadv = edad.getEditText().getText().toString().trim();
        String emailv = email.getEditText().getText().toString().trim();
        String clavev = clave.getEditText().getText().toString().trim();
        String cedulav = cedula.getEditText().getText().toString().trim();
        String cargov = cargo.getEditText().getText().toString().trim();
        String departamentov = departamento.getEditText().getText().toString().trim();
        Integer tipousuariov = obtenerTipoUsuario();

        Boolean res = true;

        if(nombrev.isEmpty()) {
            nombre.setError("El nombre es obligatorio");
            res = false;
        } else {
            nombre.setError(null);
        }

        if(apellidov.isEmpty()) {
            apellido.setError("El apellido es obligatorio");
            res = false;
        } else {
            apellido.setError(null);
        }

        if(edadv.isEmpty()) {
            edad.setError("La edad es obligatoria");
            res = false;
        } else {
            edad.setError(null);
        }

        if (emailv.isEmpty()) {
            email.setError("El email es obligatorio");
            res = false;
        } else if(!validarCorreo(emailv)) {
            email.setError("Correo inválido (solo se admiten con los dominios '@uteq.edu.ec' o '@msuteq.edu.ec')");
            res = false;
        } else {
            email.setError(null);
        }

        if(clavev.isEmpty()) {
            clave.setError("La clave es obligatoria");
            res = false;
        } else {
            clave.setError(null);
        }

        if (cedulav.isEmpty()) {
            cedula.setError("La cédula es obligatoria");
            res = false;
        } else {
            cedula.setError(null);
        }

        if (cargov.isEmpty()) {
            cargo.setError("El cargo/carrera es obligatorio");
            res = false;
        } else {
            cargo.setError(null);
        }

        if (departamentov.isEmpty()) {
            departamento.setError("El departamento/facultad es obligatorio");
            res = false;
        } else {
            departamento.setError(null);
        }

        if (tipousuariov == 0){
            mensaje.setText("El tipo de usuario es obligatorio");
            mensaje.setTextColor(ContextCompat.getColor(this, R.color.rojo));
            res = false;
        } else {
            mensaje.setText("Tipo usuario:");
            mensaje.setTextColor(ContextCompat.getColor(this, R.color.negro));
        }

        return res;
    }
    public void btRegistro(View view) {
        String nombrev = nombre.getEditText().getText().toString().trim();
        String apellidov = apellido.getEditText().getText().toString().trim();
        String edadv = edad.getEditText().getText().toString().trim();
        String emailv = email.getEditText().getText().toString().trim();
        String clavev = clave.getEditText().getText().toString().trim();
        String cedulav = cedula.getEditText().getText().toString().trim();
        String cargov = cargo.getEditText().getText().toString().trim();
        String departamentov = departamento.getEditText().getText().toString().trim();
        Integer tipousuariov = obtenerTipoUsuario();

        if(validarEntradas()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.1.13/registro.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Iterator<String> keys = jsonResponse.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                switch (key) {
                                    case "nombre":
                                        nombre.setError(jsonResponse.getString(key));
                                        break;
                                    case "apellido":
                                        apellido.setError(jsonResponse.getString(key));
                                        break;
                                    case "cedula":
                                        cedula.setError(jsonResponse.getString(key));
                                        break;
                                    case "edad":
                                        edad.setError(jsonResponse.getString(key));
                                        break;
                                    case "email":
                                        email.setError(jsonResponse.getString(key));
                                        break;
                                    case "password":
                                        clave.setError(jsonResponse.getString(key));
                                        break;
                                    case "departamento":
                                        departamento.setError(jsonResponse.getString(key));
                                        break;
                                    case "cargo":
                                        cargo.setError(jsonResponse.getString(key));
                                    case "tipousuario":
                                        mensaje.setText(jsonResponse.getString(key));
                                        mensaje.setTextColor(ContextCompat.getColor(this, R.color.rojo));
                                    case "success":
                                        Toast.makeText(getApplicationContext(), jsonResponse.getString(key), Toast.LENGTH_SHORT).show();

                                        nombre.getEditText().setText("");
                                        apellido.getEditText().setText("");
                                        edad.getEditText().setText("");
                                        cedula.getEditText().setText("");
                                        email.getEditText().setText("");
                                        cargo.getEditText().setText("");
                                        departamento.getEditText().setText("");
                                        clave.getEditText().setText("");
                                        tipousuario.clearCheck();

                                        new Handler().postDelayed(() -> {
                                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }, 5000); // Retraso 5 segundos

                                        break;
                                    case "error":
                                        Toast.makeText(getApplicationContext(), jsonResponse.getString(key), Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error",Toast.LENGTH_SHORT).show();
                                        break;
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
                protected Map<String, String> getParams() {
                    // Parámetros para enviar en la solicitud POST
                    Map<String, String> params = new HashMap<>();
                    params.put("cedula", cedulav);
                    params.put("nombre", nombrev);
                    params.put("apellido", apellidov);
                    params.put("edad", edadv);
                    params.put("departamento", departamentov);
                    params.put("cargo", cargov);
                    params.put("tipousuario", tipousuariov.toString());
                    params.put("email", emailv);
                    params.put("password", clavev);
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
    private int obtenerTipoUsuario() {
        int selectedId = tipousuario.getCheckedRadioButtonId();
        int tipoUsuario = 0; // Valor por defecto si no se selecciona ninguno

        if (selectedId == R.id.rbMedico) {
            tipoUsuario = 1; // Médico
        } else if (selectedId == R.id.rbAdministrativo) {
            tipoUsuario = 2; // Docente/Administrativo
        } else if (selectedId == R.id.rbEstudiante) {
            tipoUsuario = 3; // Estudiante
        }

        return tipoUsuario;
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