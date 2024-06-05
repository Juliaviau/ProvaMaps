package com.example.provamaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModelRecyclerView extends RecyclerView.Adapter<ModelRecyclerView.ViewHolder> {

    Context context;
    ArrayList<Model_ItemCardPerfil> arrayList = new ArrayList<>();

    public ModelRecyclerView(Context context, ArrayList<Model_ItemCardPerfil> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_perfil,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelRecyclerView.ViewHolder holder, int position) {
        holder.imageView.setImageResource(arrayList.get(position).getImg());
        holder.tipus.setText(arrayList.get(position).getTipus());
        holder.adreca.setText(arrayList.get(position).getAdreca());



        if (arrayList.get(position).getTipus().equalsIgnoreCase("Font")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_font);

        } else if (arrayList.get(position).getTipus().equalsIgnoreCase("lavabo")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_lavabo);

        } else if (arrayList.get(position).getTipus().equalsIgnoreCase("contenidor")) {
            holder.iv_foto_tipus.setImageResource(R.drawable.icona_contenidor);

        } else {


            holder.iv_foto_tipus.setImageResource(R.drawable.icona_picnic);


        }




    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, iv_foto_tipus;
        TextView tipus, adreca;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_cvi_perfil);
            tipus = itemView.findViewById(R.id.tv_cvi_perfil_tipus);
            adreca = itemView.findViewById(R.id.tv_cvi_perfil_subtitol);
            iv_foto_tipus = itemView.findViewById(R.id.iv_tipus_cv);
            animar(itemView);
        }
    }

    public void animar(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.setAnimation(animation);
    }

}
