package com.example.provamaps;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.provamaps.databinding.FragmentConfiguracioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ConfiguracioFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FragmentConfiguracioBinding binding;
    private static final String TAG = "CONFIGURACIO_TAG";
    private Context mContext;
    private boolean modeFosc = false;
private ProgressDialog progressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public ConfiguracioFragment() {
        // Required empty public constructor
    }

    public static ConfiguracioFragment newInstance(String param1, String param2) {
        ConfiguracioFragment fragment = new ConfiguracioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        modeFosc = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConfiguracioBinding.inflate(inflater, container,false);

        if (modeFosc) {
            binding.tvModeApp.setText(R.string.modeclar);
            binding.tvModeApp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icona_mode_fosc, 0, 0, 0);
        } else {
            binding.tvModeApp.setText(R.string.modefosc);
            binding.tvModeApp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icona_mode_clar, 0, 0, 0);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("...");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        comprovarUsuariRegistrat();

        //botons tancar sessio i eliminar compte
        binding.mcbTancarSessio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mAuth.getCurrentUser() == null) {
                    MyUtils.toast(mContext, "No estas registrat");
                } else {

                    mAuth.signOut();
                    startActivity(new Intent(mContext, MainActivity.class));
                    //getActivity().finishAffinity();
                }
            }
        });

        binding.mcbEliminarCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    MyUtils.toast(mContext, "No estas registrat");
                } else {
                    //mAuth.signOut();
                    //startActivity(new Intent(mContext,MainActivity.class));
                    //getActivity().finishAffinity();
                }

            }
        });

        //canviMode();
        binding.mcbCanviModeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canviMode();
            }
        });

        binding.mcbCanviIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
                String currentLanguage = prefs.getString("My_Lang", "ca"); // "ca" és el valor per defecte

                // Canviar l'idioma només si és diferent de l'actual
                String newLanguage = currentLanguage.equals("ca") ? "es" : "ca";

                if (!currentLanguage.equals(newLanguage)) {
                    // Canviar l'idioma i recrear l'activitat si cal
                    Llenguatge.setLocale(getActivity(), newLanguage);
                }
            }
        });
    }



    private void canviMode() {

        //boolean isNightMode = (getResources().getConfiguration().uiMode
                //& Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (modeFosc) {
            binding.tvModeApp.setText("Canviar a mode clar");
            binding.tvModeApp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icona_mode_fosc, 0, 0, 0);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            binding.tvModeApp.setText("Canviar a mode fosc");
            binding.tvModeApp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icona_mode_clar, 0, 0, 0);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

    private void comprovarUsuariRegistrat() {
        if (mAuth.getCurrentUser() == null) {
            // Usuari no registrat, desactivar tancar i eliminar sessio
            binding.mcbTancarSessio.setEnabled(false);
            binding.mcbEliminarCompte.setEnabled(false);
        } else {
            // Usuari registrat, activar tancar i eliminar sessio
            binding.mcbTancarSessio.setEnabled(true);
            binding.mcbEliminarCompte.setEnabled(true);
        }
    }


    private void logout() {
        mAuth.signOut();
        anarMain();
    }

    private void anarMain() {
        Intent intent = new Intent(getActivity(), ConfiguracioFragment.class);
        startActivity(intent);
        getActivity().finish();
    }

   /* @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            anarMain();
        }
    }*/
}
/*
public class ConfiguracioFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
 */