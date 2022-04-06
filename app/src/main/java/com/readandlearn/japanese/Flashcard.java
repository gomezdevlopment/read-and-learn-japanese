package com.readandlearn.japanese;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.readandlearn.japanese.RoomDatabase.Word;
import com.readandlearn.japanese.RoomDatabase.WordDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Flashcard extends AppCompatActivity {

    ArrayList<Word> flashcards = new ArrayList<>();
    TextView front;
    TextView back;
    TextView reading;
    Button good;
    Button again;
    Button show;
    int index = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.flashcard);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int date = ((year) * 365) + day;

        WordDatabase wordDatabase = WordDatabase.getInstance(this);
        List<Word> unknownWords = wordDatabase.wordDao().getUnknownWords();

        //Load flashcards
        for (Word word : unknownWords) {
            if (word.getDueDate() <= date) {
                flashcards.add(word);
            }
        }

        front = findViewById(R.id.frontOfCard);
        back = findViewById(R.id.backOfCard);
        reading = findViewById(R.id.reading);
        good = findViewById(R.id.goodButton);
        again = findViewById(R.id.againButton);
        show = findViewById(R.id.showButton);

        good.setOnClickListener(v -> {
            String wordAndReading = flashcards.get(index).getWordAndReading();
            float interval = flashcards.get(index).getStudyInterval();
            float newInterval = (float) (interval * 1.45);
            int newDate = (int) (date + newInterval);
            wordDatabase.wordDao().updateInterval(wordAndReading, newInterval);
            wordDatabase.wordDao().updateDueDate(wordAndReading, newDate);
            index += 1;
            setFlashcard();
        });

        again.setOnClickListener(v -> {
            flashcards.add(flashcards.get(index));
            String wordAndReading = flashcards.get(index).getWordAndReading();
            int interval = 1;
            int newInterval = (int) (interval * 1.45);
            wordDatabase.wordDao().updateInterval(wordAndReading, newInterval);
            index += 1;
            setFlashcard();
        });

        show.setOnClickListener(v -> {
            good.setVisibility(View.VISIBLE);
            again.setVisibility(View.VISIBLE);
            show.setVisibility(View.INVISIBLE);
            revealAnswer();
        });

        setFlashcard();
    }

    private void setFlashcard() {
        reading.setText("");
        good.setVisibility(View.INVISIBLE);
        again.setVisibility(View.INVISIBLE);
        back.setText("");
        if (index >= flashcards.size()) {
            front.setText("No More Reviews");
            show.setVisibility(View.INVISIBLE);
        } else {
            show.setVisibility(View.VISIBLE);
            String word = flashcards.get(index).getWord();
            front.setText(word);
        }
    }

    private void revealAnswer() {
        reading.setText(flashcards.get(index).getReading());
        back.setText(flashcards.get(index).getDefinition());
    }
}
