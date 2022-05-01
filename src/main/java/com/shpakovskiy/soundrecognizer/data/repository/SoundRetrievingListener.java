package com.shpakovskiy.soundrecognizer.data.repository;

import com.shpakovskiy.soundrecognizer.recognizer.model.Sound;

@FunctionalInterface
public interface SoundRetrievingListener {

    void onSoundRetrieved(Sound sound);
}