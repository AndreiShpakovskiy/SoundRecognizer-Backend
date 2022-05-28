package com.shpakovskiy.soundrecognizer.recognizer.model;

import javax.sound.sampled.AudioFormat;

public class Sound {
    private final double[] rawValues;
    private final AudioFormat audioFormat;

    public Sound(double[] rawValues, AudioFormat audioFormat) {
        this.rawValues = rawValues;
        this.audioFormat = audioFormat;
    }

    public double[] getRawValues() {
        return rawValues;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
}
