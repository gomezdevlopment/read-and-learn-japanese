package com.readandlearn.japanese;

public class DictionaryEntry {
    private String kanji;
    private String reading;
    private String definitions;

    public DictionaryEntry(String kanji, String reading, String definitions){
        this.kanji = kanji;
        this.reading = reading;
        this.definitions = definitions;
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }

}
