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

    private String status;

    public Word(@NonNull String word, String definition, String status){
        this.word = word;
        this.definition = definition;
        this.status = status;
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
}
