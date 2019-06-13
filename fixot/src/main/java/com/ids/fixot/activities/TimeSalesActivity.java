package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import com.ids.fixot.adapters.InstrumentsAdapter;
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.adapters.TimeSalesRecyclerAdapter;
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.enums.enums.TradingSession;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.TimeSale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user on 7/25/2017.
 */

public class TimeSalesActivity extends AppCompatActivity implements InstrumentsRecyclerAdapter.RecyclerViewOnItemClickListener , MarketStatusListener {

    private BroadcastReceiver receiver;


    public TimeSalesActivity() {
        LocalUtils.updateConfig(this);
    }

    TextView tvStockHeader, tvTypeHeader, tvPriceHeader, tvQuantityHeader, tvChangeHeader, tvTimeHeader;

    Toolbar myToolbar;
    RelativeLayout rootLayout;
    RecyclerView rvTrades;
    TextView tvToolbarTitle, tvToolbarStatus;
    ImageView ivBack;
    LinearLayoutManager llm;
    TimeSalesRecyclerAdapter adapter;
    private boolean started = false;
    ArrayList<TimeSale> allTrades = new ArrayList<>();
    ArrayList<TimeSale> tmpTrades = new ArrayList<>();
    GetTimeSales getTimeSales;
    private boolean running = true;
    int stockId = 0;
    boolean isFromStockDetails = true;

    RecyclerView rvInstruments;
    InstrumentsRecyclerAdapter instrumentsRecyclerAdapter;
    Spinner spMarkets;
    MarketsSpinnerAdapter marketsSpinnerAdapter;
    TradingSession selectMarket = TradingSession.All ;
    ArrayList<TradingSession> AllMarkets = new ArrayList<>();
    ArrayList<Instrument> marketInstruments = new ArrayList<>();
    ArrayList<Instrument> allInstruments = new ArrayList<>();
    Boolean isSelectInstrument = false;

    Instrument selectedInstrument = new Instrument();
    String instrumentId = "";
    GetInstruments getInstruments;
    boolean firstTabClick = true, connected = false;
    LinearLayout llTab;

    SqliteDb_TimeSales timeSales_DB;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_time_sales);
        Actions.initializeBugsTracking(this);

        timeSales_DB = new SqliteDb_TimeSales(this);
        timeSales_DB.open();
        MyApplication.timeSales = timeSales_DB.getAllTimeSales();
       // MyApplication.timeSalesTimesTamp="0";
        timeSales_DB.close();
        Log.wtf("TimeSalesActivity onCreat","MyApplication.timeSales = " + MyApplication.timeSales.size());

        if(MyApplication.lang == MyApplication.ARABIC){
            AllMarkets.add(TradingSession.All_ar);
            AllMarkets.add(TradingSession.REG_ar);
            AllMarkets.add(TradingSession.FUNDS_ar);
        }
        else{
            AllMarkets.add(TradingSession.All);
            AllMarkets.add(TradingSession.REG);
            AllMarkets.add(TradingSession.FUNDS);
        }

        MyApplication.instrumentId = "";
        getInstruments = new GetInstruments();

        Log.wtf("onCreate","MyApplication.instruments count: " + MyApplication.instruments.size());
        if (MyApplication.instruments.size() < 2) {

            Actions.initializeInstruments(this);
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            allInstruments.clear();

            if(!MyApplication.isOTC) {
                for (int i = 1; i < AllMarkets.size(); i++) {
                    allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
                }
            }else{
                allInstruments.addAll(MyApplication.instruments);
            }

           
        }


        findViews();
        Actions.initializeToolBar(getString(R.string.trades), TimeSalesActivity.this);

        started = true;
        allTrades.clear();

        if (getIntent().hasExtra("stockId")) {
            isFromStockDetails = true;
            stockId = getIntent().getExtras().getInt("stockId");
            allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, stockId, instrumentId));
            Log.wtf("hasExtra stockId",": " + stockId + " , allStocks count = " + allTrades.size());

            llTab.setVisibility(View.GONE);
        }else{
            //allTrades.addAll(MyApplication.timeSales);
            //tmpTrades = allTrades;

            marketInstruments = allInstruments;
            tmpTrades = (MyApplication.timeSales);
            /*for(int i=0; i<marketInstruments.size(); i++) {
                allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(tmpTrades, 0, marketInstruments.get(i).getInstrumentCode()));
            }*/
            allTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(tmpTrades, 0, marketInstruments));

            Log.wtf("129 allTrades count", ": "+allTrades.size() );
        }

        Log.wtf("timestamp is", ""+MyApplication.timeSalesTimesTamp );
        Log.wtf("onCreat is", "allTrades count = "+allTrades.size() );

        adapter = new TimeSalesRecyclerAdapter(this, allTrades);
        llm = new LinearLayoutManager(TimeSalesActivity.this);
        rvTrades.setLayoutManager(llm);
        rvTrades.setAdapter(adapter);

        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);


        //<editor-fold desc="Instruments Section">
