package com.example.listadetarefas;

import android.widget.ListView;
import android.content.Intent;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private AudioManager audioManager;
    private AudioHelper audioHelper;
    private ArrayList<String> audioList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    private final AudioDeviceCallback audioCallback = new AudioDeviceCallback() {
        @Override
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
            runOnUiThread(() -> {
                loadAudioOutputs();
                addStatus("Dispositivo adicionado");
            });
        }

        @Override
        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            runOnUiThread(() -> {
                loadAudioOutputs();
                addStatus("Dispositivo removido");
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioHelper = new AudioHelper(this);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, audioList);
        listView.setAdapter(adapter);

        // Botão para Bluetooth
        Button btnConfigBt = findViewById(R.id.btnConfigBt);
        btnConfigBt.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Carrega a lista inicial
        loadAudioOutputs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioManager.registerAudioDeviceCallback(audioCallback, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioManager.unregisterAudioDeviceCallback(audioCallback);
    }

    /**
     * Lista todas as saídas de áudio no ListView
     */
    private void loadAudioOutputs() {
        audioList.clear();

        AudioDeviceInfo[] outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        for (AudioDeviceInfo device : outputs) {
            audioList.add(describeDevice(device));
        }

        // Mostrar status geral
        audioList.add("Alto-falante disponível: " +
                audioHelper.isBuiltInSpeakerAvailable());

        audioList.add("Bluetooth A2DP conectado: " +
                audioHelper.isBluetoothA2dpAvailable());

        adapter.notifyDataSetChanged();
    }

    //Mensagem de status
    private void addStatus(String message) {
        audioList.add(0, "STATUS: " + message);
        adapter.notifyDataSetChanged();
    }

    //Conexão dos dispositivos
    private String describeDevice(AudioDeviceInfo device) {
        switch (device.getType()) {
            case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                return "Alto-falante interno";
            case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                return "Fones com fio";
            case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                return "Bluetooth (A2DP)";
            case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                return "Bluetooth (SCO)";
            default:
                return "Outro dispositivo: " + device.getType();
        }
    }
}