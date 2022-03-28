package com.readandlearn.japanese.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    @Query("SELECT * FROM words")
    List<Word> getWords();

    @Query("SELECT * FROM words WHERE status = 'unknown'")
    List<Word> getUnknownWords();

    @Insert
    void insertWord(Word word);

    @Delete
    void deleteWord(Word word);

    @Query("UPDATE words SET status= :status WHERE word = :word")
    void updateWordStatus(String status, String word);

    @Query("UPDATE words SET studyInterval= :interval WHERE word= :word")
    void updateInterval(String word, int interval);

    @Query("UPDATE words SET dueDate= :dueDate WHERE word= :word")
    void updateDueDate(String word, int dueDate);

    @Query("SELECT * FROM words WHERE word= :word")
    boolean ifWordExists(String word);

    @Query("SELECT * FROM words WHERE word= :word")
    Word getWord(String word);

}
