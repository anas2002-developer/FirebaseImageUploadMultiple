package com.anas.firebaseimageuploadmultiple;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.w3c.dom.Text;

import java.util.List;

public class Adapter_Image extends RecyclerView.Adapter<Adapter_Image.Image_ViewHolder> {

    List<String> files,status;

    public Adapter_Image(List<String> files, List<String> status) {
        this.files = files;
        this.status = status;
    }


    @NonNull
    @Override
    public Image_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row,parent,false);
        return new Image_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Image_ViewHolder holder, int position) {
        String image_name,image_status;

        image_name = files.get(position);
        if (image_name.length()>15){
            image_name=image_name.substring(0,15)+"...";
        }

        holder.txtImage_name.setText(image_name);

        image_status = status.get(position);
        if (image_status.equals("loading")){
            holder.imgImage_loading.setImageResource(R.drawable.loading);
        }
        else {
            holder.imgImage_loading.setImageResource(R.drawable.check);
        }

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class Image_ViewHolder extends RecyclerView.ViewHolder {


        ImageView imgImage_logo,imgImage_loading;
        TextView txtImage_name;

        public Image_ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgImage_logo = itemView.findViewById(R.id.imgImage_logo);
            imgImage_loading = itemView.findViewById(R.id.imgImage_loading);
            txtImage_name = itemView.findViewById(R.id.txtImage_name);
        }
    }
}
