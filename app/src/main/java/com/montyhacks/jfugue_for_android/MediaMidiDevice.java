package com.montyhacks.jfugue_for_android;

import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.kshoji.javax.sound.midi.MidiUnavailableException;
import jp.kshoji.javax.sound.midi.Receiver;
import jp.kshoji.javax.sound.midi.Transmitter;

public class MediaMidiDevice implements jp.kshoji.javax.sound.midi.MidiDevice {
    private MidiDevice midiDevice;
    private MediaMidiReceiver midiReceiver;
    private MediaMidiTransmitter midiTransmitter;
    private MediaMidiSystem.PortType portType;

    public MediaMidiDevice(MidiDevice midiDevice, MediaMidiSystem.PortType portType) {
        this.midiDevice = midiDevice;
        this.portType = portType;
    }

    @NonNull
    @Override
    public Info getDeviceInfo() {
        Bundle bundle = midiDevice.getInfo().getProperties();
        return new Info(bundle.getString(MidiDeviceInfo.PROPERTY_NAME, "Unknown"),
                bundle.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER, "Unknown"),
                bundle.getString(MidiDeviceInfo.PROPERTY_PRODUCT, "Unknown"),
                bundle.getString(MidiDeviceInfo.PROPERTY_VERSION, "Unknown"));
    }

    @Override
    public void open() {
        Log.e("MediaMidiDevice", "opened");
        if (portType == MediaMidiSystem.PortType.INPUT) {
            MidiInputPort inputPort = midiDevice.openInputPort(0);
            midiReceiver = new MediaMidiReceiver(inputPort);
        } else {
            MidiOutputPort outputPort = midiDevice.openOutputPort(0);
            // TODO how would we handle this?
        }
        midiTransmitter = new MediaMidiTransmitter(midiReceiver);
    }

    @Override
    public void close() {
        Log.e("MediaMidiDevice", "closed");
        if (midiDevice != null){
            try {
                midiDevice.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (midiTransmitter != null){
            midiTransmitter.close();
        }
    }

    @Override
    public boolean isOpen() {
        return midiReceiver != null;
    }

    @Override
    public long getMicrosecondPosition() {
        return -1;
    }

    @Override
    public int getMaxReceivers() {
        return midiReceiver != null ? 1 : 0;
    }

    @Override
    public int getMaxTransmitters() {
        return midiTransmitter != null ? 1 : 0;
    }

    @NonNull
    @Override
    public Receiver getReceiver() {
        return midiReceiver;
    }

    @NonNull
    @Override
    public List<Receiver> getReceivers() {
        return new ArrayList<Receiver>(Collections.singletonList(midiReceiver));
    }

    @NonNull
    @Override
    public Transmitter getTransmitter() {
        return midiTransmitter;
    }

    @NonNull
    @Override
    public List<Transmitter> getTransmitters() {
        return new ArrayList<Transmitter>(Collections.singletonList(midiTransmitter));
    }
}
