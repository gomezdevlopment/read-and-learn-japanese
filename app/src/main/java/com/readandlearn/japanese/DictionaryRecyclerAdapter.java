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
    public static int arraySize;
    public static DictionaryRecyclerAdapter.DictionaryViewHolder holderReference;

    public DictionaryRecyclerAdapter(ArrayList<DictionaryEntry> dictionaryEntries, boolean showCheckBox){
        this.dictionaryEntries = dictionaryEntries;
        this.showCheckBox = showCheckBox;
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
        holderReference = holder;
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
            if(holderReference.checkBox.isChecked()){
                checkedBoxes.add(i);
            }
        }
        return checkedBoxes;
    }
}
