package com.readandlearn.japanese;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class TranslateFragment extends Fragment {

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

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println(s);

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
        HttpURLConnection conn;
        try {
            url = new URL("https://jisho.org/api/v1/search/words?keyword=" + word);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if(responseCode != 200){
                throw new RuntimeException("HttpResponseCode" + responseCode);
            }else{
                StringBuilder info = new StringBuilder();
                Scanner sc = new Scanner(url.openStream());

                while(sc.hasNext()){
                    info.append(sc.nextLine());
                }

                sc.close();

                System.out.println(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}