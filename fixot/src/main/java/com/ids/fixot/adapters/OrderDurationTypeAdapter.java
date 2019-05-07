package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.OrderDurationType;

import java.util.ArrayList;

/**
 * Created by user on 10/4/2017.
 */

public class OrderDurationTypeAdapter extends RecyclerView.Adapter<OrderDurationTypeAdapter.MyViewHolder> {

    private ArrayList<OrderDurationType> dataSet;
    private Context jcontext;
    private RecyclerViewOnItemClickListener itemClickListener;
    int selectedPosition ;


    public OrderDurationTypeAdapter(Context scontext, ArrayList<OrderDurationType> data,RecyclerViewOnItemClickListener mitemClickListener , int selectedPos) {
        this.dataSet = data;
        this.jcontext=scontext;
        this.itemClickListener=mitemClickListener;
        this.selectedPosition = selectedPos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textVtext = holder.textViewtext;

        if (MyApplication.lang == MyApplication.ARABIC){
            textVtext.setText(dataSet.get(position).getDescriptionAr());
            if(position == selectedPosition){
                textVtext.setTypeface(MyApplication.droidbold);
            }
            else{
                textVtext.setTypeface(MyApplication.droidregular);
            }
        }else{
            textVtext.setText(dataSet.get(position).getDescriptionEn());
//            textVtext.setTypeface(MyApplication.giloryItaly);
            if(position == selectedPosition){
                textVtext.setTypeface(MyApplication.giloryBold);
            }
            else{
                textVtext.setTypeface(MyApplication.giloryItaly);
            }
        }

        switch (position){

            case 1:
                textVtext.setTextColor(Color.BLACK);
                textVtext.setTextColor(ContextCompat.getColor(jcontext, MyApplication.mshared.getBoolean(jcontext.getResources().getString(R.string.normal_theme), true) ?  R.color.black : R.color.white));
            case 5:
                textVtext.setTextColor(Color.BLACK);
                textVtext.setTextColor(ContextCompat.getColor(jcontext, MyApplication.mshared.getBoolean(jcontext.getResources().getString(R.string.normal_theme), true) ?  R.color.black  : R.color.white));
                break;

            default:
                if (!isEnabled(position))
                    textVtext.setTextColor(Color.GRAY);
                else
//                    textVtext.setTextColor(Color.BLACK);
                textVtext.setTextColor(ContextCompat.getColor(jcontext, MyApplication.mshared.getBoolean(jcontext.getResources().getString(R.string.normal_theme), true) ? R.color.black  : R.color.white));

                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size() ;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewtext;
        private View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            this.textViewtext = (TextView) itemView.findViewById(R.id.tvItem);
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public boolean isEnabled(int position) {
        return position == 1 || position == 5 || Actions.isMarketOpen();
    }


}