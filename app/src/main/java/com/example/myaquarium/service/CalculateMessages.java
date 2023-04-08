package com.example.myaquarium.service;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.myaquarium.R;

public class CalculateMessages {

    public static void setMessage(View inflatedView, int titleId, int messageId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                inflatedView.getContext(),
                R.style.AlertDialogStyle
        );
        dialog.setTitle(titleId);
        dialog.setMessage(messageId);

        dialog.setPositiveButton("Закрыть", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }
}