//        tlInstrumentItemsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                if (firstTabClick) {
//
//                    firstTabClick = false;
//                } else {
//
//                    int position = tab.getPosition();
//
//                    Instrument instrument = instrumentsAdapter.getItem(position);
//                    selectedInstrument = instrument;
//
//                    instrumentId = selectedInstrument.getInstrumentCode();
//                    Log.wtf("instrumentId", "" + instrumentId);
//                    allTrades.clear();
//                    adapter.notifyDataSetChanged();
//                    allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, stockId, instrumentId));
//                    adapter.notifyDataSetChanged();
//                    Log.wtf("tempTrades", "size: " + allTrades.size());
//                }
//            }
//        });

        //</editor-fold>

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            connected = false;
        } else {

            connected = true;
        }

        Actions.setTypeface(new TextView[]{tvStockHeader, tvTypeHeader, tvPriceHeader, tvQuantityHeader, tvChangeHeader, tvTimeHeader}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
    }


    public void back(View v) {
        MyApplication.timeSales.clear();
        MyApplication.timeSales = new ArrayList<>();

        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.timeSales.clear();
        MyApplication.timeSales = new ArrayList<>();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
        //Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
       // Actions.InitializeMarketServiceV2(this);

        if (connected) {
            running = true;
            getTimeSales = new GetTimeSales();
            getTimeSales.executeOnExecutor(MyApplication.threadPoolExecutor);
        }

        timeSales_DB = new SqliteDb_TimeSales(this);
        timeSales_DB.open();
        MyApplication.timeSales = timeSales_DB.getAllTimeSales();
        timeSales_DB.close();
        Log.wtf("TimeSalesActivity","MyApplication.timeSales = " + MyApplication.timeSales.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getTimeSales.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getTimeSales);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("getTimeSales cancel ex", e.getMessage());
        }

        try {
            getInstruments.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getInstruments);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("getInstruments cancel ex", e.getMessage());
        }

        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("catch ex", e.getMessage());
        }
    }

    public void loadFooter(View v) {
        MyApplication.timeSales.clear();
        MyApplication.timeSales = new ArrayList<>();
        Actions.loadFooter(this, v);
    }

    private void changeTabsFont(TabLayout tabLayout, Typeface typeface) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {

                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }


    private void findViews() {

        if(MyApplication.isOTC) {
            LinearLayout vs =   findViewById(R.id.spMarketLayout);

            ViewGroup.LayoutParams params = (LinearLayout.LayoutParams) vs.getLayoutParams();

            params.width = 0;

            vs.setVisibility(View.INVISIBLE);
        }


        myToolbar = findViewById(R.id.my_toolbar);
        rootLayout = findViewById(R.id.rootLayout);
        rvTrades = findViewById(R.id.rvTrades);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvToolbarStatus = findViewById(R.id.toolbar_status);
        ivBack = findViewById(R.id.ivBack);
        llTab = findViewById(R.id.llTab);

        tvTimeHeader = findViewById(R.id.tvTimeHeader);
        tvStockHeader = findViewById(R.id.tvStockHeader);
        tvTypeHeader = findViewById(R.id.tvTypeHeader);
        tvPriceHeader = findViewById(R.id.tvPriceHeader);
        tvQuantityHeader = findViewById(R.id.tvQuantityHeader);
        tvChangeHeader = findViewById(R.id.tvChangeHeader);

        rvInstruments =  findViewById(R.id.RV_instrument);
        spMarkets =  findViewById(R.id.spMarket);
        marketsSpinnerAdapter = new MarketsSpinnerAdapter(this, AllMarkets , true) ;
        spMarkets.setAdapter(marketsSpinnerAdapter);
        spMarkets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMarket = AllMarkets.get(position);
                MyApplication.instrumentId = "";
                isSelectInstrument = false;

                if(selectMarket.getValue() == TradingSession.All.getValue()){
                    marketInstruments = allInstruments;
                }else{
                    Log.wtf("spMarkets.setOnItemSelected","MyApplication.instruments count: " + MyApplication.instruments);
                    Log.wtf("spMarkets.setOnItemSelected","selectMarket.getValue() : " + selectMarket.getValue());
                    marketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments , selectMarket.getValue());
                }

                for (Instrument inst : marketInstruments) { inst.setIsSelected(false); }

                instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(TimeSalesActivity.this, marketInstruments,TimeSalesActivity.this);
                rvInstruments.setAdapter(instrumentsRecyclerAdapter);
                Log.wtf("select Market : " + selectMarket.toString() , "marketInstruments count = " + marketInstruments.size());

                allTrades.clear();
                /*for(int i=0; i<marketInstruments.size(); i++) {
                    allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, 0, marketInstruments.get(i).getInstrumentCode()));
                }*/
                allTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(MyApplication.timeSales, 0, marketInstruments));
                Log.wtf("on spMarkets click","allTrades count = " + allTrades.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rvInstruments.setLayoutManager(new LinearLayoutManager(TimeSalesActivity.this , LinearLayoutManager.HORIZONTAL, false));
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(this, marketInstruments,this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);
    }


    private class GetTimeSales extends AsyncTask<Void, String, String> {

        String lastTimesTamp = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            while (running) {

                String url = MyApplication.link + MyApplication.GetTrades.getValue();
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("stockId", "");
                parameters.put("instrumentId", "");
                parameters.put("MarketID", MyApplication.marketID);
                parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
                parameters.put("FromTS", MyApplication.timeSalesTimesTamp);
                lastTimesTamp = MyApplication.timeSalesTimesTamp;

                Log.wtf("Async GetTimeSales", "url: " + url);
                Log.wtf("Async GetTimeSales", "parameters: " + parameters);

                Log.wtf("Async Timestamp", "is: " + MyApplication.timeSalesTimesTamp);

                if (isCancelled())
                    break;

                try {
                    result = ConnectionRequests.GET(url, TimeSalesActivity.this, parameters);
                    publishProgress(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("GetTimeSales catch ex", e.getMessage());

                    if(!result.equals("")) {
                        if(MyApplication.isDebug) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetTrades.getKey(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                try {

                    Thread.sleep(3000);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.wtf("GetTimeSales InterruptedException catch ex", e.getMessage());
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                ArrayList<TimeSale> retrievedTimeSales = GlobalFunctions.GetTimeSales(values[0]);

                /*if(allTrades.size() == 0 && MyApplication.timeSales.size() != 0){
                    allTrades = (MyApplication.timeSales);
                    Log.wtf("allTrades enter","size = " + allTrades.size());
                }*/

                if (retrievedTimeSales.size() > 0) {

                    timeSales_DB = new SqliteDb_TimeSales(TimeSalesActivity.this);
                    timeSales_DB.open();
                    timeSales_DB.insertTimeSalesList(retrievedTimeSales);
                    timeSales_DB.close();
                    Log.wtf("TimeSalesActivity","insertTimeSalesList size = " + retrievedTimeSales.size());

                    Log.wtf("lastTimesTamp",": " + lastTimesTamp);
//                    if(!lastTimesTamp.equals("0")){
                        MyApplication.timeSales.addAll(0, retrievedTimeSales);
//                    }else{
//                        MyApplication.timeSales = (retrievedTimeSales);
//                    }

                    //MyApplication.timeSales.addAll(0, retrievedTimeSales);
                    Log.wtf("timeSales Trades size", "is: " + MyApplication.timeSales.size());

                    Log.wtf("GetTimeSales", "instrumentId: " + instrumentId);
                    Log.wtf("GetTimeSales", "stockId: " + stockId);
                    //allTrades.addAll(0, Actions.filterTimeSalesByInstrumentIDAndStockID(retrievedTimeSales, stockId, instrumentId));

                    tmpTrades = (Actions.filterTimeSalesByInstrumentIDAndStockID(retrievedTimeSales, stockId , MyApplication.instrumentId));
                    if(isSelectInstrument) {

                        allTrades.addAll(0,tmpTrades);
                    }
                    else{

                        if(selectMarket.getValue() == TradingSession.All.getValue()){
                            allTrades.addAll(0,Actions.filterTimeSalesByInstrumentsAndStockID(retrievedTimeSales, stockId, allInstruments));
                        }
                        else{
                            allTrades.addAll(0,Actions.filterTimeSalesByInstrumentsAndStockID(retrievedTimeSales, stockId, marketInstruments));
                        }
                    }

                    Log.wtf("allTrades Trades size", "is: " + allTrades.size());

                    /*adapter = new TimeSalesRecyclerAdapter(TimeSalesActivity.this, allTrades);
                    llm = new LinearLayoutManager(TimeSalesActivity.this);
                    rvTrades.setLayoutManager(llm);
                    rvTrades.setAdapter(adapter);*/
                    adapter.notifyDataSetChanged();
                }

                Log.wtf("adapter.getItemCount()",": " + adapter.getItemCount());
                Log.wtf("allTrades","size = " + allTrades.size());
                if(adapter.getItemCount() != allTrades.size()){
                    Log.wtf("adapter.getItemCount()","allTrades.size");
                    adapter = new TimeSalesRecyclerAdapter(TimeSalesActivity.this, allTrades);
                    rvTrades.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("GetTimeSales onProgressUpdate catch ex", e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(TimeSalesActivity.this);
        }

        @Override
        protected String doInBackground(Void... a) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetInstruments.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", instrumentId.length() == 0 ? "0" : instrumentId);
                parameters.put("key", getResources().getString(R.string.beforekey));
                result = ConnectionRequests.GET(url, TimeSalesActivity.this, parameters);

                MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("GetInstruments doInBackground catch ex", e.getMessage());
                if(MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyApplication.dismiss();

            allInstruments.clear();
            for(int i=1; i<AllMarkets.size(); i++){
                Log.wtf("GetInstruments onPostExecute","MyApplication.instruments count: " + MyApplication.instruments.size());
                Log.wtf("GetInstruments onPostExecute","AllMarkets.get(i) : " + AllMarkets.get(i).getValue());
                allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments , AllMarkets.get(i).getValue()));
            }
            marketInstruments = allInstruments;
            instrumentsRecyclerAdapter.notifyDataSetChanged();

            tmpTrades = (MyApplication.timeSales);
            allTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(tmpTrades, 0, marketInstruments));
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClicked(View v, int position) {

        for(int i=0; i<marketInstruments.size(); i++){
            if(i==position){
                marketInstruments.get(i).setIsSelected(marketInstruments.get(i).getIsSelected() ? false : true);
                selectedInstrument = marketInstruments.get(i).getIsSelected() ? marketInstruments.get(i) : new Instrument();
                instrumentId = marketInstruments.get(i).getIsSelected() ? selectedInstrument.getInstrumentCode() : "" ;
                isSelectInstrument = marketInstruments.get(i).getIsSelected() ? true : false ;
                MyApplication.instrumentId = instrumentId;
            }
            else{
                marketInstruments.get(i).setIsSelected(false);
            }
        }

        /*if(allInstruments.get(position).getIsSelected()){ allInstruments.get(position).setIsSelected(false); }
        else{ allInstruments.get(position).setIsSelected(true); }*/

        instrumentsRecyclerAdapter.notifyDataSetChanged();

        retrieveFiltered();
    }


    private void retrieveFiltered() {
        tmpTrades = new ArrayList<>();
        allTrades = new ArrayList<>();

        Log.wtf("MyApplication.timeSales","count = " + MyApplication.timeSales.size());
        /*if (hasSectorId) {

            tmpTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(MyApplication.timeSales, 0, marketInstruments));
            *//*for(int i=0; i<marketInstruments.size(); i++) {
                tmpTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, 0, marketInstruments.get(i).getInstrumentCode()));
            }*//*
        } else {

            tmpTrades = (Actions.filterTimeSalesByInstrumentsAndStockID(MyApplication.timeSales, 0, marketInstruments));
            //tmpTrades = (Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, 0 , MyApplication.instrumentId));
        }*/

        tmpTrades = (Actions.filterTimeSalesByInstrumentIDAndStockID(MyApplication.timeSales, 0 , MyApplication.instrumentId));

        Log.wtf("MyApplication.timeSales tmpTrades","count = " + tmpTrades.size());

        if(isSelectInstrument) {

            allTrades.addAll(tmpTrades);
        }
        else{

            if(selectMarket.getValue() == TradingSession.All.getValue()){
                /*for(int i=0; i<allInstruments.size(); i++) {
                    allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(tmpTrades, 0 , allInstruments.get(i).getInstrumentCode()));
                }*/

                allTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(tmpTrades, 0, allInstruments));
            }
            else{
                //self.stockQuotations = self.stockQuotations?.filter({ $0.tradingSession == appDelegate.segmentId })

                /*for(int i=0; i<marketInstruments.size(); i++) {
                    allTrades.addAll(Actions.filterTimeSalesByInstrumentIDAndStockID(tmpTrades, 0 , marketInstruments.get(i).getInstrumentCode()));
                }*/
                allTrades.addAll(Actions.filterTimeSalesByInstrumentsAndStockID(tmpTrades, 0, marketInstruments));
            }
        }
        Log.wtf("MyApplication.timeSales allTrades","count = " + allTrades.size());

        adapter = new TimeSalesRecyclerAdapter(TimeSalesActivity.this, allTrades);
        rvTrades.setAdapter(adapter);

        Log.wtf("on instr click","allStocks count = " + allTrades.size());
    }
}
