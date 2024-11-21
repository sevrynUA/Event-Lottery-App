package com.example.celery_sticks.ui.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.celery_sticks.R;
import com.example.celery_sticks.User;

import java.util.ArrayList;
import java.util.Date;


public class ImageArrayAdapter extends ArrayAdapter<String[]> {

    public ImageArrayAdapter(Context context, ArrayList<String[]> data) {super (context, 0, data); }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_browse_images_content, parent, false);
        } else {
            view = convertView;
        }
        // string[] type has the following info in indices 0-3: type, title, id, imageData
        String[] instance = getItem(position);

        TextView type = view.findViewById(R.id.image_type);
        TextView title = view.findViewById(R.id.image_title);
        ImageView image = view.findViewById(R.id.admin_browse_image_view);

        type.setText(instance[0]);
        title.setText(instance[1]);

        // set image to imageview here
        if (instance[3] != null) {
            if (!instance[3].equals("")) {

                byte[] decodedImage = Base64.decode(instance[3], Base64.DEFAULT);

                Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                // set qrImage to decoded bitmap
                image.setImageBitmap(qrBitmap);
            }
        }

        return view;
    }
}
