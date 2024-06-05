package com.example.provamaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PerfilFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Model_ItemCardPerfil> arrayList = new ArrayList<>();

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa la lista de items aquí si es necesario
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));

        // Maneja los argumentos si es necesario
        if (getArguments() != null) {
            // Código para manejar los argumentos
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el diseño del fragmento
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // usersRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // Inicializa el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Configura el adaptador del RecyclerView
        ModelRecyclerView modelRecyclerView = new ModelRecyclerView(requireContext(), arrayList);
        recyclerView.setAdapter(modelRecyclerView);
    }
}