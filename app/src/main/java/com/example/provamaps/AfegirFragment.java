package com.example.provamaps;

import android.app.Dialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfegirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfegirFragment extends Fragment {

    Dialog dialog;

    public AfegirFragment() {
        // Required empty public constructor
    }


    public static AfegirFragment newInstance(String param1, String param2) {
        AfegirFragment fragment = new AfegirFragment();

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
        View rootView = inflater.inflate(R.layout.fragment_afegir, container, false);

        CardView cardViewFont = rootView.findViewById(R.id.cardview_font);
        cardViewFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_font);
            }
        });

        CardView cardViewContenidor = rootView.findViewById(R.id.cardview_contenidor);
        cardViewContenidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_contenidor);
            }
        });

        CardView cardViewLavabo = rootView.findViewById(R.id.cardview_lavabo);
        cardViewLavabo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_lavabo);
            }
        });

        CardView cardViewPicnic = rootView.findViewById(R.id.cardview_picnic);
        cardViewPicnic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al nuevo fragmento al hacer clic en el CardView
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_picnic);
            }
        });

        return rootView;
    }
}