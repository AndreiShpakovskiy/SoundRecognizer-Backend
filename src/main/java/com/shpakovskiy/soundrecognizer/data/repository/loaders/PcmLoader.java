package com.shpakovskiy.soundrecognizer.data.repository.loaders;

import com.shpakovskiy.soundrecognizer.recognizer.SoundSourceRecognizer;
import com.shpakovskiy.soundrecognizer.recognizer.model.Sound;
import com.shpakovskiy.soundrecognizer.recognizer.utils.RawDataFormatter;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PcmLoader implements FormattedLoader {

    @Override
    public Sound load(String sourcePath) throws UnsupportedAudioFileException {
        try {
            byte[] rawAudioData = Files.readAllBytes(Paths.get(sourcePath));

            System.out.println("Raw length: " + rawAudioData.length);

            AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);
            AudioInputStream audioFileRawInputStream = new AudioInputStream(new ByteArrayInputStream(rawAudioData), audioFormat, rawAudioData.length);

            AudioFormat baseFormat = audioFileRawInputStream.getFormat();

            AudioInputStream cleanAudioInputStream = AudioSystem.getAudioInputStream(
                    baseFormat,
                    audioFileRawInputStream
            );

            //TODO: Pretty old library, doesn't support all formats.
            // Consider finding a replacement or use custom implementation, as was done above.
            PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
            if (!conversionProvider.isConversionSupported(SoundSourceRecognizer.PREFERRED_PROCESSING_FORMAT, baseFormat)) {
                System.err.println("Audio format conversion is not supported.");
                return null;
            }

            final AudioInputStream outDinSound = conversionProvider.getAudioInputStream(
                    SoundSourceRecognizer.PREFERRED_PROCESSING_FORMAT,
                    cleanAudioInputStream
            );

            //new Thread(() -> {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int count;
            do {
                count = outDinSound.read(buffer, 0, 1024);

                if (count > 0) {
                    outputStream.write(buffer, 0, count);
                }
            } while (count > 0);

            byte[] allBytes = outputStream.toByteArray();

            System.out.println("All bytes length: " + allBytes.length);

            return new Sound(
                    RawDataFormatter.justToDouble(allBytes),
                    //RawDataFormatter.retrieveSoundValues(outDinSound.readAllBytes(), outDinSound.getFormat().getSampleSizeInBits() / Byte.SIZE),
                    outDinSound.getFormat()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}