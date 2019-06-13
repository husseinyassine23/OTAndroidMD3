package com.ids.fixot.adapters;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockOrderBook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 3/30/2017.
 */

public class StockOrderBookRecyclerAdapter extends RecyclerView.Adapter<StockOrderBookRecyclerAdapter.ViewHolder> {

    private ArrayList<StockOrderBook> allOrders;
    private Activity context;
    double maxAsk = 0.0;
    double maxBid = 0.0;

    public StockOrderBookRecyclerAdapter(Activity context, ArrayList<StockOrderBook> allOrders) {
        this.context = context;
        this.allOrders = allOrders;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);
        }
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }


    private class ItemViewHolder extends ViewHolder {

        TextView tvAskNumberItem, tvAskQtyItem, tvPriceItem, tvBidQtyItem, tvBidNumberItem;
        LinearLayout llItem;
        protected View v;
        protected View separator;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.separator =  v.findViewById(R.id.separator);
            this.llItem =  v.findViewById(R.id.llItem);
            this.tvAskNumberItem = v.findViewById(R.id.tvAskNumberItem);
            this.tvPriceItem =   v.findViewById(R.id.tvPriceItem);
            this.tvAskQtyItem = v.findViewById(R.id.tvAskQtyItem);
            this.tvBidQtyItem =  v.findViewById(R.id.tvBidQtyItem);
            this.tvBidNumberItem =  v.findViewById(R.id.tvBidNumberItem);
        }

    }


    @Override
    public StockOrderBookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_item, viewGroup, false);
        return new StockOrderBookRecyclerAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        StockOrderBook order = allOrders.get(position);
        StockOrderBook maxAskStock, maxBidStock;

        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                maxAskStock =  Collections.max(allOrders.subList(0, allOrders.size()-1), Comparator.comparing(s -> s.getAskValue()));
                maxBidStock =  Collections.max(allOrders.subList(0, allOrders.size()-1), Comparator.comparing(s -> s.getBidValue()));

                maxAsk = maxAskStock.getAskValue();
                maxBid = maxBidStock.getBidValue();

            }else{

                maxAskStock = new StockOrderBook();
                maxBidStock = new StockOrderBook();

                for (int i = 0; i < allOrders.size()-1; i ++){

                    if (allOrders.get(i).getAskValue() > maxAskStock.getAskValue()){

                        maxAskStock = allOrders.get(i);
                    }

                    if (allOrders.get(i).getBidValue() > maxBidStock.getBidValue()){

                        maxBidStock = allOrders.get(i);
                    }

                }

                maxAsk = maxAskStock.getAskValue();
                maxBid = maxBidStock.getBidValue();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvAskNumberItem.setText(order.getAsk() );
        holder.tvAskQtyItem.setText(order.getAskQuantity());
        holder.tvPriceItem.setText(order.getPrice());
        holder.tvBidNumberItem.setText(order.getBid());
        holder.tvBidQtyItem.setText(order.getBidQuantity());

        if (order.getAskQuantity().length() > 0 && order.getBidQuantity().length() > 0){ //all gray

            holder.separator.setVisibility(View.GONE);
            holder.tvAskQtyItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorValues  : R.color.colorValuesInv));
            holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorValues  : R.color.colorValuesInv));

            holder.tvBidNumberItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorValues  : R.color.colorValuesInv));
            holder.tvBidQtyItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorValues  : R.color.colorValuesInv));

            holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.gray  : R.color.grayInv));


        }else {

            holder.separator.setVisibility(View.VISIBLE);
            if (position % 2 == 1){

                if (order.getAskQuantity().length() > 0){

                    double askPercentage = (order.getAskValue() * 100) / maxAsk;
                    if (askPercentage < 10.0){
                        askPercentage = askPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(askPercentage == 100 ? ContextCompat.getColor(context, R.color.red_color) : ContextCompat.getColor(context, R.color.light_red_color));
                    double width = MyApplication.screenWidth * ((askPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)width, 2);
                    params.gravity = Gravity.START;
                    holder.separator.setLayoutParams(params);

                    if (order.getAskQuantity().equals("0")){
                        holder.separator.setVisibility(View.GONE);
                    }else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.red_color  : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.odd_red_color));
                }else{


                    double bidPercentage = (order.getBidValue() * 100) / maxBid;
                    if (bidPercentage < 10.0){
                        bidPercentage = bidPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(bidPercentage == 100 ? ContextCompat.getColor(context, R.color.green_color) : ContextCompat.getColor(context, R.color.light_green_color));
                    double width = MyApplication.screenWidth * ((bidPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)width, 2);
                    params.gravity = Gravity.END;
                    holder.separator.setLayoutParams(params);

                    if (order.getBidQuantity().equals("0")){
                        holder.separator.setVisibility(View.GONE);
                    }else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }


                    //holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?
                            R.color.green_color  : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.odd_green_color));
                }
            }
            else{

                if (order.getAskQuantity().length() > 0){

                    double askPercentage = (order.getAskValue() * 100) / maxAsk;
                    if (askPercentage < 10.0){
                        askPercentage = askPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(askPercentage == 100 ? ContextCompat.getColor(context, R.color.red_color) : ContextCompat.getColor(context, R.color.light_red_color));
                    double width = MyApplication.screenWidth * ((askPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)width, 2);
                    params.gravity = Gravity.START;
                    holder.separator.setLayoutParams(params);

                    if (order.getAskQuantity().equals("0")){
                        holder.separator.setVisibility(View.GONE);
                    }else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?
                            R.color.red_color  : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.even_red_color));
                }else{

                    double bidPercentage = (order.getBidValue() * 100) / maxBid;
                    if (bidPercentage < 10.0){
                        bidPercentage = bidPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(bidPercentage == 100 ? ContextCompat.getColor(context, R.color.green_color) : ContextCompat.getColor(context, R.color.light_green_color));
                    double width = MyApplication.screenWidth * ((bidPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)width, 2);
                    params.gravity = Gravity.END;
                    holder.separator.setLayoutParams(params);

                    if (order.getBidQuantity().equals("0")){
                        holder.separator.setVisibility(View.GONE);
                    }else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    //holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.green_color  : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.even_green_color));
                }
            }


        }

        Actions.overrideFonts(context, holder.llItem, false);
        Actions.setTypeface(new TextView[]{holder.tvAskNumberItem, holder.tvAskQtyItem,  holder.tvPriceItem, holder.tvBidNumberItem, holder.tvBidQtyItem}, MyApplication.giloryBold);

    }

    @Override
    public int getItemCount() {
        return allOrders.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}
