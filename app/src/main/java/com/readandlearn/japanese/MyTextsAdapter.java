package com.readandlearn.japanese;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyTextsAdapter extends RecyclerView.Adapter<MyTextsAdapter.MylistAdapterVh> {
    private final ArrayList<String> DATA_TO_DISPLAY;
    private final ArrayList<String> TITLES;
    Context context;

    private String fileName;

    public MyTextsAdapter(ArrayList<String> dataToDisplay, ArrayList<String> titles, Context context) {
        this.DATA_TO_DISPLAY = dataToDisplay;
        this.TITLES = titles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyTextsAdapter.MylistAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new MyTextsAdapter.MylistAdapterVh(LayoutInflater.from(context).inflate(R.layout.text_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyTextsAdapter.MylistAdapterVh holder, int position) {
        holder.title.setText(TITLES.get(holder.getAdapterPosition()));
        holder.card.setOnClickListener(v -> {
              fileName = DATA_TO_DISPLAY.get(holder.getAdapterPosition());
//            Intent myIntent = new Intent(v.getContext(), ReadingScreen.class);
//            startActivity(myIntent);
        });
            holder.card.setOnLongClickListener(v -> {
                fileName = DATA_TO_DISPLAY.get(holder.getAdapterPosition());
                new AlertDialog.Builder(holder.card.getContext())
                        .setTitle(R.string.deleteText)
                        .setMessage(R.string.deleteTextSubText)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            DATA_TO_DISPLAY.remove(holder.getAdapterPosition());
                            TITLES.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            File file = new File(context.getFilesDir(), fileName);
                            if (file.exists()) {
                                context.deleteFile(fileName);
                                deleteTitleFromFile(fileName);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            });

    }

    @Override
    public int getItemCount() {
        return DATA_TO_DISPLAY.size();
    }

    public class MylistAdapterVh extends RecyclerView.ViewHolder {
        TextView title;
        CardView card;

        public MylistAdapterVh(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.myLibraryTitles);
            card = itemView.findViewById(R.id.myTextCard);
        }
    }



    public void deleteTitleFromFile(String fileToDelete) {
        String filepath = context.getFilesDir() + "/textTitles.txt";
        String tempFile = context.getFilesDir() + "temp.txt";
        File originalFile = new File(filepath);
        File newFile = new File(tempFile);
        try {
            FileWriter fw = new FileWriter(newFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            Scanner sc = new Scanner(originalFile);

            while (sc.hasNext()) {
                String title = sc.nextLine();
                if (!title.equals(fileToDelete)) {
                    bw.write(title);
                    bw.newLine();
                }
            }
            sc.close();
            bw.flush();
            bw.close();
            originalFile.delete();
            File dump = new File(filepath);
            newFile.renameTo(dump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
