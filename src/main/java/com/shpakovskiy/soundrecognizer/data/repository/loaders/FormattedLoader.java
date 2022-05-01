package com.shpakovskiy.soundrecognizer.data.repository.loaders;

import com.shpakovskiy.soundrecognizer.recognizer.model.Sound;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface FormattedLoader {

    Sound load(String sourcePath) throws UnsupportedAudioFileException;
}