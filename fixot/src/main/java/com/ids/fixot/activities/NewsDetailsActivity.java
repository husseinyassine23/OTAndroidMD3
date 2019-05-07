package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.NewsItem;

import java.util.Calendar;

/**
 * Created by user on 7/24/2017.
 */

public class NewsDetailsActivity extends AppCompatActivity {

    public NewsDetailsActivity() {
        LocalUtils.updateConfig(this);
    }

    RelativeLayout rootLayout;
    TextView tvTitle, tvDate, tvDescription;
    Button btSource;
    NewsItem newsItem;
    private boolean started = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_news_details);
        Actions.initializeBugsTracking(this);

        findViews();
        started = true;

        try {
            newsItem = getIntent().getExtras().getParcelable("newsItem");
        } catch (Exception e) {
            e.printStackTrace();
            newsItem = new NewsItem();
        }

        try {
            tvTitle.setText(newsItem.getHead());
        } catch (Exception e) {
            e.printStackTrace();
            tvTitle.setText("");
        }

        try {
            tvDate.setText(newsItem.getCreationDate());
        } catch (Exception e) {
            e.printStackTrace();
            tvDate.setText("");
        }

        try {
            tvDescription.setText(newsItem.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
            tvDescription.setText("");
        }

        if (newsItem.getDetails().equals("")){
            tvDescription.setVisibility(View.GONE);
        }

        Actions.initializeToolBar(getString(R.string.news),NewsDetailsActivity.this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        if(isProbablyArabic(newsItem.getHead())){
            //Log.wtf("News","is arabic");
            tvTitle.setTypeface(MyApplication.droidbold);
            tvTitle.setGravity(Gravity.RIGHT);
        }
    }


    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }


    public void loadFooter(View v){

        Actions.loadFooter(this, v);
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

    public void back(View v){
        finish();
    }

    private void findViews(){

        rootLayout =  findViewById(R.id.rootLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate =  findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        btSource = findViewById(R.id.btSource);

        btSource.setOnClickListener(v ->
                {
//                    Intent browserIntent ;
                    String url = newsItem.getLink();
//
//                    Log.wtf("last 4 character",": " + url.substring(url.length() - 4));
//                    if(url.substring(url.length() - 4).equals(".pdf")){
//                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/viewer?url="+url));
//                    }
//                    else{
//                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    }
//                    startActivity(browserIntent);

                    Log.wtf("last 4 character",": " + url.substring(url.length() - 4));
                    if(url.substring(url.length() - 4).equals(".pdf")){
                        url = "https://docs.google.com/viewer?url="+url ;
                    }

                    Log.wtf("origianl","link = " + newsItem.getLink());
                    Log.wtf("news","link = " + url);

                    startActivity(new Intent(NewsDetailsActivity.this, PdfDisplayActivity.class).putExtra("url", url));
                }
        );
    }


    public void goTo(View v){

        try {

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
//            startActivity(browserIntent);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(newsItem.getLink()), "application/pdf");
            this.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
            e.printStackTrace();

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
//            startActivity(browserIntent);

            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.parse("https://docs.google.com/viewer?url="+newsItem.getLink()), "text/html");
            startActivity(intent2);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
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
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }
}
