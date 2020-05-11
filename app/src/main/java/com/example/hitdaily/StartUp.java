package com.example.hitdaily;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.myapplication.R;

public class StartUp {
    Activity activity;
    AlertDialog dialog;
    StartUp(Activity mActivity){
        activity = mActivity;
    }

    void StartLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.Theme_AppCompat_Dialog);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        dialog  = builder.create();
        dialog.show();
    }

    void dismisDialog(){
        dialog.dismiss();
    }
}
