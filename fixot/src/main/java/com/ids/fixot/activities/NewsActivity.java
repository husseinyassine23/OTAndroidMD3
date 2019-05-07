package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.NewsRecyclerAdapter;
import com.ids.fixot.model.NewsItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class NewsActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private TextView tvNoData;
    private NewsRecyclerAdapter adapter;
    LinearLayoutManager llm;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;
    GetNews mGetNews;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<NewsItem> allNews = new ArrayList<>();
    private boolean pulltoRefresh = false, flagLoading = false;
    RelativeLayout rootLayout;
    private boolean started = false;

    public NewsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_news);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();
        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);

        if (Actions.isNetworkAvailable(this)){

            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
        }else{

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }
    }


    public void back(View v){
        supportFinishAfterTransition();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        rvNews = findViewById(R.id.rvNews);
        llm = new LinearLayoutManager(NewsActivity.this);

        swipeContainer = findViewById(R.id.swipeContainer);
        tvNoData = findViewById(R.id.tvNoData);

        rvNews.setLayoutManager(llm);
        Actions.initializeToolBar(getString(R.string.news), NewsActivity.this);
    }

    private void setListeners(){

        swipeContainer.setOnRefreshListener(() -> {

            allNews.clear();
            adapter.notifyDataSetChanged();
            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
            swipeContainer.setRefreshing(false);
        });

        adapter = new NewsRecyclerAdapter(this, allNews);
        rvNews.setAdapter(adapter);
    }

    public void loadFooter(View v){

        Actions.loadFooter(this, v);
    }

    public void addItems() {
        if (allNews.size() > 0) {
            flagLoading = false;
            //getNews = new GetNews();
            //getNews.execute(allNews.get(allNews.size() - 1).getId() + "");
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mGetNews.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(mGetNews);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("News ex", e.getMessage());
        }
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetNews extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetNews.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("newsId", "0");
            parameters.put("stockId", "");
            parameters.put("language", "" ); //Actions.getLanguage()
            parameters.put("count", "" + MyApplication.count);
            parameters.put("key", getResources().getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, NewsActivity.this, parameters);
                Log.wtf("result", result);
                allNews.addAll(GlobalFunctions.GetNews(result));

            } catch (Exception e) {
                e.printStackTrace();
                if(MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetNews.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            adapter.notifyDataSetChanged();
        }
    }
}
