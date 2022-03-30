package com.readandlearn.japanese;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.readandlearn.japanese.RoomDatabase.Word;
import com.readandlearn.japanese.RoomDatabase.WordDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recycler = view.findViewById(R.id.unknownWordsRecycler);
        ArrayList<DictionaryEntry> dictionaryEntries = new ArrayList<>();
        Button studyButton = view.findViewById(R.id.studyButton);

        WordDatabase wordDatabase = WordDatabase.getInstance(view.getContext());
        List<Word> unknownWords = wordDatabase.wordDao().getUnknownWords();
        for(Word word: unknownWords){
            dictionaryEntries.add(new DictionaryEntry(word.getWord(), word.getReading(), word.getDefinition()));
        }

        DictionaryRecyclerAdapter adapter = new DictionaryRecyclerAdapter(dictionaryEntries, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        studyButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), Flashcard.class));
        });
    }
}