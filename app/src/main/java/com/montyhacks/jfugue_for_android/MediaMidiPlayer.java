package com.montyhacks.jfugue_for_android;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import jp.kshoji.javax.sound.midi.Sequence;

public class MediaMidiPlayer extends Player {
    @Override
    public void play(final Sequence sequence) {
        runOnAnotherThread(new Runnable(){
            @Override
            public void run() {
                MediaMidiPlayer.super.play(sequence);
            }
        });
    }

    private void runOnAnotherThread(Runnable runnable){
        new Thread(runnable).start();
    }

    // TODO Work In Progress, not sure if this is better than the approach above
    public class MediaMidiPlayerNew extends Player implements Runnable {
        private Pattern pattern;

        public MediaMidiPlayerNew(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public void run() {
            play(pattern);
        }
    }
}



