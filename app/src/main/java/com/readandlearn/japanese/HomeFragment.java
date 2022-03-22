package com.readandlearn.japanese;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class HomeFragment extends Fragment {

    FloatingActionButton addTextFloating;
    private MyTextsAdapter mta;
    private RecyclerView myTextsRecycler;
    private LinearLayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addTextFloating = view.findViewById(R.id.addTextFloating);

        addTextFloating.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), UploadTextActivity.class));
        });

        ArrayList<String> DATA = new ArrayList<>();
        ArrayList<String> USER_TEXTS = new ArrayList<>();
        ArrayList<String> TITLES = new ArrayList<>();

        File userTextsFile = new File(getActivity().getFilesDir() + "/textTitles.txt");

        addTitlesToArray(userTextsFile, USER_TEXTS);

        //emptyView(USER_TEXTS);

        String[] files = getActivity().fileList();
        for (String file : files) {
            for (String text : USER_TEXTS) {
                if (file.equals(text)) {
                    DATA.add(file);
                }
            }
        }
        for (String s : DATA) {
            TITLES.add(s.split(".txt")[0]);
        }

        myTextsRecycler = view.findViewById(R.id.myTextsRecycler);
        mta = new MyTextsAdapter(DATA, TITLES, view.getContext());
        myTextsRecycler.setAdapter(mta);
        layoutManager = new LinearLayoutManager(getContext());
        myTextsRecycler.setLayoutManager(layoutManager);
        //ConstraintLayout layout = view.findViewById(R.id.layout);
    }

    private void addTitlesToArray(File file, ArrayList<String> array) {
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                String title = sc.nextLine();
                array.add(title);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}