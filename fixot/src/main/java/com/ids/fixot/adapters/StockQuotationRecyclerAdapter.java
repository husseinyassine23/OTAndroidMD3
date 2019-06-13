package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;

/**
 * Created by user on 3/29/2017.
 */

public class StockQuotationRecyclerAdapter extends RecyclerView.Adapter<StockQuotationRecyclerAdapter.ItemViewHolder> implements Filterable {


    private ArrayList<StockQuotation> allStocks;
    private ArrayList<StockQuotation> filteredStocks;
    private ItemFilters itemFilters = new ItemFilters();
    private Activity context;


    public StockQuotationRecyclerAdapter(Activity context, ArrayList<StockQuotation> allStocks) {

        this.context = context;
        this.allStocks = allStocks;
        this.filteredStocks = allStocks;
    }

    @Override
    public Filter getFilter() {
        return itemFilters;
    }

    public ArrayList<StockQuotation> getFilteredItems() {
        return filteredStocks;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final StockQuotation stockQuotation = filteredStocks.get(position);
        String stockID = stockQuotation.getSecurityId() + ""; // getStockId
        String stockChange = stockQuotation.getChange() + "";
        String stockAmount = stockQuotation.getLast() + "";

        String stockHigh = stockQuotation.getBid() + "";
        String stockLow = stockQuotation.getAsk() + "";
        String session = MyApplication.lang == MyApplication.ENGLISH ? stockQuotation.getSessionNameEn() : stockQuotation.getSessionNameAr();

        holder.tvStockId.setText(stockID);
        if (MyApplication.lang == MyApplication.ARABIC) {
            holder.tvStockSymbol.setText(stockQuotation.getSymbolAr());
            holder.tvStockName.setText(stockQuotation.getNameAr());

        } else {
            holder.tvStockSymbol.setText(stockQuotation.getSymbolEn());
            holder.tvStockName.setText(stockQuotation.getNameEn());
        }


        try {
            //holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stockChange), Actions.OneDecimal));
            holder.tvChange.setText(stockChange);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exc", "exc");
            holder.tvChange.setText(stockChange);
        }

        /*if (Double.parseDouble(stockChange) == 0){

            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        }else if (Double.parseDouble(stockChange) > 0){


            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.green_color));
        }else{

            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));
        }*/

        holder.tvChange.setBackgroundColor(Actions.textColor(stockChange));

        holder.tvPrice.setText(stockAmount);
        holder.tvHigh.setText(stockHigh);
        holder.tvLow.setText(stockLow);
        holder.tvSession.setText(session);

        holder.tvStockId.setTypeface(MyApplication.giloryBold);

        holder.btTrades.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putParcelable("stockQuotation", stockQuotation);
            Intent i = new Intent(context, TradesActivity.class);
            i.putExtras(b);
            context.startActivity(i);
        });

        holder.rllayout.setOnClickListener(v -> {
            Intent i = new Intent(context, StockDetailActivity.class);
            i.putExtra("stockID", stockQuotation.getStockID());
            context.startActivity(i);
        });

        holder.rllayout.setOnLongClickListener(view -> {
            showDialog(stockQuotation);
            return true;
        });

        //<editor-fold desc="layout coloring">
        if (stockQuotation.isChanged()) {
            Log.wtf("StockQuotationRecyclerAdapter",stockQuotation.getSymbolEn() + " isChanged");

            holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            new CountDownTimer(2000, 50) {

                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {

                    stockQuotation.setChanged(false);
//                    if (position % 2 == 1)
//                        holder.rllayout.setBackgroundColor( ContextCompat.getColor(context, R.color.colorLight));
//                    else
//                        holder.rllayout.setBackgroundColor( ContextCompat.getColor(context, R.color.white));
                    if (position%2 == 0 ){
                        holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme) );
                    }else{
                        holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv) );
                    }
                }
            }.start();

        } else {
//            if (position % 2 == 1)
//                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//            else
//                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            if (position%2 == 0 ){
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme) );
            }else{
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv) );
            }
        }

        if (stockQuotation.getSessionId() == null)
            stockQuotation.setSessionId("");

        switch (stockQuotation.getSessionId()) {

            case MyApplication.CIRCUIT_BREAKER:
                holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                holder.btTrades.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;

            default: //Trading

//                if (position % 2 == 1)
//                    holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//                else
//                    holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        }
        //</editor-fold>

        Actions.overrideFonts(context, holder.rllayout, false);
        holder.btTrades.setTypeface(MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{holder.tvStockId, holder.tvPrice, holder.tvChange}, MyApplication.giloryBold );
        Actions.setTypeface(new TextView[]{holder.tvStockSymbol, holder.tvSession},
                MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);

//        holder.tvSession.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);
    }

    private void showDialog(StockQuotation stockQuotation){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList<String> options = new ArrayList<>();
        options.add(context.getResources().getString(R.string.stock_details));
        options.add(context.getResources().getString(R.string.trades_title));
        options.add(context.getResources().getString(R.string.order_book));

        String[] items = {context.getResources().getString(R.string.stock_details), context.getResources().getString(R.string.trades_title), context.getResources().getString(R.string.order_book)
                , context.getResources().getString(R.string.buy), context.getResources().getString(R.string.sell)};

        builder.setItems(items, (dialog, which) -> {
            switch (which) {

                case 0: //stock details
                    Intent i = new Intent(context, StockDetailActivity.class);
                    i.putExtra("stockID", stockQuotation.getStockID());
                    context.startActivity(i);
                    break;

                    case 1: // trades title

                        context.startActivity(new Intent(context, TimeSalesActivity.class)
                                .putExtra("stockId", stockQuotation.getStockID())
                                .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 2: // order book

                    context.startActivity(new Intent(context, StockOrderBookActivity.class)
                            .putExtra("stockId", stockQuotation.getStockID())
                            .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 3: // buy

                    Bundle buyBundle = new Bundle();
                    buyBundle.putParcelable("stockQuotation", stockQuotation);
                    buyBundle.putInt("action", MyApplication.ORDER_BUY);
                    Intent buyIntent = new Intent(context, TradesActivity.class);
                    buyIntent.putExtras(buyBundle);
                    context.startActivity(buyIntent);
                    break;

                case 4: // sell

                    Bundle sellBundle = new Bundle();
                    sellBundle.putParcelable("stockQuotation", stockQuotation);
                    sellBundle.putInt("action", MyApplication.ORDER_SELL);
                    Intent sellIntent = new Intent(context, TradesActivity.class);
                    sellIntent.putExtras(sellBundle);
                    context.startActivity(sellIntent);
                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return filteredStocks.size();
    }

    private class ItemFilters extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<StockQuotation> list = allStocks;

            int count = list.size();
            final ArrayList<StockQuotation> nlist = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {

                if (MyApplication.instrumentId.length() > 0) {

                    if (list.get(i).getInstrumentId().equals(MyApplication.instrumentId)) {

                        filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                                + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                        if (filterableString.toLowerCase().contains(filterString)) {
                            nlist.add(list.get(i));
                        }
                    }
                } else {

                    filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                            + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredStocks = (ArrayList<StockQuotation>) results.values;
            notifyDataSetChanged();
        }

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvStockId, tvStockSymbol, tvStockName, tvLow, tvPrice, tvHigh, tvChange, tvSession;
        Button btTrades;
        LinearLayout rllayout; // RelativeLayout // LinearLayout
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.btTrades = (Button) v.findViewById(R.id.btTrades);
            this.tvStockId = (TextView) v.findViewById(R.id.tvStockId);
            this.tvStockSymbol = (TextView) v.findViewById(R.id.tvStockSymbol);
            this.tvStockName = (TextView) v.findViewById(R.id.tvStockName);
            this.tvLow = (TextView) v.findViewById(R.id.tvLow);
            this.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
            this.tvHigh = (TextView) v.findViewById(R.id.tvHigh);
            this.tvChange = (TextView) v.findViewById(R.id.tvChange);
            this.tvSession = (TextView) v.findViewById(R.id.tvSession);
            this.rllayout = v.findViewById(R.id.rllayout);
        }
    }
}