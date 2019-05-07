package com.ids.fixot;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

//import com.crashlytics.android.Crashlytics;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.MarketStatus;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.Parameter;
import com.ids.fixot.model.Portfolio;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.SubAccount;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.model.Unit;
import com.ids.fixot.model.User;
import com.ids.fixot.model.WebItem;
import com.ids.fixot.model.item;
import com.ids.fixot.model.webserviceItem;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziMobileConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    public static boolean isMultiAccountStatements = true;

    //<editor-fold desc="Initial Configuration section">
    public static boolean showInternetLossDialog = true;
    public static boolean showBackgroundRequestToastError = true;
    public static boolean isDebug = true;
    public static boolean showMowazi = false;
    public static String link = "", baseLink = "", appLabel = "";
    public static SharedPreferences mshared;
    public static SharedPreferences.Editor editor;
    public static final int VIBRATION_PERIOD = 250;
    public static final int Session_Out_Period = 14;
    public static int ENGLISH = 1, ARABIC = 2;
    public static int lang = ENGLISH;
    public static int screenWidth, screenHeight;
    public static Typeface opensansregular, opensansbold, droidregular, giloryBold, giloryItaly, droidbold;

    public static Boolean trustEveryone = false;

    public static Boolean showOTC = false;
    public static Boolean isOTC = false;

    public static int LOCAL = 1, TESTING = 2, LIVE = 3, MD3 = 4, SERVICE_LINK = MD3 ;
    public static String afterKey = "";

    public static Context context;
    public static ProgressDialog progress;
    public static MyApplication instance;
    //</editor-fold>

    //<editor-fold desc="Ot section">
    public final static int MARKET_OPEN = 1;
    public final static int MARKET_CLOSED = -1;
    public final static int VALUES_SPAN_COUNT = 1;
    public final static int GRID_VALUES_SPAN_COUNT = 2;
    public static int count = 20;
    public static User currentUser = new User();
    public static SubAccount selectedSubAccount = new SubAccount();
    public static Portfolio portfolio = new Portfolio();
    public static MarketStatus marketStatus = new MarketStatus();
    public final static int ORDER_BUY = 1, ORDER_SELL = 2;
    public final static int STATUS_EXECUTED = 6, STATUS_REJECTED = 10, STATUS_PRIVATE = 16;
    public static Parameter parameter = new Parameter();
    public static final String CIRCUIT_BREAKER = "10";
    public static final int TOP_GAINERS = 1;
    public static final int TOP_LOSERS = 2;
    public static final int TOP_TRADED = 3;
    public static final int TOP_TRADES = 4;
    public static final int TOP_VALUES = 5;
    public static final int MARKET_PRICE = 1;
    public static final int LIMIT = 2;
    public static String instrumentId = "";
    public static String Auction_Instrument_id = "AUCTION_MKT";
    public static String CB_Auction_id = "10";
    public static ArrayList<BrokerageFee> allBrokerageFees = new ArrayList<>();
    public static ArrayList<StockQuotation> stockQuotations = new ArrayList<>();
    public static ArrayList<TimeSale> timeSales = new ArrayList<>();
    public static ArrayList<WebItem> webItems = new ArrayList<>();
    public static ArrayList<Instrument> instruments = new ArrayList<>();
    public static ArrayList<Unit> units = new ArrayList<>();
    public static ArrayList<OrderDurationType> allOrderDurationType = new ArrayList<>();
    public static HashMap<String, Instrument> instrumentsHashmap = new HashMap<>();

    public static String brokerID = "0";
    public static String stockTimesTamp="0";
    public static String sectorsTimesTamp="0";
    public static String timeSalesTimesTamp="0";
    public static String marketID = (isOTC ? Integer.toString(enums.MarketType.KWOTC.getValue()) : Integer.toString(enums.MarketType.XKUW.getValue()) );
    //</editor-fold>

    //<editor-fold desc="async tasks pool section">
    public static int corePoolSize = 60;
    public static int  maximumPoolSize = 80;
    public static int  keepAliveTime = 10;
    public static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    //</editor-fold>

    //<editor-fold desc="mowazi section">
    public static double dinar = 0.0;
    public static double brokerageQuantityCoif = 0.0;
    public static double brokerageSumCoif = 0.0;
    public static int defaultPriceType = 1;
    public static int mowaziClientID = 0;
    public static String mowaziUrl = "https://www.almowazi.com/md3iphoneservice/services/dataservice.svc"; //"https://www.almowazi.com/AlmowaziBrokerService/servicedata.asmx";
    public static String mowaziNewsLink = "";
    public static String mowaziImagePath = "";
    public static String mowaziGeneralNewsLink = "";
    public static String mowaziBrokerId = "1";
    public static ArrayList<MowaziCompany> allMowaziCompanies = new ArrayList<>();
    public static ArrayList<MowaziMobileConfiguration> allMowaziMobileConfigurations = new ArrayList<>();
    //</editor-fold>

    public static item GetStockQuotation = new item();
    public static item GetRealTimeData = new item();
    public static item AddDevice2 = new item();
    public static item GetParameters = new item();
    public static item GetInstruments = new item();
    public static item GetBrokerageFees = new item();
    public static item GetSiteMapData = new item();
    public static item UpdateBadgeNotification = new item();
    public static item GetOrderDurationTypes = new item();
    public static item Login = new item();
    public static item GetTradeTickerData = new item();
    public static item GetPriceTickerData = new item();
    public static item GetSectorChartData = new item();
    public static item LoadSectorDetails = new item();
    public static item GetPortfolio = new item();
    public static item GetUserOrders = new item();
    public static item LoadStockDetails = new item();
    public static item GetStockChartData = new item();
    public static item GetTrades = new item();
    public static item GetOffMarketQuotes = new item();
    public static item GetStockOrderBook = new item();
    public static item GetSectorIndex = new item();
    public static item GetStockTops = new item();
    public static item GetNews = new item();
    public static item GetMobileSiteMap = new item();
    public static item AddFavoriteStocks = new item();
    public static item RemoveFavoriteStocks = new item();
    public static item GetFavoriteStocks = new item();
    public static item GetQuickLinks = new item();
    public static item GetTradeInfo = new item();
    public static item AddNewOrder = new item();
    public static item CancelOrder = new item();
    public static item UpdateOrder = new item();
    public static item ActivateOrder = new item();
    public static item GetUnits = new item();
    public static item ChangePassword = new item();

    public static String AlternativeWebserviceLink = "http://www.ids-support.com/iphone/fixbrokers.json";

    public static String CmplxPassStringAr ,CmplxPassStringEn ,PassStringAr ,PassStringEn ;

    public static Calendar sessionOut = null;


    @Override
    public void onCreate() {
        super.onCreate();

        if(SERVICE_LINK == LOCAL){

            //link = "http://10.2.2.103/Live_Webservice/Services/DataService.svc";
            //baseLink = "http://10.2.2.103/Live_Webservice/Mobile/";

            link = "http://10.2.2.103/OTWebService/Services/DataService.svc";
            baseLink = "http://10.2.2.103/OTWebService/Mobile/";


            // link = "https://testweb1.waseet.com.kw//MobileServiceT2/Services/DataService.svc";  //Wasset MD3
            // baseLink = "https://testweb1.waseet.com.kw//MobileServiceT2/Mobile/";

            // link = "http://phaseii.sharqtrade.com/MD3iphoneWebservice/Services/DataService.svc"; //Sharq MD3
            // baseLink = "http://phaseii.sharqtrade.com/MD3iphoneWebservice/Mobile/";

            // link = "http://www.kiconlinetrading.kicwasata.com/MD3iphoneWebservice/Services/DataService.svc"; //KIC MD3
            // baseLink = "http://www.kiconlinetrading.kicwasata.com/MD3iphoneWebservice/Mobile/";

            // link = "http://www.tijarifb.com/MobileServiceMD3/Services/DataService.svc"; //Tijari MD3
            // baseLink = "http://www.tijarifb.com/MobileServiceMD3/Mobile/";

            // link = "http://www.oulawasata.com.kw/MobileServiceMD3/Services/DataService.svc"; //Oula MD3
            // baseLink = "http://www.oulawasata.com.kw/MobileServiceMD3/Mobile/";

             //link = "https://online.waseet.com.kw/MD3AndroidService/Services/DataService.svc";  //Wasset MD3
             //baseLink = "https://online.waseet.com.kw/MD3AndroidService/Mobile/";

             /*link = "http://www.oulawasata.com.kw/MD3AndroidService/Services/DataService.svc";  //oulawasata MD3
             baseLink = "http://www.oulawasata.com.kw/MD3AndroidService/Mobile/";*/

            //link = "http://appiph.usbc.com.kw/MD3AndroidService/Services/DataService.svc";  //tijari MD3
            //baseLink = "http://appiph.usbc.com.kw/MD3AndroidService/Mobile/";

            //link = "http://www.kiconlinetrading.kicwasata.com/MD3AndroidService/Services/DataService.svc";  //kic MD3
            //baseLink = "http://www.kiconlinetrading.kicwasata.com/MD3AndroidService/Mobile/";

            //link = "http://www.sharqetrade.com/MD3AndroidService/Services/DataService.svc";  //sharqetrade MD3
            //baseLink = "http://www.sharqetrade.com/MD3AndroidService/Mobile/";

        }else if(SERVICE_LINK == TESTING){

            link = BuildConfig.TestingLink;
            baseLink = BuildConfig.BaseTestingLink;

        }else if(SERVICE_LINK == LIVE){

            link = BuildConfig.LiveLink;
            baseLink = BuildConfig.BaseLiveLink;

        }else if(SERVICE_LINK == MD3){

            link = BuildConfig.MD3Link;
            baseLink = BuildConfig.BaseMD3Link;
        }

        appLabel = BuildConfig.label;

        brokerID = BuildConfig.BrokerId;
        MyApplication.showInternetLossDialog = false;
        MyApplication.timeSalesTimesTamp = "0";

        context = getApplicationContext();
        setWebserviceItem();

        GetScreenDimensions();
        try {
            droidregular = Typeface.createFromAsset(getAssets(),
                    "DroidKufiRegular.ttf");
            droidbold = Typeface.createFromAsset(getAssets(),
                    "DroidKufiBold.ttf");
            giloryBold = Typeface.createFromAsset(getAssets(),
                    "GilroyBold.otf");
            giloryItaly = Typeface.createFromAsset(getAssets(),
                    "GilroyLight.otf");

            if (MyApplication.showMowazi) {
                opensansregular = Typeface.createFromAsset(getAssets(),
                        "opensansregular.ttf");
                opensansbold = Typeface.createFromAsset(getAssets(),
                        "opensansbold.ttf");
            }

            instance = this;

            mshared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = mshared.edit();
            MyApplication.lang = MyApplication.mshared.getInt("lang", 0);


            if(MyApplication.lang==MyApplication.ARABIC)
                LocalUtils.setLocale(new Locale("ar"));
            else
                LocalUtils.setLocale(new Locale("en"));
            LocalUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

            if (Actions.isMyServiceRunning(context, AppService.class)){
                Log.wtf("will stop it","...");
                Actions.stopAppService(context);
            }

            Intent intent = new Intent(getApplicationContext(), AppService.class);
            startService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }


        CmplxPassStringAr = "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<div style=\"direction: rtl;\">\n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">بهدف توفير الحماية والأمن, قمنا بوضع بعض النصائح لإختيار كلمة السر. يرجى إتباع التعليمات التالية عند اختيار كلمة السر</td>\n" +
                "<td valign=\"top\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>إن كلمة السر حساسة للغاية (فحرف A الكبير يختلف عن a الصغير) عند إختيار كلمة السر</li>\n" +
                "<li>يجب أن تكون كلمة السر من 8 حروف على الاقل وبحد اقصى 16 حرف</li>\n" +
                "<li>يجب أن تحتوي كلمة السر على اي حرف من A الى Z وعلى رقم من 0 الى 9</li>\n" +
                "<li>ويتعين أن لا تحتوي كلمة السر على:</li>\n" +
                "<ul>\n" +
                "<li>اكثر من حرفين متشابهين ومكررين (rrr او ppp لا يسمح بكتابة)</li>\n" +
                "<li>اكثر من حرفين متتاليين أو متعاقبين (لا يسمح بإستخدام abc على سبيل المثال)</li>\n" +
                "<li>اكثر من ثلاثة إرقام متتالية او متشابهة (لا يسمح بإستخدام 2345 على سبيل المثال)</li>\n" +
                "<li>لا يسمح باستخدام الحروف او الرموز الخاصة مثل ?!()&lt;&gt;&amp;%$#@*</li>\n" +
                "</ul>\n" +
                "<li>يتعين أن لا تكون كلمة السر الجديدة مطابقة لإسم المستخدم او لإسم الدخول</li>\n" +
                "<li>يتعين ان تكون كلمة السر الجديدة مختلفة عن كلمة السر السابقة</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n";

        CmplxPassStringEn = "\n" +
                "<table cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"width: 100%;\">\n" +
                "In order to protect your security, we have set certain rules for selecting passwords. Please follow these guidlines while selecting a password:\n" +
                "</td>\n" +
                "<td valign=\"top\"> </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>\n" +
                "The password is case sensitive ('A' is different from 'a')\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should have at least 8 characters and a maximum of 16 characters\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should have at least one letter (a-z) and one numeric character (0-9)\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should not have:\n" +
                "</li>\n" +
                "<ul>\n" +
                "<li>\n" +
                "more than two repeated letters (rrr, ppp are not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "more than two consecutive letters (abc is not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "more than three consecutive numbers (2345 is not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "special characters such as spaces or \\\"()/|?,;:'~&lt;&gt;\\\\+=.[]{}\\\"\n" +
                "</li>\n" +
                "</ul>\n" +
                "<li>\n" +
                "The password should not be identical to the User Name or name\n" +
                "</li>\n" +
                "<li>\n" +
                "The password must be different from your earlier password\n" +
                "</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>";

        PassStringAr = "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<div style=\"direction: rtl;\">\n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">بهدف توفير الحماية والأمن, قمنا بوضع بعض النصائح لإختيار كلمة السر. يرجى إتباع التعليمات التالية عند اختيار كلمة السر</td>\n" +
                "<td valign=\"top\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>إن كلمة السر حساسة للغاية (فحرف A الكبير يختلف عن a الصغير) عند إختيار كلمة السر</li>\n" +
                "<li>يجب أن تكون كلمة السر من 8 حروف على الاقل وبحد اقصى 16 حرف</li>\n" +
                "<li>يجب أن تحتوي كلمة السر على اي حرف من A الى Z وعلى رقم من 0 الى 9</li>\n" +
                "<li>ويتعين أن لا تحتوي كلمة السر على:</li>\n" +
                "<ul>\n" +
                "<li>لا يسمح باستخدام الحروف او الرموز الخاصة مثل ?!()&lt;&gt;&amp;%$#@*</li>\n" +
                "</ul>\n" +
                "<li>يتعين أن لا تكون كلمة السر الجديدة مطابقة لإسم المستخدم او لإسم الدخول</li>\n" +
                "<li>يتعين ان تكون كلمة السر الجديدة مختلفة عن كلمة السر السابقة</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n";

        PassStringEn = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">In order to protect your security, we have set certain rules for selecting passwords. Please follow these guidlines while selecting a password:</td>\n" +
                "<td valign=\"top\">&nbsp;</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>The password is case sensitive ('A' is different from 'a')</li>\n" +
                "<li>The password should have at least 8 characters and a maximum of 16 characters</li>\n" +
                "<li>The password should have at least one letter (a-z) and one numeric character (0-9)</li>\n" +
                "<li>The password should not have:&nbsp;</li>\n" +
                "<ul>\n" +
                "<li>special characters such as spaces or \\\"()/|?,;:'~&lt;&gt;\\\\+=.[]{}\\\"</li>\n" +
                "</ul>\n" +
                "<li>The password should not be identical to the User Name or name</li>\n" +
                "<li>The password must be different from your earlier password</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n";


    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);

        MultiDex.install(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    private void GetScreenDimensions() {
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalUtils.updateConfig(this, newConfig);
    }

    public static void showDialog(Activity activity) {
        progress = new ProgressDialog(activity, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.show();
    }

    public static void dismiss() {
        if (progress != null)
            progress.dismiss();
    }

    public void setWebserviceItem(){

        this.GetStockQuotation = new item("100","/GetStockQuotation?");
        this.GetRealTimeData = new item("101","/GetRealTimeData?");
        this.AddDevice2 = new item("102","/AddDevice?");
        this.GetParameters = new item("103","/GetParameters?");
        this.GetInstruments = new item("104","/GetInstruments?");
        this.GetBrokerageFees = new item("105","/GetBrokerageFees?");
        this.GetSiteMapData = new item("106","/GetSiteMapData?");
        this.UpdateBadgeNotification = new item("107","/UpdateBadgeNotification?");
        this.GetOrderDurationTypes = new item("108","/GetOrderDurationTypes?");
        this.Login = new item("109","/Login?");
        this.GetTradeTickerData = new item("110","/GetTradeTickerData?");
        this.GetPriceTickerData = new item("111","/GetPriceTickerData?");
        this.GetSectorChartData = new item("112","/GetSectorChartData?");
        this.LoadSectorDetails = new item("113","/LoadSectorDetails?");
        this.GetPortfolio = new item("114","/GetPortfolio?");
        this.GetUserOrders = new item("115","/GetUserOrders?");
        this.LoadStockDetails = new item("116","/LoadStockDetails?");
        this.GetStockChartData = new item("117","/GetStockChartData?");
        this.GetTrades = new item("118","/GetTrades?");
        this.GetStockOrderBook = new item("119","/GetStockOrderBook?");
        this.GetSectorIndex = new item("122","/GetSectorIndex?");
        this.GetStockTops = new item("123","/GetStockTops?");
        this.GetNews = new item("124","/GetNews?");
        this.GetMobileSiteMap = new item("125","/GetMobileSiteMap?");
        this.AddFavoriteStocks = new item("126","/AddFavoriteStocks?");
        this.RemoveFavoriteStocks = new item("127","/RemoveFavoriteStocks?");
        this.GetFavoriteStocks = new item("128","/GetFavoriteStocks?");
        this.GetQuickLinks = new item("129","/GetQuickLinks?");
        this.GetTradeInfo = new item("130","/GetTradeInfo?");
        this.AddNewOrder = new item("131","/AddNewOrder?");
        this.CancelOrder = new item("132","/CancelOrder?");
        this.UpdateOrder = new item("133","/UpdateOrder?");
        this.ActivateOrder = new item("134","/ActivateOrder?");
        this.GetUnits = new item("135","/GetUnits?");
        this.ChangePassword = new item("136","/ChangePassword?");
        this.GetOffMarketQuotes = new item("137","/GetOffMarketQuotes?");
    }

}