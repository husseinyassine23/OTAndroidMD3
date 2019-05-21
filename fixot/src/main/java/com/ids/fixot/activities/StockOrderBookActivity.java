package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.OrderBookRecyclerAdapter;
import com.ids.fixot.adapters.StockOrderBookRecyclerAdapter;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.StockOrderBook;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by user on 3/30/2017.
 */

public class StockOrderBookActivity extends AppCompatActivity implements OrderBookRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    private BroadcastReceiver receiver;


    RecyclerView rvOrders;
    LinearLayoutManager llm;
    StockOrderBookRecyclerAdapter adapter;
    LinearLayout rootLayout;
    private ArrayList<StockOrderBook> allOrders = new ArrayList<>();
    private boolean started = false;
    int stockId = 0,ii=0;
    GetStockOrderBook getStockOrderBook;

    StockQuotation stock = new StockQuotation();
    boolean isFavorite = false , running = true;
    ImageView ivFavorite;

    TextView tvAskNumberHeader, tvAskQtyHeader, tvPriceHeader, tvBidNumberHeader, tvBidQtyHeader, tvStockTitle, tvStockName;

    public StockOrderBookActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void refreshMarketTime(String status,String time,Integer color){

        final TextView marketstatustxt = findViewById(R.id.market_state_value_textview);
        final LinearLayout llmarketstatus = findViewById(R.id.ll_market_state);
        final TextView markettime =  findViewById(R.id.market_time_value_textview);

        marketstatustxt.setText(status);
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this,color));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_stock_order_book);
        Actions.initializeBugsTracking(this);


        started = true;
        findViews();

        if (getIntent().hasExtra("stockId")){
            stockId = getIntent().getExtras().getInt("stockId");
            String barTitle = getString(R.string.order_book) +"-"+getIntent().getExtras().getString("stockName");
            Actions.initializeToolBar(barTitle, StockOrderBookActivity.this);
          //  tvStockTitle.setVisibility(View.GONE);

            stock = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockId"));
            stock.setStockID(getIntent().getExtras().getInt("stockId"));

            if(getIntent().getExtras().getString("isFavorite")!= null){
                isFavorite = getIntent().getExtras().getString("isFavorite").equals("1") ? true : false ;
            }

            Log.wtf("getIntent().getExtras().getInt(\"isFavorite\")" , "is " + getIntent().getExtras().getInt("isFavorite"));
            Log.wtf("getIntent().getExtras().getString(\"isFavorite\")" , "is " + getIntent().getExtras().getString("isFavorite"));
            ivFavorite.setImageResource(isFavorite ? R.drawable.added_to_favorites : R.drawable.add_to_favorites );

            setStockName(getIntent().getExtras().getString("securityId") + " - " +  getIntent().getExtras().getString("stockName")); //getInt("stockId")
        }else{
            Actions.initializeToolBar(getString(R.string.order_book), StockOrderBookActivity.this);
        }

        Actions.showHideFooter(this);

        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.setTypeface(new TextView[]{tvAskNumberHeader, tvAskQtyHeader,  tvAskNumberHeader, tvPriceHeader, tvBidNumberHeader, tvBidQtyHeader,   tvStockName},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

    }

    private void setStockName(String stockName) {
        tvStockName.setText(stockName);
        if (MyApplication.lang == MyApplication.ARABIC) {
            tvStockName.setTypeface(MyApplication.droidbold);
        } else {
            tvStockName.setTypeface(MyApplication.giloryBold);
        }
    }


    private void findViews(){

        rvOrders = findViewById(R.id.rvOrders);
        rootLayout =  findViewById(R.id.rootLayout);

        tvAskNumberHeader =  findViewById(R.id.tvAskNumberHeader);
        tvAskQtyHeader = findViewById(R.id.tvAskQtyHeader);
        tvPriceHeader =  findViewById(R.id.tvPriceHeader);
        tvBidNumberHeader = findViewById(R.id.tvBidNumberHeader);
        tvBidQtyHeader =   findViewById(R.id.tvBidQtyHeader);
      //  tvStockTitle = findViewById(R.id.market_time_value_textview);

        tvStockName = findViewById(R.id.stockName);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> new StockOrderBookActivity.AddRemoveFavoriteStock().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
    }

    private class AddRemoveFavoriteStock extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(StockOrderBookActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddFavoriteStocks.getValue();

            if (isFavorite) {
                url = MyApplication.link + MyApplication.RemoveFavoriteStocks.getValue();
            }

            String stringer = "{\"StockIDs\":[\"" + stock.getStockID() + "\"],\"UserID\":" + MyApplication.currentUser.getId()
                    + ",\"key\":\"" + getString(R.string.beforekey) + "\"}";

            result = ConnectionRequests.POSTWCF2(url, stringer);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String success = object.getString("MessageEn");
                if (success.equals("Success")) {

                    if (!isFavorite) {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.save_success), false, false);
                        ivFavorite.setImageResource(R.drawable.added_to_favorites);
                        isFavorite = true;
                    } else {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.delete_success), false, false);
                        ivFavorite.setImageResource(R.drawable.add_to_favorites);
                        isFavorite = false;
                    }
                } else {
                    Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.error), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();


                if(MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isFavorite) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.RemoveFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }

    public void goToTrade(View v) {

        switch (v.getId()) {

            case R.id.btSell:

                Bundle sellBundle = new Bundle();
                sellBundle.putParcelable("stockQuotation", stock);
                sellBundle.putInt("action", MyApplication.ORDER_SELL);
                Intent sellIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                sellIntent.putExtras(sellBundle);
                StockOrderBookActivity.this.startActivity(sellIntent);
                break;

            case R.id.btBuy:

                Bundle buyBundle = new Bundle();
                buyBundle.putParcelable("stockQuotation", stock);
                buyBundle.putInt("action", MyApplication.ORDER_BUY);
                Intent buyIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                buyIntent.putExtras(buyBundle);
                StockOrderBookActivity.this.startActivity(buyIntent);
                break;
        }

    }

    private void setListeners(){

        llm = new LinearLayoutManager(StockOrderBookActivity.this);
        adapter = new StockOrderBookRecyclerAdapter(StockOrderBookActivity.this, allOrders);
        rvOrders.setLayoutManager(llm);
        rvOrders.setAdapter(adapter);

    }

    public void loadFooter(View v){

        Actions.loadFooter(this, v);
    }

    public void back(View v){

        finish();
    }

    public void close(View v) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getStockOrderBook.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getStockOrderBook);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("getStockOrderBook ex", e.getMessage());
        }

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }
    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        Actions.InitializeSessionServiceV2(this);
     //   Actions.InitializeMarketServiceV2(this);


        running = true;
        Log.wtf("Orders Activity OnResume","Actions.isNetworkAvailable(this) : " + Actions.isNetworkAvailable(this) + " / running = " + running);
        if (Actions.isNetworkAvailable(this)){
            getStockOrderBook = new GetStockOrderBook();
            getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);
        }else{
            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }


    @Override
    public void onItemClicked(View v, int position) {

    }

    private class GetStockOrderBook extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(StockOrderBookActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetStockOrderBook.getValue(); // this method uses key after login

            while (running){
                Log.wtf("Thread" , "runnig " + ii);
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("stockId", "" + stockId);
                parameters.put("key", MyApplication.currentUser.getKey());

                Log.wtf("GetStockOrderBook","GetStockOrderBook");
                try {
                    result = ConnectionRequests.GET(url, StockOrderBookActivity.this, parameters);

                    publishProgress(result);

                } catch (Exception e) {
                    e.printStackTrace();
                    if(MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetStockOrderBook.getKey(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                allOrders.clear();

                allOrders.addAll(GlobalFunctions.GetStockOrderBooks(values[0]));
                runOnUiThread(() -> adapter.notifyDataSetChanged());

                MyApplication.dismiss();

            }catch (Exception e){
                e.printStackTrace();
            }
            Log.wtf("stocksSize", allOrders.size() + "");
            adapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
