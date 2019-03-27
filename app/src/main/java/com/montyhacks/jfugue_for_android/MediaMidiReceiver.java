package com.montyhacks.jfugue_for_android;

import android.media.midi.MidiInputPort;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import jp.kshoji.javax.sound.midi.MidiMessage;
import jp.kshoji.javax.sound.midi.Receiver;

public class MediaMidiReceiver implements Receiver {
    private MidiInputPort inputPort;

    public MediaMidiReceiver(MidiInputPort inputPort) {
        this.inputPort = inputPort;
    }

    @Override
    public void send(@NonNull MidiMessage message, long timeStamp) {
        byte[] bytes = message.getMessage();
        try {
            if (bytes != null) {
                inputPort.send(bytes, 0, bytes.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        Log.e("MediaMidiReceiver", "closed");
        if (inputPort != null){
            try {
                inputPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
