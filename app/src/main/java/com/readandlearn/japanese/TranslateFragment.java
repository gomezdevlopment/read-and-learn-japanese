package com.readandlearn.japanese;

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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TranslateFragment extends Fragment {

    HttpURLConnection conn;
    ArrayList<DictionaryEntry> dictionaryEntries;
    RecyclerView recycler;
    DictionaryRecyclerAdapter adapter;
    TextView searchEnglish;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_translate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchBar = view.findViewById(R.id.searchBar);
        dictionaryEntries = new ArrayList<>();
        recycler = view.findViewById(R.id.dictionaryRecycler);
        searchEnglish = view.findViewById(R.id.searchEnglish);
        progressBar = view.findViewById(R.id.progressBar);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchEnglish.setVisibility(View.INVISIBLE);
                char first = s.charAt(0);
                char last = s.charAt(s.length()-1);

                recycler.setAdapter(null);
                progressBar.setVisibility(View.VISIBLE);
                Thread thread = new Thread(() -> {
                    try  {
                        queryJisho(s, Character.compare(first, last) == 0 && Character.compare(first, '\"') == 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void queryJisho(String word, boolean englishSearch){
        URL url;
        StringBuilder info = new StringBuilder();

        try {
            url = new URL("https://jisho.org/api/v1/search/words?keyword=" + word);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if(responseCode != 200){
                throw new RuntimeException("HttpResponseCode" + responseCode);
            }else{
                Scanner sc = new Scanner(url.openStream());

                while(sc.hasNext()){
                    String line = sc.nextLine();
                    info.append(line);
                }
                sc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            conn.disconnect();
        }

        JSONObject data;
        JSONArray array;
        dictionaryEntries.clear();
        try {
            data = new JSONObject(info.toString());
            array = data.getJSONArray("data");

            for(int arrayIndex = 0; arrayIndex<array.length(); arrayIndex++){
                String kanji = "";
                String reading = "";
                StringBuilder englishDefinitions = new StringBuilder("");

                try{
                    kanji = array.getJSONObject(arrayIndex).getJSONArray("japanese").getJSONObject(0).getString("word");
                }catch(JSONException e){
                    e.printStackTrace();
                }

                try{
                    reading = array.getJSONObject(arrayIndex).getJSONArray("japanese").getJSONObject(0).getString("reading");
                }catch(JSONException e){
                    e.printStackTrace();
                }

                try{
                    JSONArray senses = array.getJSONObject(arrayIndex).getJSONArray("senses");

                    for(int i = 0; i<senses.length(); i++){
                        JSONArray definitions = senses.getJSONObject(i).getJSONArray("english_definitions");
                        englishDefinitions.append(i+1).append(".) ");
                        for(int j= 0; j < definitions.length(); j++){
                            String definition = definitions.getString(j);
                            if(j == definitions.length()-1){
                                englishDefinitions.append(definition).append("\n");
                            }else{
                                englishDefinitions.append(definition).append(", ");
                            }
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }

                if(kanji.isEmpty() && !reading.isEmpty()){
                    dictionaryEntries.add(new DictionaryEntry(reading, "", String.valueOf(englishDefinitions)));
                }else{
                    dictionaryEntries.add(new DictionaryEntry(kanji, reading, String.valueOf(englishDefinitions)));
                }
            }

            searchEnglish.setOnClickListener(view -> {
                String englishQuery = "\"" + word + "\"";

                Thread thread = new Thread(() -> {
                    try  {
                        queryJisho(englishQuery, true);
                        requireActivity().runOnUiThread(() -> {
                            searchEnglish.setVisibility(View.INVISIBLE);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            });

            requireActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                setAdapter();
                Pattern p = Pattern.compile("[^a-zA-Z0-9]");
                boolean hasSpecialChar = p.matcher(word).find();
                if(!hasSpecialChar && !englishSearch){
                    searchEnglish.setText("Search for the english word '" + word + "' instead?");
                    searchEnglish.setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(){
        adapter = new DictionaryRecyclerAdapter(dictionaryEntries, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }
}