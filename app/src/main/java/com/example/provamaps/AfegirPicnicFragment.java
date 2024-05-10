package com.example.provamaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfegirPicnicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfegirPicnicFragment extends Fragment {

    public AfegirPicnicFragment() {
        // Required empty public constructor
    }


    public static AfegirPicnicFragment newInstance(String param1, String param2) {
        AfegirPicnicFragment fragment = new AfegirPicnicFragment();

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
        return inflater.inflate(R.layout.fragment_afegir_picnic, container, false);
    }
}