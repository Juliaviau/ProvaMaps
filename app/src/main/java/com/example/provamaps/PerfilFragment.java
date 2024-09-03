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
import java.util.List;

public class PerfilFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentPerfilBinding binding;
    private static final String TAG = "PORFILE_TAG";
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<Model_ItemCardPerfil> arrayListPunts = new ArrayList<>();
    SearchView searchView;
    ModelRecyclerView modelRecyclerView;
    private RealtimeManager realtimeManager;

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

        // Inicializa la llista d'items per defecte
        /*arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font3,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font1,"Lavabo","Jadsdrdins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.mapafont,"Picnic","font mco laboris nisi ut aliquip ex ea commodo consequat. "));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font4,"Font","Jardins de Vicenç Albert Ballester i Camps"));
        arrayListFonts.add(new Model_ItemCardPerfil(R.drawable.font2,"Contenidor","Jaaaardins de Vicenç Albert Ballester i Camps"));*/

        //Obté les dades de la base de dades

        //inicialitza el singleton realtimemanager
        realtimeManager = RealtimeManager.getInstance();

        // Obté les dades de la base de dades
        //obtenirFonts();


    }


    private void obtenirContenidors() {
        // Segons les dades del LiveData de RealtimeManager actualitza la UI quan canviin les dades
        realtimeManager.obtenirContenidorsUsuari().observe(getViewLifecycleOwner(), contenidors -> {
            if (contenidors != null && !contenidors.isEmpty()) {
                // Actualitza l'ArrayList de l'adapter i notifica els canvis
                //arrayListFonts.clear();
                for (Contenidor contenidor : contenidors) {

                    // Obté les dades de l'objecte Font
                    String tipus = contenidor.getTipus();
                    String lat = contenidor.getLatitud();
                    String lon = contenidor.getLongitud();
                    String urlFoto = contenidor.getUrlfoto();

                    // Crea el Model_ItemCardPerfil
                    Model_ItemCardPerfil itemCardPerfil = new Model_ItemCardPerfil(
                            /*urlFoto,*/
                            tipus,
                            lat,
                            lon,
                            urlFoto
                    );

                    // Añade el objeto a la lista que maneja el Adapter
                    arrayListPunts.add(itemCardPerfil);
                }
                modelRecyclerView.notifyDataSetChanged();
            } else {
                //Notificar que no hi ha res guardat
                Toast.makeText(getContext(), "No hi ha contenidors guardats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenirPicnics() {
        // Segons les dades del LiveData de RealtimeManager actualitza la UI quan canviin les dades
        realtimeManager.obtenirPicnicsUsuari().observe(getViewLifecycleOwner(), picnics -> {
            if (picnics != null && !picnics.isEmpty()) {
                // Actualitza l'ArrayList de l'adapter i notifica els canvis
                //arrayListFonts.clear();
                for (Picnic picnic : picnics) {

                    // Obté les dades de l'objecte Font
                    String tipus = picnic.getTipus();
                    String lat = picnic.getLatitud();
                    String lon = picnic.getLongitud();
                    String urlFoto = picnic.getUrlfoto();

                    // Crea el Model_ItemCardPerfil
                    Model_ItemCardPerfil itemCardPerfil = new Model_ItemCardPerfil(
                            /*urlFoto,*/
                            tipus,
                            lat,
                            lon,
                            urlFoto
                    );

                    // Añade el objeto a la lista que maneja el Adapter
                    arrayListPunts.add(itemCardPerfil);
                }
                modelRecyclerView.notifyDataSetChanged();
            } else {
                //Notificar que no hi ha res guardat
                Toast.makeText(getContext(), "No hi ha picnics guardats", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void obtenirLavabos() {
        // Segons les dades del LiveData de RealtimeManager actualitza la UI quan canviin les dades
        realtimeManager.obtenirLavabosUsuari().observe(getViewLifecycleOwner(), lavabos -> {
            if (lavabos != null && !lavabos.isEmpty()) {
                // Actualitza l'ArrayList de l'adapter i notifica els canvis
                //arrayListFonts.clear();
                for (Lavabo lavabo : lavabos) {

                    // Obté les dades de l'objecte Font
                    String tipus = lavabo.getTipus();
                    String lat = lavabo.getLatitud();
                    String lon = lavabo.getLongitud();
                    String urlFoto = lavabo.getUrlfoto();


                    // Crea el Model_ItemCardPerfil
                    Model_ItemCardPerfil itemCardPerfil = new Model_ItemCardPerfil(
                            /*urlFoto,*/
                            tipus,
                            lat,
                            lon,
                            urlFoto
                    );

                    // Añade el objeto a la lista que maneja el Adapter
                    arrayListPunts.add(itemCardPerfil);
                }
                modelRecyclerView.notifyDataSetChanged();
            } else {
                //Notificar que no hi ha res guardat
                Toast.makeText(getContext(), "No hi ha lavabos guardats", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void obtenirFonts() {
        // Segons les dades del LiveData de RealtimeManager actualitza la UI quan canviin les dades
        realtimeManager.obtenirFontsUsuari().observe(getViewLifecycleOwner(), fonts -> {
            if (fonts != null && !fonts.isEmpty()) {
                // Actualitza l'ArrayList de l'adapter i notifica els canvis
                //arrayListFonts.clear();
                for (Font font : fonts) {

                    // Obté les dades de l'objecte Font
                    String tipus = font.getTipus();
                    String lat = font.getLatitud();
                    String lon = font.getLongitud();
                    String urlFoto = font.getUrlfoto();//TODO: mirar com van les fotos.


                    // Crea el Model_ItemCardPerfil
                    Model_ItemCardPerfil itemCardPerfil = new Model_ItemCardPerfil(
                            /*urlFoto,*/
                            tipus,
                            lat,
                            lon,
                            urlFoto
                    );

                    // Añade el objeto a la lista que maneja el Adapter
                    arrayListPunts.add(itemCardPerfil);
                }
                MyUtils.toast(getContext(),"abans " + arrayListPunts.size());
                modelRecyclerView.notifyDataSetChanged();
                MyUtils.toast(getContext(),"despres " + arrayListPunts.size());
            } else {
                //Notificar que no hi ha res guardat
                Toast.makeText(getContext(), "No hi fonts ha guardades", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater,container,false);
        //return inflater.inflate(R.layout.fragment_perfil, container, false);


        obtenirFonts();
        obtenirContenidors();
        obtenirPicnics();
        obtenirLavabos();
        MyUtils.toast(getContext(),"dp carregar " + arrayListPunts.size());

        //obtenirFonts();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Missatge d'espera
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Carregant...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        // usersRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // Inicializa el RecyclerView on hi haurà la llista de punts que hagi afegir l'usuari en questio
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));





        // Aporta els elements de l'arraylist al recycleview
        modelRecyclerView = new ModelRecyclerView(requireContext(), arrayListPunts);
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
        MyUtils.toast(getContext(), "arr" + arrayListPunts.size());
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
                            Log.e(TAG, "onDataChange ", e);
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