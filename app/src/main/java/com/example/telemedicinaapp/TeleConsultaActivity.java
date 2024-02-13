package com.example.telemedicinaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;


public class TeleConsultaActivity extends AppCompatActivity {
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_consulta);

        // Inicializar las opciones por defecto para las conferencias Jitsi Meet.
        URL serverURL;
        try {
            // Si utiliza JaaS, sustituya "https://meet.jit.si" por el serverURL adecuado
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // Si utiliza JaaS, establezca aquí el JWT obtenido
                //.setToken("MiJWT")
                // Se pueden establecer diferentes banderas de características
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip. Enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        registerForBroadcastMessages();

        // Construir objeto de opciones para unirse a la conferencia. El SDK fusionará la opción por defecto
        // que establecimos anteriormente y éste al unirse.
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom("Teleconsulta UTEQ")
                // Configuración de audio y vídeo
                .setAudioMuted(true)
                .setVideoMuted(true)
                .build();
        // Lanzar la nueva actividad con las opciones dadas. El método launch() se encarga
        // de crear el Intent requerido y pasar las opciones.
        JitsiMeetActivity.launch(this, options);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* Esto registra cada posible evento enviado desde JitsiMeetSDK
           Si sólo se necesitan algunos de los eventos, el bucle for se puede sustituir
           por sentencias individuales:
           ej: intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... otros eventos
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Ejemplo para manejar diferentes eventos JitsiMeetSDK
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conferencia Unida con url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participante inscrito%s", event.getData().get("name"));
                    break;
            }
        }
    }

    // Ejemplo para enviar acciones a JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    public void btvolver(View view) {
        Intent intent = new Intent(TeleConsultaActivity.this, UsuarioMainActivity.class);
        startActivity(intent);
    }
}
