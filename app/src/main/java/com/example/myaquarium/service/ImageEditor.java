package com.example.myaquarium.service;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.myaquarium.R;
import com.squareup.picasso.Picasso;

public class ImageEditor {
    public static Button editAddedImage(Uri uri, Context context, ImageView img) {

        Picasso.get().load(uri).resize(150, 150).centerCrop().into(img);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                150,
                150
        );
        lp.setMargins(0,0,0,5);

        Button button = new Button(context);
        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                70
        );

        lpBtn.setMargins(30, 0, 0, 0);
        button.setText("удалить");
        button.setTextSize(10);
        button.setTextColor(Color.WHITE);
        button.setLayoutParams(lpBtn);
        button.setPadding(0,0,0,0);
        button.setBackgroundResource(R.color.bthAll);

        img.setLayoutParams(lp);

        return button;
    }

    public static LinearLayout editLayoutImage(
            Context context,
            Button button,
            ImageView img
    ) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(img);
        layout.addView(button);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                150,
                150
        );
        lp.setMargins(0,0,0,5);
        img.setLayoutParams(lp);

        return layout;
    }
}
