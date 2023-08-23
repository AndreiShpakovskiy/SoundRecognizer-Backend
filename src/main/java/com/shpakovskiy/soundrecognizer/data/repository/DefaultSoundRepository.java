package com.shpakovskiy.soundrecognizer.data.repository;

import com.shpakovskiy.soundrecognizer.data.repository.loaders.FormattedLoader;
import com.shpakovskiy.soundrecognizer.data.repository.loaders.PcmLoader;
import com.shpakovskiy.soundrecognizer.data.repository.loaders.WavLoader;

import java.util.HashMap;
import java.util.Map;

//TODO: Add JavaDoc.
public class DefaultSoundRepository implements SoundRepository {
    private final Map<String, FormattedLoader> formattedLoaders = new HashMap() {{
        put("pcm", new PcmLoader());
        put("wav", new WavLoader());
    }};

    @Override
    public void loadSound(String soundFilePath, SoundRetrievingListener soundRetrievingListener) {
        String fileExtension = soundFilePath.split("\\.")[1]; // For now, assume there is only one dot

        try {
            soundRetrievingListener.onSoundRetrieved(formattedLoaders.get(fileExtension.toLowerCase()).load(soundFilePath));
        } catch (Exception e) {
            e.printStackTrace();
            soundRetrievingListener.onSoundRetrieved(null);
        }
    }
}