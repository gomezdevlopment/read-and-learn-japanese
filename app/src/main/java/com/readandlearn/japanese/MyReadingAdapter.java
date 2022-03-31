package com.readandlearn.japanese;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.readandlearn.japanese.RoomDatabase.Word;
import com.readandlearn.japanese.RoomDatabase.WordDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyReadingAdapter extends RecyclerView.Adapter<MyReadingAdapter.MylistAdapterVh> {
    private final Context CONTEXT;
    private final ArrayList<String> PLAIN_LINES;
    private Dialog definitionDialog;
    ArrayList<DictionaryEntry> entries = new ArrayList<>();
    DictionaryRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    public MyReadingAdapter(Context context, ArrayList<String> plain, Dialog dialog) {
        this.PLAIN_LINES = plain;
        this.CONTEXT = context;
        this.definitionDialog = dialog;
    }

    @NonNull
    @Override
    public MyReadingAdapter.MylistAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyReadingAdapter.MylistAdapterVh(LayoutInflater.from(CONTEXT).inflate(R.layout.text_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyReadingAdapter.MylistAdapterVh holder, int position) {
        String line = PLAIN_LINES.get(position);
        SpannableStringBuilder sb = createHighlightsBackground(line);
        holder.textBox.setText(sb);
    }

    @Override
    public int getItemCount() {
        return PLAIN_LINES.size();
    }

    public class MylistAdapterVh extends RecyclerView.ViewHolder {
        TextView textBox;

        public MylistAdapterVh(@NonNull View itemView) {
            super(itemView);
            textBox = itemView.findViewById(R.id.recyclerTextView);
            float fontSize = 30f;
            textBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            textBox.setMovementMethod(LinkMovementMethod.getInstance());
            setCustomActionCallback(textBox);
        }
    }

    private void setCustomActionCallback(TextView tv){
        tv.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                menu.clear();
                menu.add(0, 0, 0, "Define");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                tv.setMovementMethod(new ArrowKeyMovementMethod());
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                String text;
                if (menuItem.getItemId() == 0) {
                    int min = 0;
                    int max = tv.getText().length();
                    if (tv.isFocused()) {
                        final int selStart = tv.getSelectionStart();
                        final int selEnd = tv.getSelectionEnd();
                        min = Math.max(0, Math.min(selStart, selEnd));
                        max = Math.max(0, Math.max(selStart, selEnd));
                    }
                    text = tv.getText().toString().substring(min, max);
                    if (!text.isEmpty()) {
                        showDefinitionPopUp(text, definitionDialog);
                        actionMode.finish();
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

    }

    public SpannableStringBuilder createHighlightsBackground(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        WordDatabase wordDatabase = WordDatabase.getInstance(CONTEXT);
        List<Word> listOfWords = wordDatabase.wordDao().getWords();
        List<String> wordNames = new ArrayList<>();

        for(Word word : listOfWords){
            wordNames.add(word.getWord());
        }

        //Sort Words by length
        for (int i=1; i<wordNames.size(); i++)
        {
            String temp = wordNames.get(i);
            int j = i - 1;
            while (j >= 0 && temp.length() > wordNames.get(j).length())
            {
                wordNames.set(j + 1, wordNames.get(j));
                j--;
            }
            wordNames.set(j + 1, temp);
        }

        ArrayList<int[]> spanRanges = new ArrayList<>();


        for (String wordName : wordNames) {
            if (sb.toString().contains(wordName)) {
                ArrayList<int[]> wordIndices = findIndicesOfWord(text, wordName);
                for(int[] indices: wordIndices){
                    SpannableString span = new SpannableString(wordName);
                    Word word = wordDatabase.wordDao().getWord(wordName);
                    int spanColor = ContextCompat.getColor(CONTEXT, R.color.black);
                    if (word.getStatus().equals("unknown")) {
                        spanColor = ContextCompat.getColor(CONTEXT, R.color.japanRed);
                    } else if (word.getStatus().equals("known")) {
                        spanColor = ContextCompat.getColor(CONTEXT, R.color.blue);
                    }
                    int finalSpanColor = spanColor;
                    ClickableSpan newSpan = new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(finalSpanColor);
                            ds.setUnderlineText(true);
                        }

                        @Override
                        public void onClick(@NonNull View view) {
                            SpannableString s;
                            TextView tv = (TextView) view;
                            s = SpannableString.valueOf(tv.getText());
                            int start = s.getSpanStart(this);
                            int end = s.getSpanEnd(this);
                            String word = s.subSequence(start, end).toString();
                            definitionDialog = new Dialog(CONTEXT);
                            showDefinitionPopUp(word, definitionDialog);
                        }
                    };

                    span.setSpan(newSpan, 0, wordName.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    int[] newIndices = null;
                    int index = indices[0];
                    int indexEnd = indices[1];

                    if(!spanRanges.isEmpty()){
                        boolean overlap = false;
                        for(int[] indicesArray: spanRanges){
                            if (index < indicesArray[1] && indexEnd > indicesArray[0]) {
                                overlap = true;
                                break;
                            }
                        }
                        if(!overlap){
                            sb.replace(index, indexEnd, span);
                            newIndices = new int[]{index, indexEnd};
                        }
                    }else{
                        sb.replace(index, indexEnd, span);
                        spanRanges.add(new int[]{index, indexEnd});
                    }
                    if(newIndices!=null){
                        spanRanges.add(new int[]{index, indexEnd});
                    }
                }
            }
        }
        return sb;
    }

    private ArrayList<int[]> findIndicesOfWord(String text, String wordName){
        ArrayList<int[]> wordIndices = new ArrayList<>();
        int index = text.indexOf(wordName);
        int indexEnd = index+wordName.length();
        wordIndices.add(new int[]{index, indexEnd});
        while(index >= 0) {
            index = text.indexOf(wordName, index+1);
            indexEnd = index+wordName.length();
            if(index >= 0){
                wordIndices.add(new int[]{index, indexEnd});
            }
        }
        return wordIndices;
    }


    public void showDefinitionPopUp(String string, Dialog dialog) {
        String word = string.replaceAll("\\s+", "");
        final Dialog DIALOG = dialog;
        float dimAmount = (float) .4;
        DIALOG.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DIALOG.getWindow().setDimAmount(dimAmount);
        DIALOG.setContentView(R.layout.popup_window);
        DIALOG.setCanceledOnTouchOutside(false);

        TextView wordToDefine = DIALOG.findViewById(R.id.wordBox);
        Button unknown = DIALOG.findViewById(R.id.unknownButton);
        Button close = DIALOG.findViewById(R.id.closeButton);
        Button known = DIALOG.findViewById(R.id.knownButton);
        RecyclerView recycler = DIALOG.findViewById(R.id.popupRecycler);
        ProgressBar progressCircle = DIALOG.findViewById(R.id.progressCircle);

        WordDatabase wordDatabase = WordDatabase.getInstance(CONTEXT);
        List<Word> words = wordDatabase.wordDao().getWords();
        ArrayList<String> wordAndReadings = new ArrayList<>();
        for(Word word1: words){
            wordAndReadings.add(word1.getWordAndReading());
        }

        DIALOG.show();
        wordToDefine.setText(word);
        known.setEnabled(false);
        unknown.setEnabled(false);
        setAdapter(recycler, entries, new ArrayList<>(), false);
        searchDict(word, recycler, progressCircle, known, unknown, wordAndReadings);
        unknown.setOnClickListener(v -> {
            ArrayList<Integer> indexesOfWordsToMarkUnknown = DictionaryRecyclerAdapter.getCheckedBoxes();
            for(int index: indexesOfWordsToMarkUnknown){
                if(wordAndReadings.contains(entries.get(index).getKanjiAndReading())){
                    String wordAndReading = entries.get(index).getKanjiAndReading();
                    if(wordDatabase.wordDao().getWordAndReading(wordAndReading).getStatus().equals("known")){
                        wordDatabase.wordDao().updateWordStatus("unknown", wordAndReading);
                    }
                }else{
                    wordDatabase.wordDao().insertWord(new Word(word, entries.get(index).getReading(), entries.get(index).getDefinitions(), "unknown"));
                }
            }
            DIALOG.dismiss();
        });

        known.setOnClickListener(v -> {
            ArrayList<Integer> indexesOfWordsToMarkKnown = DictionaryRecyclerAdapter.getCheckedBoxes();
            for(int index: indexesOfWordsToMarkKnown){
                System.out.println(indexesOfWordsToMarkKnown.size());
                if(wordAndReadings.contains(entries.get(index).getKanjiAndReading())){
                    String wordAndReading = entries.get(index).getKanjiAndReading();
                    System.out.println(wordAndReading);
                    if(wordDatabase.wordDao().getWordAndReading(wordAndReading).getStatus().equals("unknown")){
                        System.out.println("Success");
                        wordDatabase.wordDao().updateWordStatus("known", wordAndReading);
                    }
                }else{
                    wordDatabase.wordDao().insertWord(new Word(word, entries.get(index).getReading(), entries.get(index).getDefinitions(), "known"));
                }
            }

            DIALOG.dismiss();
        });

        close.setOnClickListener(v -> DIALOG.dismiss());

        DIALOG.setOnDismissListener(v ->{
            notifyItemRangeChanged(0, PLAIN_LINES.size());
        });

    }

    private void setAdapter(RecyclerView recycler, ArrayList<DictionaryEntry> dictionaryEntries, ArrayList<Integer> flashcards, boolean showCheckBox) {
        adapter = new DictionaryRecyclerAdapter(dictionaryEntries, true, flashcards);
        layoutManager = new LinearLayoutManager(recycler.getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private void searchDict(String word, RecyclerView recycler, ProgressBar progressCircle, Button known, Button unknown, ArrayList<String> wordAndReadings) {
        entries.clear();
        ArrayList<Integer> flashcards = new ArrayList<>();
        Thread thread = new Thread(() -> {
            entries = parseJSON(queryJisho(word), word);
            Activity readingActivity = (Activity) CONTEXT;
            readingActivity.runOnUiThread(() -> {
                if (entries.isEmpty()) {
                    entries.add(new DictionaryEntry("Sorry, no definition was found.", "", ""));
                    setAdapter(recycler, entries, flashcards, false);
                    known.setEnabled(false);
                    unknown.setEnabled(false);

                }else{
                    for(int i = 0; i<entries.size(); i++){
                        if(wordAndReadings.contains(entries.get(i).getKanjiAndReading())){
                            flashcards.add(i);
                        }
                    }
                    setAdapter(recycler, entries, flashcards, true);
                    known.setEnabled(true);
                    unknown.setEnabled(true);
                }
                progressCircle.setVisibility(View.INVISIBLE);
            });
        });
        thread.start();
    }

    private String queryJisho(String word) {
        StringBuilder info = new StringBuilder();
        try {
            URL url = new URL("https://jisho.org/api/v1/search/words?keyword=" + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode" + responseCode);
            } else {
                Scanner sc = new Scanner(url.openStream());

                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    info.append(line);
                }
                sc.close();
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info.toString();
    }

    private ArrayList<DictionaryEntry> parseJSON(String info, String word) {
        ArrayList<DictionaryEntry> dictionaryEntries = new ArrayList<>();
        JSONObject data;
        JSONArray array;

        try {
            data = new JSONObject(info);
            array = data.getJSONArray("data");
            int duplicateEntry = 1;
            for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++) {
                String kanji = "";
                String reading = "";
                StringBuilder englishDefinitions = new StringBuilder();

                try {
                    kanji = array.getJSONObject(arrayIndex).getJSONArray("japanese").getJSONObject(0).getString("word");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    reading = array.getJSONObject(arrayIndex).getJSONArray("japanese").getJSONObject(0).getString("reading");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray senses = array.getJSONObject(arrayIndex).getJSONArray("senses");

                    for (int i = 0; i < senses.length(); i++) {
                        JSONArray definitions = senses.getJSONObject(i).getJSONArray("english_definitions");
                        englishDefinitions.append(i + 1).append(".) ");
                        for (int j = 0; j < definitions.length(); j++) {
                            String definition = definitions.getString(j);
                            if (j == definitions.length() - 1) {
                                englishDefinitions.append(definition).append("\n");
                            } else {
                                englishDefinitions.append(definition).append(", ");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (kanji.isEmpty() && !reading.isEmpty() && reading.equals(word)) {
                    dictionaryEntries.add(new DictionaryEntry(reading, String.valueOf(duplicateEntry), String.valueOf(englishDefinitions)));
                    duplicateEntry++;
                } else if (kanji.equals(word)) {
                    dictionaryEntries.add(new DictionaryEntry(kanji, reading, String.valueOf(englishDefinitions)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dictionaryEntries;
    }
}

