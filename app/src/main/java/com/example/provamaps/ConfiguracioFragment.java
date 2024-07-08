package com.example.provamaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.provamaps.databinding.FragmentConfiguracioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfiguracioFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FragmentConfiguracioBinding binding;
    private static final String TAG = "CONFIGURACIO_TAG";
    private Context mContext;
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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConfiguracioBinding.inflate(inflater, container,false);






        // Inflate the layout for this fragment
       // View rootView = inflater.inflate(R.layout.fragment_configuracio, container, false);
        mAuth = FirebaseAuth.getInstance();
       // button = rootView.findViewById(R.id.btntancarsessio);
        /*binding.btntancarsessio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });*/
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        //botons tancar sessio i eliminar compte
        binding.mcbTancarSessio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(mContext,MainActivity.class));
                //getActivity().finishAffinity();
            }
        });

    }

    private void logout() {
        mAuth.signOut();
        irMain();
    }

    private void irMain() {
        Intent intent = new Intent(getActivity(), ConfiguracioFragment.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            irMain();
        }
    }
}
/*
public class ConfiguracioFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
 */