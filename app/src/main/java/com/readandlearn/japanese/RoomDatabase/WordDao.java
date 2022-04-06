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

    @Query("UPDATE words SET status= :status WHERE wordAndReading = :wordAndReading")
    void updateWordStatus(String status, String wordAndReading);

    @Query("UPDATE words SET studyInterval= :interval WHERE wordAndReading= :wordAndReading")
    void updateInterval(String wordAndReading, float interval);

    @Query("UPDATE words SET dueDate= :dueDate WHERE wordAndReading= :wordAndReading")
    void updateDueDate(String wordAndReading, int dueDate);

    @Query("SELECT * FROM words WHERE word= :word")
    Word getWord(String word);

    @Query("SELECT * FROM words WHERE wordAndReading= :wordAndReading")
    Word getWordAndReading(String wordAndReading);

}
