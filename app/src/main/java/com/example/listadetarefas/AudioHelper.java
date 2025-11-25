package com.example.listadetarefas;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

public class AudioHelper {

    private final AudioManager audioManager;
    private final Context context;

    public AudioHelper(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean audioOutputAvailable(int type) {

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false;
        }

        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        for (AudioDeviceInfo d : devices) {
            if (d.getType() == type) return true;
        }

        return false;
    }

    public boolean isBuiltInSpeakerAvailable() {
        return audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
    }

    public boolean isBluetoothA2dpAvailable() {
        return audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
    }
}
