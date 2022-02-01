package com.espublico.entrevista.csv.constants;

/**
 * OrderConstants
 * Interface que contiene los campos del CSV
 */
public interface OrderConstants {

    public static final String REGION = "Region";
    public static final String COUNTRY = "Country";
    public static final String ITEM_TYPE = "Item Type";
    public static final String SALES_CHANNEL = "Sales Channel";
    public static final String ORDER_PRIORITY = "Order Priority";
    public static final String ORDER_DATE = "Order Date";
    public static final String ORDER_ID = "Order ID";
    public static final String SHIP_DATE = "Ship Date";
    public static final String UNITS_SOLD = "Units Sold";
    public static final String UNIT_PRICE = "Unit Price";
    public static final String UNIT_COST = "Unit Cost";
    public static final String TOTAL_REVENUE = "Total Revenue";
    public static final String TOTAL_COST = "Total Cost";
    public static final String TOTAL_PROFIT = "Total Profit";
    String[] HEADERS = {REGION, COUNTRY, ITEM_TYPE, SALES_CHANNEL, ORDER_PRIORITY, ORDER_DATE, ORDER_ID, SHIP_DATE, UNITS_SOLD, UNIT_PRICE, UNIT_COST, TOTAL_REVENUE, TOTAL_COST, TOTAL_PROFIT};


}
