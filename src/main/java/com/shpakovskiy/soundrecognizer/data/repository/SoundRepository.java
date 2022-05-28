package com.shpakovskiy.soundrecognizer.data.repository;

public interface SoundRepository {

    // Sound loadFromFile(String filePath) throws IOException, UnsupportedAudioFileException;

    void loadSound(String soundFilePath, SoundRetrievingListener soundRetrievingListener);

    // void recordSound(SoundRetrievingListener soundRetrievingListener);

    // void stopRecording();
}
