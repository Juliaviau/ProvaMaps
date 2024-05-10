package com.example.provamaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfegirContenidorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfegirContenidorFragment extends Fragment {


    public AfegirContenidorFragment() {
        // Required empty public constructor
    }


    public static AfegirContenidorFragment newInstance(String param1, String param2) {
        AfegirContenidorFragment fragment = new AfegirContenidorFragment();

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
        return inflater.inflate(R.layout.fragment_afegir_contenidor, container, false);
    }
}