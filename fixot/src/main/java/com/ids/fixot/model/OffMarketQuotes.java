package com.ids.fixot.model;

import com.ids.fixot.R;

/**
 * Created by Amal on 3/23/2017.
 */

public class OffMarketQuotes {

    private String OffMarketLastPrice,OffMarketLastQuantity, OffMarketValueToday , OffMarketVolumeToday;
    private String InstrumentId, InstrumentNameAr, InstrumentNameEn, MarketType, NameAr,  NameEn, SectorID, SectorNameAr, SectorNameEn , SecurityID , StockID , SymbolAr , SymbolEn ;

    public OffMarketQuotes() {}

    public void setOffMarketLastPrice(String offMarketLastPrice) {
        OffMarketLastPrice = offMarketLastPrice;
    }

    public void setOffMarketLastQuantity(String offMarketLastQuantity) {
        OffMarketLastQuantity = offMarketLastQuantity;
    }

    public void setOffMarketValueToday(String offMarketValueToday) {
        OffMarketValueToday = offMarketValueToday;
    }

    public void setOffMarketVolumeToday(String offMarketVolumeToday) {
        OffMarketVolumeToday = offMarketVolumeToday;
    }

    public void setInstrumentId(String instrumentId) {
        InstrumentId = instrumentId;
    }

    public void setInstrumentNameAr(String instrumentNameAr) {
        InstrumentNameAr = instrumentNameAr;
    }

    public void setInstrumentNameEn(String instrumentNameEn) {
        InstrumentNameEn = instrumentNameEn;
    }

    public void setMarketType(String marketType) {
        MarketType = marketType;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public void setSectorID(String sectorID) {
        SectorID = sectorID;
    }

    public void setSectorNameAr(String sectorNameAr) {
        SectorNameAr = sectorNameAr;
    }

    public void setSectorNameEn(String sectorNameEn) {
        SectorNameEn = sectorNameEn;
    }

    public void setSecurityID(String securityID) {
        SecurityID = securityID;
    }

    public void setStockID(String stockID) {
        StockID = stockID;
    }

    public void setSymbolAr(String symbolAr) {
        SymbolAr = symbolAr;
    }

    public void setSymbolEn(String symbolEn) {
        SymbolEn = symbolEn;
    }

    public String getOffMarketLastPrice() {
        return OffMarketLastPrice;
    }

    public String getOffMarketLastQuantity() {
        return OffMarketLastQuantity;
    }

    public String getOffMarketValueToday() {
        return OffMarketValueToday;
    }

    public String getOffMarketVolumeToday() {
        return OffMarketVolumeToday;
    }

    public String getInstrumentId() {
        return InstrumentId;
    }

    public String getInstrumentNameAr() {
        return InstrumentNameAr;
    }

    public String getInstrumentNameEn() {
        return InstrumentNameEn;
    }

    public String getMarketType() {
        return MarketType;
    }

    public String getNameAr() {
        return NameAr;
    }

    public String getNameEn() {
        return NameEn;
    }

    public String getSectorID() {
        return SectorID;
    }

    public String getSectorNameAr() {
        return SectorNameAr;
    }

    public String getSectorNameEn() {
        return SectorNameEn;
    }

    public String getSecurityID() {
        return SecurityID;
    }

    public String getStockID() {
        return StockID;
    }

    public String getSymbolAr() {
        return SymbolAr;
    }

    public String getSymbolEn() {
        return SymbolEn;
    }
}
