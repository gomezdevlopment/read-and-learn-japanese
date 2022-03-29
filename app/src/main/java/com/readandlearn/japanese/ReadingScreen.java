package com.readandlearn.japanese;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class ReadingScreen extends AppCompatActivity{
    private Dialog definitionDialog;
    private final ArrayList<String> PLAIN_STRINGS = new ArrayList<>();
    RecyclerView textRecyclerView;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Read");
            actionBar.setElevation(0);
        }

        fileName = getIntent().getExtras().getString("filename");
        definitionDialog = new Dialog(ReadingScreen.this);

        loadText();

        textRecyclerView = findViewById(R.id.readingScreenRecycler);
        MyReadingAdapter mra = new MyReadingAdapter(ReadingScreen.this, PLAIN_STRINGS, definitionDialog);
        textRecyclerView.setAdapter(mra);
        RecyclerView.ItemAnimator animator = textRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        Objects.requireNonNull(textRecyclerView.getItemAnimator()).setChangeDuration(0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        textRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadText() {
        InputStream is = null;
        String textContent = "";
        try {
            is = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder(textContent);
            String text;

            while ((text = br.readLine()) != null) {
                if (text.length() > 1) {
                    sb.append(text).append(" ");
                } else {
                    PLAIN_STRINGS.add(sb.toString());
                    sb = new StringBuilder("");
                }
            }
            PLAIN_STRINGS.add(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (definitionDialog != null)
            definitionDialog.dismiss();
    }
}

