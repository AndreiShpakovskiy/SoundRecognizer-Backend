package com.shpakovskiy.soundrecognizer.data.repository.loaders;

import com.shpakovskiy.soundrecognizer.recognizer.model.Sound;
import com.shpakovskiy.soundrecognizer.recognizer.utils.RawDataFormatter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class WavLoader implements FormattedLoader {

    @Override
    public Sound load(String sourcePath) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(sourcePath))) {

            AudioFormat soundProperties = audioInputStream.getFormat();
            // byte[] rawAudioBytes = audioInputStream.readAllBytes();

//            return new Sound(
//                    RawDataFormatter.retrieveSoundValues(rawAudioBytes, soundProperties.getSampleSizeInBits() / Byte.SIZE),
//                    soundProperties
//            );

            return null;
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Error while loading WAV file");
            e.printStackTrace();
            return null;
        }
    }
}