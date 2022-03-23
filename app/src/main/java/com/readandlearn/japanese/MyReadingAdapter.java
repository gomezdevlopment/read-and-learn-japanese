package com.readandlearn.japanese;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MyReadingAdapter extends RecyclerView.Adapter<MyReadingAdapter.MylistAdapterVh>{
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
            textBox.setHighlightColor(Color.TRANSPARENT);
            float fontSize = 30f;
            textBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            textBox.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public SpannableStringBuilder createHighlightsBackground(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder("");
        ArrayList<String> characters = new ArrayList<>();
        for (int characterIndex = 0; characterIndex < text.length(); characterIndex++) {
            characters.add(text.substring(characterIndex, characterIndex + 1));
            characters.add(" ");
        }

        for (String character : characters) {
            if (!character.equals(" ")) {
                SpannableString word = new SpannableString(character);
                ClickableSpan newSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        SpannableString s;
                        TextView tv = (TextView) view;
                        tv.setMovementMethod(null);
                        s = SpannableString.valueOf(tv.getText());
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String word = s.subSequence(start, end).toString();
                        definitionDialog = new Dialog(CONTEXT);
                        showDefinitionPopUp(word, definitionDialog, tv);
                    }
                };
                word.setSpan(newSpan, 0, word.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
//                if (UNKNOWN_WORDS.contains((word.toString().toLowerCase()))) {
//                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.japanRed)), 0, word.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
//                } else if (KNOWN_WORDS.contains(word.toString().toLowerCase())) {
//                    word.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
//                } else {
//                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.samuraiBlue)), 0, word.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
//                }
                sb.append(word);
            } else {
                sb.append("");
            }
        }
        return sb;
    }

    public void showDefinitionPopUp(String word, Dialog dialog, TextView tv) {
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

        DIALOG.show();
        wordToDefine.setText(word);
        setAdapter(recycler, entries);
        searchDict(word, recycler, progressCircle);
        unknown.setOnClickListener(v -> {
            DIALOG.dismiss();
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        });

        known.setOnClickListener(v -> {
            DIALOG.dismiss();
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        });

        close.setOnClickListener(v -> {
            DIALOG.dismiss();
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        });

    }

    private void setAdapter(RecyclerView recycler, ArrayList<DictionaryEntry> dictionaryEntries) {
        adapter = new DictionaryRecyclerAdapter(dictionaryEntries);
        layoutManager = new LinearLayoutManager(recycler.getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private void searchDict(String word, RecyclerView recycler, ProgressBar progressCircle) {
        entries.clear();
        Thread thread = new Thread(() -> {
            entries = parseJSON(queryJisho(word));
            Activity readingActivity = (Activity) CONTEXT;
            readingActivity.runOnUiThread(() -> {
                setAdapter(recycler, entries);
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

    private ArrayList<DictionaryEntry> parseJSON(String info) {
        ArrayList<DictionaryEntry> dictionaryEntries = new ArrayList<>();
        JSONObject data;
        JSONArray array;

        try {
            data = new JSONObject(info);
            array = data.getJSONArray("data");

            for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++) {
                String kanji = "";
                String reading = "";
                StringBuilder englishDefinitions = new StringBuilder("");

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

                if (kanji.isEmpty() && !reading.isEmpty()) {
                    dictionaryEntries.add(new DictionaryEntry(reading, "", String.valueOf(englishDefinitions)));
                } else {
                    dictionaryEntries.add(new DictionaryEntry(kanji, reading, String.valueOf(englishDefinitions)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dictionaryEntries;
    }
}

