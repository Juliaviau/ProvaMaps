package com.example.provamaps;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.provamaps.databinding.FragmentAfegirBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.nullness.qual.NonNull;

import android.Manifest;

public class AfegirFragment extends Fragment {

    private FragmentAfegirBinding binding;
    double latitud = 10.5, longitud;
    Dialog dialog;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

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

        binding = FragmentAfegirBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        obtenirPosicio();
        
        //CardView cardViewFont = view.findViewById(R.id.cardview_font);
        binding.cardviewFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putFloat("latitud", (float) latitud);
                bundle.putFloat("longitud", (float) longitud);
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_font,bundle);
            }
        });

        //CardView cardViewContenidor = view.findViewById(R.id.cardview_contenidor);
        binding.cardviewContenidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putFloat("latitud", (float) latitud);
                bundle.putFloat("longitud", (float) longitud);
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_contenidor,bundle);
            }
        });

        //CardView cardViewLavabo = view.findViewById(R.id.cardview_lavabo);
        binding.cardviewLavabo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putFloat("latitud", (float) latitud);
                bundle.putFloat("longitud", (float) longitud);
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_lavabo,bundle);
            }
        });

        //CardView cardViewPicnic = view.findViewById(R.id.cardview_picnic);
        binding.cardviewPicnic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putFloat("latitud", (float) latitud);
                bundle.putFloat("longitud", (float) longitud);
                Navigation.findNavController(v).navigate(R.id.pagina_afegir_picnic,bundle);
            }
        });

        return view;
    }

    private void obtenirPosicio() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();
                            }
                        }
                    });
        } else {
            //demana permisos
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenirPosicio();
            } else {
                MyUtils.toast(getActivity(),"No s'ha donat permis d'ubicació");
            }
        }
    }
}