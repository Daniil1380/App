package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BibLibAdapter bibLibAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InputStream publications = getResources().openRawResource(R.raw.text);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView =  view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        try {
            bibLibAdapter = new BibLibAdapter(publications);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(bibLibAdapter);
        return view;


    }
}