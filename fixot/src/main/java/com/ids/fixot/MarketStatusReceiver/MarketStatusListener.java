package com.ids.fixot.MarketStatusReceiver;

public interface MarketStatusListener {

    public void refreshMarketTime(String status,String time,Integer color);

}
