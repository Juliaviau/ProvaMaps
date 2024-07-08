package com.example.provamaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.provamaps.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PerfilFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentPerfilBinding binding;
    private static final String TAG = "PORFILE_TAG";
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<Model_ItemCardPerfil> arrayList = new ArrayList<>();
    SearchView searchView;
    ModelRecyclerView modelRecyclerView;

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
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayList.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));

        // Maneja los argumentos si es necesario
        if (getArguments() != null) {
            // Código para manejar los argumentos
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater,container,false);
        // Infla el diseño del fragmento
        //return inflater.inflate(R.layout.fragment_perfil, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();



        // usersRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // Inicializa el RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Configura el adaptador del RecyclerView
        modelRecyclerView = new ModelRecyclerView(requireContext(), arrayList);
        binding.recyclerView.setAdapter(modelRecyclerView);

        binding.searchviewa.setOnQueryTextListener(this);

        loadMyInfo();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        modelRecyclerView.filtrar(newText);
        return false;
    }

    //rep el context de l'activity mainActivity
    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    private void loadMyInfo () {
        //Toast.makeText(mContext, "entra a loadmyinfo  sdf", Toast.LENGTH_SHORT).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(""+firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Les dades que treu de la base de dades
                        String email = ""+dataSnapshot.child("email").getValue();
                        String name = ""+dataSnapshot.child("name").getValue();
                        //String profileImageUrl = ""+dataSnapshot.child("profileImageUrl").getValue();
                        String profileImageUrl = firebaseAuth.getCurrentUser().getPhotoUrl().toString();

                        //pasar les dades a al UI
                        binding.tvCorreuUsuari.setText(email);
                        binding.tvNomUsuari.setText(name);

                        //Toast.makeText(mContext,profileImageUrl, Toast.LENGTH_SHORT).show();

                        //Foto perfil
                        try {
                            Glide.with(mContext)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.icona_persona_blanc)
                                    .into(binding.porfileIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onCreateChange ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                        //MyUtils.toast(mContext, "Error: " + databaseError.getMessage());
                    }
                });
    }
}