package com.example.provamaps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ModelRecyclerView extends RecyclerView.Adapter<ModelRecyclerView.ViewHolder> {

    Context context;
    ArrayList<Model_ItemCardPerfil> arrayList = new ArrayList<>();
    ArrayList<Model_ItemCardPerfil> llistaOriginal;
    private Geocoder geocoder;

    public ModelRecyclerView(Context context, ArrayList<Model_ItemCardPerfil> arrayPunts) {
        this.context = context;
        arrayList = new ArrayList<>(arrayPunts);
        llistaOriginal = arrayPunts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Crea per a cada un com es mostra
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_perfil,parent,false);
        geocoder = new Geocoder(context, Locale.getDefault());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelRecyclerView.ViewHolder holder, int posicio) {

        //Obte l'imatge del punt, el tipus i l'adreca del punt de la posicio especificada
        holder.imageView.setImageResource(arrayList.get(posicio).getImg());

        Model_ItemCardPerfil item = arrayList.get(posicio);

        Glide.with(context)
                .load(item.getUrlfoto())
                .placeholder(R.drawable.icona_fer_foto)  // Imagen de carga (opcional)
                .error(R.drawable.icona_imatge)  // Imagen en caso de error (opcional)
                .into(holder.imageView);  // Imagen en el ImageView



        holder.tipus.setText(arrayList.get(posicio).getTipus());

        try {
            double lat = Double.parseDouble(item.getLat());
            double lon = Double.parseDouble(item.getLon());

            List<Address> adreca = geocoder.getFromLocation(lat, lon, 1);

            if (adreca != null && !adreca.isEmpty()) {
                Address address = adreca.get(0);

                String poblacio = address.getLocality();
                String provincia = address.getAdminArea();
                String pais = address.getCountryName();
                String numero = address.getFeatureName();
                String comarca = address.getSubAdminArea();
                String carrer = address.getThoroughfare();

                holder.adreca.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

            } else {
                // Si no se encuentra una dirección, muestra un mensaje o realiza alguna acción
                holder.adreca.setText("Adreça no trobada");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            holder.adreca.setText("Error en obtenir l'adreça");
        }


       // holder.adreca.setText(arrayList.get(posicio).getAdreca());

        //Segons el tipus que sigui, hi posa un icona o altre
        if (arrayList.get(posicio).getTipus().equalsIgnoreCase("Font")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_font);

        } else if (arrayList.get(posicio).getTipus().equalsIgnoreCase("lavabo")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_lavabo);

        } else if (arrayList.get(posicio).getTipus().equalsIgnoreCase("contenidor")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_contenidor);

        } else {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_picnic);
        }

        //Anima la forma en que es mostra al perfil
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //El model a mostrar te una imatge de com es i una del tipus
        //Un text que diu de quin tipus es, i l'adreca del punt on esta localitada
        ImageView imageView, iv_foto_tipus;
        TextView tipus, adreca;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_cvi_perfil);
            tipus = itemView.findViewById(R.id.tv_cvi_perfil_tipus);
            adreca = itemView.findViewById(R.id.tv_cvi_perfil_subtitol);
            iv_foto_tipus = itemView.findViewById(R.id.iv_tipus_cv);


            LinearLayout cardviewPerfil = itemView.findViewById(R.id.cardviewperfil);

            cardviewPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Model_ItemCardPerfil item = arrayList.get(position);
                        onEditButtonClick(item);
                    }
                }
            });


          //  animar(itemView);
        }
    }


    private void onEditButtonClick(Model_ItemCardPerfil item) {

        //Segons el tipus que sigui, hi posa un icona o altre
        if (item.getTipus().equalsIgnoreCase("Font")) {

            EditFontFragment fragment = EditFontFragment.newInstance(item.getFont());
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.perfilfra, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (item.getTipus().equalsIgnoreCase("Lavabo")) {

            EditLavaboFragment fragment = EditLavaboFragment.newInstance(item.getLavabo());
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.perfilfra, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (item.getTipus().equalsIgnoreCase("Contenidor")) {
            EditContenidorFragment fragment = EditContenidorFragment.newInstance(item.getContenidor());
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.perfilfra, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            EditPicnicFragment fragment = EditPicnicFragment.newInstance(item.getPicnic());
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.perfilfra, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }



    public void animar(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.setAnimation(animation);
    }

    public void iniciar() {
        //MyUtils.toast(context, "araaar" + arrayList.size());
        arrayList.clear();

        arrayList.addAll(llistaOriginal);
        //MyUtils.toast(context, "arr" + arrayList.size());
        notifyDataSetChanged();
    }

    public void filtrar(String textBuscar) {

        if (textBuscar.isEmpty()) {
            arrayList.clear();
            arrayList.addAll(llistaOriginal);
        } else {
            List<Model_ItemCardPerfil> filtrada;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filtrada = llistaOriginal.stream()
                        .filter(i -> i.getTipus().toLowerCase().contains(textBuscar.toLowerCase()) ||
                                i.getAdreca(geocoder).toLowerCase().contains(textBuscar.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                filtrada = new ArrayList<>();
                for (Model_ItemCardPerfil m : llistaOriginal) {
                    if (m.getTipus().toLowerCase().contains(textBuscar.toLowerCase()) ||
                            m.getAdreca(geocoder).toLowerCase().contains(textBuscar.toLowerCase())) {
                        filtrada.add(m);
                    }
                }
            }
            arrayList.clear();
            arrayList.addAll(filtrada);
        }
        notifyDataSetChanged();
    }
}