package com.readandlearn.japanese;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;


public class TranslateFragment extends Fragment {

    HttpURLConnection conn;
    TextView wordInfo;

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
        wordInfo = view.findViewById(R.id.wordInfo);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Thread thread = new Thread(() -> {
                    try  {
                        queryJisho(s);
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

    private void queryJisho(String word){
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
        try {
            data = new JSONObject(info.toString());
            array = data.getJSONArray("data");

            String kanji = array.getJSONObject(0).getJSONArray("japanese").getJSONObject(0).getString("word");
            String reading = array.getJSONObject(0).getJSONArray("japanese").getJSONObject(0).getString("reading");
            StringBuilder englishDefinitions = new StringBuilder("");

            JSONArray senses = array.getJSONObject(0).getJSONArray("senses");

            for(int i = 0; i<senses.length(); i++){
                JSONArray definitions = senses.getJSONObject(i).getJSONArray("english_definitions");
                for(int j= 0; j < definitions.length(); j++){
                    String definition = definitions.getString(j);
                    if(j != definitions.length()-1){
                        englishDefinitions.append(definition);
                    }else{
                        englishDefinitions.append(definition).append(", ");
                    }
                }
            }
            
            requireActivity().runOnUiThread(() -> {
                wordInfo.setText(kanji);
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}