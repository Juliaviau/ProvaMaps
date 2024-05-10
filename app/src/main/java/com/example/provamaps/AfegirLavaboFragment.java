package com.example.provamaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfegirLavaboFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfegirLavaboFragment extends Fragment {


    public AfegirLavaboFragment() {
        // Required empty public constructor
    }


    public static AfegirLavaboFragment newInstance(String param1, String param2) {
        AfegirLavaboFragment fragment = new AfegirLavaboFragment();

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
        return inflater.inflate(R.layout.fragment_afegir_lavabo, container, false);
    }
}