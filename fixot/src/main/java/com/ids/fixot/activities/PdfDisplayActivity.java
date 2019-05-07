package com.ids.fixot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

/**
 * Created by DEV on 3/20/2018.
 */

public class PdfDisplayActivity extends AppCompatActivity {


    RelativeLayout rootLayout;
    WebView wvDetails;
    private boolean started = false;
    String url;

    public PdfDisplayActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_webpage);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();

        url = getIntent().getExtras().getString("url");

        loadWebView();
        wvDetails.loadUrl(url);


        Actions.initializeToolBar(getString(R.string.news),this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);
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

    private void loadWebView() {

        wvDetails.getSettings().setJavaScriptEnabled(true);
        wvDetails.getSettings().setUseWideViewPort(true);
        wvDetails.getSettings().setLoadWithOverviewMode(true);
        wvDetails.getSettings().setAllowFileAccess(true);
        wvDetails.getSettings().setAllowContentAccess(true);
        wvDetails.setLayerType(WebView.LAYER_TYPE_NONE, null);

        wvDetails.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.wtf("url", url);
                /*if (url.contains(".pdf")) {

                    try {
                        Log.wtf("contains pdf", "yes");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "application/pdf");
                        view.getContext().startActivity(intent);

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        e.printStackTrace();

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setDataAndType(Uri.parse("https://docs.google.com/viewer?url=" + url), "text/html");
                        startActivity(intent2);
                    }
                } else {
                    Log.wtf("contains pdf", "no");
                    //view.loadUrl(url);

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }*/
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }
        });

        wvDetails.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

            }
        });
    }

    public void back(View v){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void findViews(){

        rootLayout = findViewById(R.id.rootLayout);
        wvDetails = findViewById(R.id.wvDetails);
    }


    public void share(View v) {
        String shareBody = url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
}
