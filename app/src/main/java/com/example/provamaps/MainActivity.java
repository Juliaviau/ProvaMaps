package com.example.provamaps;

import static com.example.provamaps.Llenguatge.setLocale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.provamaps.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {

    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding binding;
    private SearchView buscadorMapa;
    private final int FINE_PERMISSION_CODE = 1;
    ImageButton creuDialog;
    Button btn_inici_sessio, btn_tornar_mapa;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);
        loadLocale();
        firebaseAuth = FirebaseAuth.getInstance();
        //Si quan entra al MainActivity, no s'ha registrat va a la pagina de login
        if (firebaseAuth.getCurrentUser() == null) {
            startLoginOptionsActivity();
        }

        View alertCustomDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_fragment,null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(alertCustomDialog);
        creuDialog = (ImageButton) alertCustomDialog.findViewById(R.id.btn_creu_dialog);
        btn_inici_sessio = (Button) alertCustomDialog.findViewById(R.id.btn_signup);
        btn_tornar_mapa = (Button) alertCustomDialog.findViewById(R.id.btn_tornamapa);

        dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.show();//comentar. posa el alert dialog nomes d'iniciar el mapa

        creuDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_inici_sessio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startLoginOptionsActivity();
            }
        });

        btn_tornar_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                //Toast.makeText(MainActivity.this,"taralaralà",Toast.LENGTH_SHORT).show();
            }
        });

        setupNavegacio();
    }


    public void loadLocale() {
        //obté el llenguatge de preferencia de l'aplicació
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String llengapp = prefs.getString("My_Lang", "");

        // Obtenir l'idioma actual del dispositiu
        Locale llengdis = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            llengdis = getResources().getConfiguration().getLocales().get(0);
        }
        String llengactual = llengdis.getLanguage();

        // Comprovar si l'idioma guardat és diferent de l'idioma actual del dispositiu
        if (!llengapp.isEmpty() && !llengapp.equals(llengactual)) {
            // Només canviar l'idioma si és diferent
            Llenguatge.setLocale(MainActivity.this, llengapp);
            /*MyUtils.toast(MainActivity.this, "Idioma canviat a: " + llengapp);
        } else {
            MyUtils.toast(MainActivity.this, "Idioma actual: " + llengactual);
        }*/
        }
    }


    private void startLoginOptionsActivity() {
        //va al login, i deixa aquesta de fons per si fa un finish
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        //finish();//afegit
    }

    private void setupNavegacio() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hostfragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.pagina_perfil || itemId == R.id.pagina_afegir) { //Si al menu clica el perfil o la pagina per afegir
                    if (firebaseAuth.getCurrentUser() == null) { //I no hi ha cap usuari registrat

                        //Toast.makeText(MainActivity.this, "Please sign in with Google first.", Toast.LENGTH_SHORT).show();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        // startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        return false; // Prevent navigation to the Profile fragment
                    }
                }
                return NavigationUI.onNavDestinationSelected(item, navHostFragment.getNavController());
            }
        });
    }

    public void amagarBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void mostrarBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}