package com.example.telemedicinaapp.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.telemedicinaapp.Modelo.Cita;
import com.example.telemedicinaapp.R;

import java.util.ArrayList;

public class AdaptadorCita extends ArrayAdapter<Cita> {
    private ArrayList<Cita> datos;
    private Context context;

    // Constructor
    public AdaptadorCita(Context context, ArrayList<Cita> datos) {
        super(context, R.layout.ly_items_citas, datos);
        this.context = context;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        // Devuelve al menos 1 para mostrar el mensaje de lista vacía
        return datos.isEmpty() ? 1 : datos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (datos.isEmpty()) {
            // Inflar una vista alternativa que contenga solo un mensaje de "lista vacía"
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText("Aún no tiene citas agendadas");
        } else {
            // Inflar la vista normal para mostrar los datos de la cita
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ly_items_citas, parent, false);
            TextView lblFecha = (TextView)convertView.findViewById(R.id.lblFecha);
            lblFecha.setText("Fecha: " + getItem(position).getFecha());
            TextView lblHora = (TextView)convertView.findViewById(R.id.lblHora);
            lblHora.setText("Hora: " + getItem(position).getHora());
        }
        return convertView;
    }
}
