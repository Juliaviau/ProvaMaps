package com.example.provamaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AfegirFontFragment extends Fragment {



    public AfegirFontFragment() {
        // Required empty public constructor
    }


    public static AfegirFontFragment newInstance(String param1, String param2) {
        AfegirFontFragment fragment = new AfegirFontFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_afegir_font, container, false);
    }
}