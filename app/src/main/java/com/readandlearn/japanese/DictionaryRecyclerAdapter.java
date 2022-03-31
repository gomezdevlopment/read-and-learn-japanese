package com.readandlearn.japanese;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DictionaryRecyclerAdapter extends RecyclerView.Adapter<DictionaryRecyclerAdapter.DictionaryViewHolder> {

    private ArrayList<DictionaryEntry> dictionaryEntries;
    private boolean showCheckBox;
    private ArrayList<Integer> flashcards;
    public static int arraySize;
    private static ArrayList<CheckBox> checkBoxes;

    public DictionaryRecyclerAdapter(ArrayList<DictionaryEntry> dictionaryEntries, boolean showCheckBox, ArrayList<Integer> flashcards){
        this.dictionaryEntries = dictionaryEntries;
        this.showCheckBox = showCheckBox;
        this.flashcards = flashcards;
        this.checkBoxes = new ArrayList<>();
        arraySize = dictionaryEntries.size();
    }

    public class DictionaryViewHolder extends RecyclerView.ViewHolder{
        private TextView kanji;
        private TextView reading;
        private TextView englishDefinitions;
        private CheckBox checkBox;

        public DictionaryViewHolder(final View view){
            super(view);
            kanji = view.findViewById(R.id.kanji);
            reading = view.findViewById(R.id.reading);
            englishDefinitions = view.findViewById(R.id.englishDefinitions);
            checkBox = view.findViewById(R.id.checkBox);
            if(!showCheckBox){
                checkBox.setVisibility(View.INVISIBLE);
            }
        }
    }
    @NonNull
    @Override
    public DictionaryRecyclerAdapter.DictionaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_entry, parent, false);
        return new DictionaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryRecyclerAdapter.DictionaryViewHolder holder, int position) {
        String kanjiAndReading = dictionaryEntries.get(position).getKanjiAndReading();
        String kanjiString = dictionaryEntries.get(position).getKanji();
        String readingString = dictionaryEntries.get(position).getReading();
        String definitionString = dictionaryEntries.get(position).getDefinitions();
        holder.kanji.setText(kanjiString);
        holder.reading.setText(readingString);
        holder.englishDefinitions.setText(definitionString);

        if(position == 0){
            holder.checkBox.setChecked(true);
        }

        if(flashcards.contains(position)){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setText("");
        }

        if(kanjiString.equals("Sorry, no definition was found.")){
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        checkBoxes.add(holder.checkBox);
    }

    @Override
    public int getItemCount() {
        return dictionaryEntries.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static ArrayList<Integer> getCheckedBoxes(){
        ArrayList<Integer> checkedBoxes = new ArrayList<>();
        for(int i = 0; i < arraySize; i++){
            if(checkBoxes.get(i).isChecked()){
                checkedBoxes.add(i);
            }
        }
        return checkedBoxes;
    }
}
