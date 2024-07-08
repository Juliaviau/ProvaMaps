package com.example.provamaps;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
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

//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.example.provamaps.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {


    private FirebaseAuth firebaseAuth;

    private ActivityMainBinding binding;

    private GoogleMap mMap;
    private SearchView buscadorMapa;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    ImageButton creuDialog;
    Button btn_inici_sessio, btn_tornar_mapa;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(MainActivity.this,"taralaralà",Toast.LENGTH_SHORT).show();
            }
        });

        //buscadorMapa = findViewById(R.id.buscadorMapaView);

        /*buscadorMapa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String posicio = buscadorMapa.getQuery().toString();
                List<Address> llistaAdreces = null;

                if (posicio != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        llistaAdreces = geocoder.getFromLocationName(posicio,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address adress = llistaAdreces.get(0);
                    LatLng latLng = new LatLng(adress.getLatitude(), adress.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(posicio));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //mapFragment.getMapAsync(MainActivity.this);
        */
        /*fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();*/

        setupNavegacio();
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


    /*private void setupNavegacio() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hostfragment);
        NavigationUI.setupWithNavController(
                bottomNavigationView, navHostFragment.getNavController()
        );
    }*/

    public void amagarBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void mostrarBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }


    /*private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
                                      @Override
                                      public void onSuccess(Location location) {
                                          if (location != null) {
                                              currentLocation = location;
                                              // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                              SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                                              mapFragment.getMapAsync(MainActivity.this);
                                          }
                                      }
                                  }

        );
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng posicioActual = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicioActual, 18));
        MarkerOptions options = new MarkerOptions().position(posicioActual).title("La meva posició");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));//color del marcador
        mMap.addMarker(options);

        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);*//*
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }*/

  /*  @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            } else {
                Toast.makeText(this, "No hi ha permisos de localització, siusplau activa-les.",Toast.LENGTH_SHORT).show();
            }
        }
    }


    //PER EL MENU DE LA DRETA
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    //PER EL MENU DE LA DRETA
  /*  @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mapNone) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.mapHibrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if(id == R.id.mapNormal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if(id == R.id.mapSatelit) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if(id == R.id.mapTerreny) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        return super.onOptionsItemSelected(item);
    }*/
}

