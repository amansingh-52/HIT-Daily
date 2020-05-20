/*package com.example.hitdaily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    ArrayList<String> mSubject = new ArrayList<>();
    ArrayList<String> mClassNumber = new ArrayList<>();
    ArrayList<String> mPercentage = new ArrayList<>();
    ArrayList<String> mP_cardView = new ArrayList<>();
    ArrayList<String> mA_cardView = new ArrayList<>();
    Context mContext;

    public RecyclerViewAdaptor(ArrayList<String> mSubject, ArrayList<String> mClassNumber, ArrayList<String> mPercentage, ArrayList<String> mP_cardView, ArrayList<String> mA_cardView, Context mContext) {
        this.mSubject = mSubject;
        this.mClassNumber = mClassNumber;
        this.mPercentage = mPercentage;
        this.mP_cardView = mP_cardView;
        this.mA_cardView = mA_cardView;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate()
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView{

        TextView subject;
        TextView classNumber;
        TextView percentage;
        CardView P_cardView;
        CardView A_cardView;

        public ViewHolder(@NonNull Context context) {
            super(context);

        }
    }
}
*/
