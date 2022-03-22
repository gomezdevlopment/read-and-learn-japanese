package com.readandlearn.japanese;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import java.io.*;

public class UploadTextActivity extends AppCompatActivity {

    private String file_name;
    private volatile String textToSave = null;
    Button upload;
    EditText textBox;
    EditText titleTextBox;
    ConstraintLayout layout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_text);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Upload Text");
            actionBar.setElevation(0);
        }

        textBox = findViewById(R.id.uploadTextBox);
        titleTextBox = findViewById(R.id.addTitleTextBox);
        layout = findViewById(R.id.uploadTextLayout);

        upload = findViewById(R.id.button);
        upload.setOnClickListener(view -> {
            if (!checkForInvalidText()) {
                save(view);
            }
        });
    }

    public boolean checkForInvalidText() {
        boolean error;
        String title = titleTextBox.getText().toString();
        String text = textBox.getText().toString();
        file_name = title + ".txt";
        File f = new File(getFilesDir() + "/" + file_name);
        if (f.exists()) {
            createSnackBar(this, layout, "Title Already Exists");
            error = true;
        } else if (title.equals("")) {
            createSnackBar(this, layout, "Empty Title - Please enter a title.");
            error = true;
        } else if (text.equals("")) {
            createSnackBar(this, layout, "Empty Text - Please enter text into the text field.");
            error = true;
        } else {
            error = false;
        }
        return error;
    }

    public void save(View v) {
        textToSave = textBox.getText().toString();
        String title = titleTextBox.getText().toString();
        file_name = title + ".txt";
        Runnable bgTask = () -> {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(file_name, MODE_PRIVATE);
                fos.write(textToSave.getBytes());
                storeTextTitles(file_name);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread bgThread = new Thread(bgTask);
        try {
            bgThread.start();
        } finally {
            startActivity(new Intent(UploadTextActivity.this, MainActivity.class));
        }
    }

    public void storeTextTitles(String title) {
        File userTextsFile = new File(getFilesDir() + "/textTitles.txt");
        FileWriter fw;
        try {
            userTextsFile.createNewFile();
            fw = new FileWriter(userTextsFile, true);
            BufferedWriter bf = new BufferedWriter(fw);
            bf.write(title);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createSnackBar(Context context, ConstraintLayout layout, String message){
        final Snackbar snackbar = Snackbar.make(layout, message, BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", view -> snackbar.dismiss());
        snackbar.setTextColor(Color.WHITE);
        snackbar.setBackgroundTint(Color.BLACK);
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.samuraiBlue));
        snackbar.show();
    }
}
