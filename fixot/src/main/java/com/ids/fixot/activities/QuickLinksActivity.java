package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.SiteRecyclerAdapter;
import com.ids.fixot.model.WebItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



/**
 * Created by user on 4/3/2017.
 */

public class QuickLinksActivity extends AppCompatActivity implements SiteRecyclerAdapter.RecyclerViewOnItemClickListener{

    RelativeLayout rootLayout;
    private ArrayList<WebItem> webItems = new ArrayList<>();
    SiteRecyclerAdapter adapter;
    RecyclerView rvList;
    LinearLayoutManager llm;
    ProgressBar progressBar;
    private boolean started = false;

    public QuickLinksActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_site);
        Actions.initializeBugsTracking(this);

        findViews();

        Actions.initializeToolBar(getString(R.string.links), QuickLinksActivity.this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        started = true;

        if (Actions.isNetworkAvailable(this)){

            new GetQuickLinks().execute();
        }else {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFooter(View v){

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);


//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    public void back(View v){
        finish();
    }

    private void findViews(){

        rootLayout = findViewById(R.id.rootLayout);
        rvList = findViewById(R.id.rvList);
        progressBar = findViewById(R.id.progressBar);

        llm = new LinearLayoutManager(QuickLinksActivity.this);
        rvList.setLayoutManager(llm);
        adapter = new SiteRecyclerAdapter(this, webItems, this);
        rvList.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(View v, int position) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webItems.get(position).getUrl()));
        startActivity(browserIntent);

//        startActivity(new Intent(QuickLinksActivity.this, SiteMapDataActivity.class).putExtra("linkObject", webItems.get(position)));
    }

    private class GetQuickLinks extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {

                MyApplication.showDialog(QuickLinksActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetQuickLinks.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<>();

            parameters.put("key", getResources().getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, QuickLinksActivity.this, parameters);
                Log.wtf("result", result);
                webItems.addAll(GlobalFunctions.GetSiteMap(result));

            } catch (Exception e) {
                e.printStackTrace();
                if(MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetQuickLinks.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.wtf("allNews", webItems.size()+"");

            try{

                MyApplication.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
