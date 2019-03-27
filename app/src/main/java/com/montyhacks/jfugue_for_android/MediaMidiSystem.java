package com.montyhacks.jfugue_for_android;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.kshoji.javax.sound.midi.MidiSystem;

public class MediaMidiSystem implements MidiManager.OnDeviceOpenedListener{
    private final Context context;
    private MidiManager midiManager;
    private MidiManager.DeviceCallback deviceCallback;
    private final List<MidiDeviceInfo> deviceInfos = new ArrayList<>();
    private OnDeviceConnectionChangedListener onDeviceConnectionChangedListener;
    private MediaMidiDevice mediaMidiDevice;

    enum PortType{
        INPUT, OUTPUT
    }
    /**
     * Constructor
     *
     * @param context the context
     */
    public MediaMidiSystem(@NonNull final Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Initializes {@link MidiSystem}
     */
    public void initialize() {
        midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        if (midiManager == null) {
            throw new NullPointerException("MidiManager is null");
        }

        deviceInfos.addAll(Arrays.asList(midiManager.getDevices()));

        deviceCallback = createDeviceAddedCallback();
        midiManager.registerDeviceCallback(deviceCallback, null);
    }

    public void setOnDeviceConnectionChanged(OnDeviceConnectionChangedListener listener){
        onDeviceConnectionChangedListener = listener;
    }

    private MidiManager.DeviceCallback createDeviceAddedCallback() {
        return new MidiManager.DeviceCallback() {
            @Override
            public void onDeviceRemoved(MidiDeviceInfo device) {
                synchronized (deviceInfos){
                    if (deviceInfos.contains(device)) {
                        deviceInfos.remove(device);
                    }
                }

                if (onDeviceConnectionChangedListener != null) {
                    onDeviceConnectionChangedListener.onDevicesChanged(deviceInfos);
                }
            }

            @Override
            public void onDeviceAdded(MidiDeviceInfo device) {
                synchronized (deviceInfos){
                    if (!deviceInfos.contains(device)){
                        deviceInfos.add(device);
                    }
                }

                if (onDeviceConnectionChangedListener != null) {
                    onDeviceConnectionChangedListener.onDevicesChanged(deviceInfos);
                }
            }
        };
    }

    /**
     * Terminates {@link MidiSystem}
     */
    public void terminate() {
        deviceInfos.clear();

        midiManager.unregisterDeviceCallback(deviceCallback);

        if (mediaMidiDevice != null){
            mediaMidiDevice.close();
            mediaMidiDevice = null;
        }

        midiManager = null;
    }

    public void openDevice(int index){
        midiManager.openDevice(deviceInfos.get(index), this, new Handler(Looper.myLooper()));
    }

    public List<MidiDeviceInfo> getDevices(){
        return deviceInfos;
    }

    @Override
    public void onDeviceOpened(MidiDevice midiDevice) {
        if (midiDevice == null) {
            Log.e("Failed", "could not open device");
        } else {
            Log.e("Success", "Connected to" + midiDevice.toString());
            MidiSystem.removeMidiDevice(mediaMidiDevice);

            if (mediaMidiDevice != null) {
                mediaMidiDevice.close();
            }

            mediaMidiDevice = new MediaMidiDevice(midiDevice, PortType.INPUT);
            mediaMidiDevice.open();

            MidiSystem.addMidiDevice(mediaMidiDevice);

        }
    }

    public interface OnDeviceConnectionChangedListener{
        void onDevicesChanged(List<MidiDeviceInfo> devices);
    }
}

