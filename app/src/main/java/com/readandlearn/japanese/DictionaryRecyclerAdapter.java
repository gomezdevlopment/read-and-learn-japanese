package com.readandlearn.japanese;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DictionaryRecyclerAdapter extends RecyclerView.Adapter<DictionaryRecyclerAdapter.DictionaryViewHolder> {

    private ArrayList<DictionaryEntry> dictionaryEntries;

    public DictionaryRecyclerAdapter(ArrayList<DictionaryEntry> dictionaryEntries){
        this.dictionaryEntries = dictionaryEntries;
    }

    public class DictionaryViewHolder extends RecyclerView.ViewHolder{
        private TextView kanji;
        private TextView reading;
        private TextView englishDefinitions;

        public DictionaryViewHolder(final View view){
            super(view);

            kanji = view.findViewById(R.id.kanji);
            reading = view.findViewById(R.id.reading);
            englishDefinitions = view.findViewById(R.id.englishDefinitions);
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
        holder.kanji.setText(dictionaryEntries.get(position).getKanji());
        holder.reading.setText(dictionaryEntries.get(position).getReading());
        holder.englishDefinitions.setText(dictionaryEntries.get(position).getDefinitions());
    }

    @Override
    public int getItemCount() {
        return dictionaryEntries.size();
    }
}
