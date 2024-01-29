package com.example.telemedicinaapp.Modelo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cita {
    private final String hora;
    private final String fecha;
    public Cita(JSONObject a) throws JSONException {
        fecha = a.getString("fecha");
        hora = a.getString("hora");
    }
    public static ArrayList<Cita> JsonObjectsBuild(JSONArray datos) throws JSONException {
        ArrayList<Cita> usuarios = new ArrayList<>();
        for (int i = 0; i < datos.length() && i<20; i++) {
            usuarios.add(new Cita(datos.getJSONObject(i)));
        }
        return usuarios;
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
    }

}
