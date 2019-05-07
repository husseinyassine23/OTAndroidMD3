package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.fragments.TopGainersFragment;
import com.ids.fixot.fragments.TopLosersFragment;
import com.ids.fixot.fragments.TopTradedFragment;
import com.ids.fixot.fragments.TopTradesFragment;
import com.ids.fixot.fragments.TopValuesFragment;

import java.util.Calendar;


public class TopsPagerActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    RelativeLayout rootLayout;
    private TabLayout tlTabs;
    String tag_top_gainers = "top_gainers";
    String tag_top_losers = "top_losers";
    String tag_top_trades = "top_trades";
    String tag_top_traded = "top_traded";
    String tag_top_values = "top_values";

    private boolean started = false;

    public TopsPagerActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_tops_pager);
        Actions.showHideFooter(this);
        Actions.initializeBugsTracking(this);

        Actions.initializeToolBar(getResources().getString(R.string.tops), this);

        started = true;
        findViews();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, new TopGainersFragment(), tag_top_gainers)
                .commit();

        Actions.overrideFonts(this, rootLayout, false);

        changeTabsFont(tlTabs, MyApplication.lang  == MyApplication.ENGLISH? MyApplication.giloryBold : MyApplication.droidbold);
    }

    private void findViews() {

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        tlTabs = (TabLayout) findViewById(R.id.tlTabs);
        fragmentManager = getSupportFragmentManager();

        initializeTabHost();

    }

    private void initializeTabHost(){

        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_gainer)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_looser)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_traded)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_trades)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_value)));

        tlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.wtf("pos", "is "+tab.getPosition());
                int position = tab.getPosition();

                switch (position){

                    case 0:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopGainersFragment(), tag_top_gainers)
                                .commit();
                        break;

                    case 1:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopLosersFragment(), tag_top_losers)
                                .commit();
                        break;

                    case 2:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopTradedFragment(), tag_top_traded)
                                .commit();
                        break;

                    case 3:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopTradesFragment(), tag_top_trades)
                                .commit();
                        break;

                    case 4:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopValuesFragment(), tag_top_values)
                                .commit();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tlTabs.getTabAt(0).select();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.InitializeSessionService(this);
        Actions.InitializeMarketService(this);
        Actions.checkLanguage(this, started);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {

        finish();
    }

    public void loadFooter(View v) {

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
}
