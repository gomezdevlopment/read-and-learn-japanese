package com.readandlearn.japanese.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "words")
public class Word {

    @NonNull
    @PrimaryKey
    private String word;

    private String definition;

    private String reading;

    private String status;

    private int studyInterval;

    private int dueDate;

    public Word(@NonNull String word, String reading, String definition, String status){
        this.word = word;
        this.definition = definition;
        this.status = status;
        this.reading = reading;
        this.studyInterval = 1;
        this.dueDate = 1;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public String getStatus() {
        return status;
    }

    public String getReading() {
        return reading;
    }

    public int getStudyInterval() {
        return studyInterval;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setStudyInterval(int studyInterval) {
        this.studyInterval = studyInterval;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }
}
