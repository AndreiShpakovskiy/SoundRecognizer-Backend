package com.shpakovskiy.soundrecognizer.recognizer.model;

public class MatchFrame {
    private final int windowId;
    private final int songId;

    public MatchFrame(int songId, int windowId) {
        this.songId = songId;
        this.windowId = windowId;
    }

    public int getWindowId() {
        return windowId;
    }

    public int getSongId() {
        return songId;
    }
}