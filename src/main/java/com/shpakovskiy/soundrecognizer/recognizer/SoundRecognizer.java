package com.shpakovskiy.soundrecognizer.recognizer;

import com.shpakovskiy.soundrecognizer.recognizer.model.Sound;

import java.util.Map;

public interface SoundRecognizer {

    void addKnownSound(Sound sound, String name);

    //FIXME: It shouldn't be map in the future
    Map<String, Double> getBestMatch(Sound sound);
}
