package com.montyhacks.jfugue_for_android;

import android.media.midi.MidiDeviceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.jfugue.pattern.Pattern;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;

import java.util.ArrayList;
import java.util.List;

import jp.kshoji.javax.sound.midi.Sequence;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        MediaMidiSystem.OnDeviceConnectionChangedListener {
    Spinner spinner;
    MediaMidiSystem mediaMidiSystem;
    ArrayAdapter<String> spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new ArrayList<String>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mediaMidiSystem = new MediaMidiSystem(this);
        mediaMidiSystem.setOnDeviceConnectionChanged(this);
        mediaMidiSystem.initialize();
        setSpinnerAdapterValues(mediaMidiSystem.getDevices());
    }

    public void onPlay (View v) {
        Log.e("Button", "Pressed");

        // TODO is going to perform a lot of work on the main thread, thus will have to call this on
        // another thread and when that is finished we can start playing on yet another thread
        Sequence s = new MediaMidiPlayer().getSequence(new Pattern(
                new ChordProgression("I IV vi V").eachChordAs("$_i $_i $_i $_i"),
                new Rhythm().addLayer("..XOOOX...X...XO")));

        new MediaMidiPlayer().play(s);

        // Alternatively can just run eveything on a thread, but not nice
//        new Thread(new MediaMidiPlayerNew(new Pattern(
//                new ChordProgression("I IV vi V").eachChordAs("$_i $_i $_i $_i"),
//                new Rhythm().addLayer("..XOOOX...X...XO")))).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaMidiSystem != null){
            mediaMidiSystem.terminate();
            mediaMidiSystem = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e("Spinner selected", "index: " + i);
        if (mediaMidiSystem != null){
            mediaMidiSystem.openDevice(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDevicesChanged(List<MidiDeviceInfo> devices) {
        setSpinnerAdapterValues(devices);
    }

    private void setSpinnerAdapterValues(List<MidiDeviceInfo> devices){
        if (devices == null || devices.isEmpty()){
            return;
        }

        final List<String> list = new ArrayList<>();

        for (MidiDeviceInfo d: devices){
            list.add(d.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT));
            Log.i("deviceInfo", d.toString());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerAdapter.clear();
                spinnerAdapter.addAll(list);

            }
        });
    }
}
