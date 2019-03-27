package com.montyhacks.jfugue_for_android;

import android.support.annotation.Nullable;
import android.util.Log;

import jp.kshoji.javax.sound.midi.Receiver;
import jp.kshoji.javax.sound.midi.Transmitter;

public class MediaMidiTransmitter implements Transmitter {
    private Receiver midiReceiver;

    public MediaMidiTransmitter(Receiver midiReceiver) {
        this.midiReceiver = midiReceiver;
    }


    @Override
    public void setReceiver(@Nullable Receiver receiver) {
        midiReceiver = receiver;
    }

    @Nullable
    @Override
    public Receiver getReceiver() {
        return midiReceiver;
    }

    @Override
    public void close() {
        Log.e("MediaMidiTransmitter", "closed");
        midiReceiver.close();
    }
}
