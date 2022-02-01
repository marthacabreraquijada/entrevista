package com.espublico.entrevista.hibernate.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "orders", schema = "entrevista", catalog = "")
public class OrdersEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "region", nullable = false, length = 255)
    private String region;
    @Basic
    @Column(name = "country", nullable = false, length = 255)
    private String country;
    @Basic
    @Column(name = "item_type", nullable = false, length = 255)
    private String itemType;
    @Basic
    @Column(name = "sales_channel", nullable = false, length = 255)
    private String salesChannel;
    @Basic
    @Column(name = "order_priority", nullable = false, length = 255)
    private String orderPriority;
    @Basic
    @Column(name = "order_date", nullable = false)
    private Date orderDate;
    @Basic
    @Column(name = "ship_date", nullable = false)
    private Date shipDate;
    @Basic
    @Column(name = "units_sold", nullable = false)
    private int unitsSold;
    @Basic
    @Column(name = "unit_price", nullable = false, precision = 0)
    private double unitPrice;
    @Basic
    @Column(name = "unit_cost", nullable = false, precision = 0)
    private double unitCost;
    @Basic
    @Column(name = "total_revenue", nullable = false, precision = 0)
    private double totalRevenue;
    @Basic
    @Column(name = "total_cost", nullable = false, precision = 0)
    private double totalCost;
    @Basic
    @Column(name = "total_profit", nullable = false, precision = 0)
    private double totalProfit;
    @Basic
    @Column(name = "batch", nullable = false, length = -1)
    private long batch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public String getOrderPriority() {
        return orderPriority;
    }

    public void setOrderPriority(String orderPriority) {
        this.orderPriority = orderPriority;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public int getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(int unitsSold) {
        this.unitsSold = unitsSold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public long getBatch() {
        return batch;
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdersEntity that = (OrdersEntity) o;
        return id == that.id && unitsSold == that.unitsSold && Double.compare(that.unitPrice, unitPrice) == 0 && Double.compare(that.unitCost, unitCost) == 0 && Double.compare(that.totalRevenue, totalRevenue) == 0 && Double.compare(that.totalCost, totalCost) == 0 && Double.compare(that.totalProfit, totalProfit) == 0 && Objects.equals(region, that.region) && Objects.equals(country, that.country) && Objects.equals(itemType, that.itemType) && Objects.equals(salesChannel, that.salesChannel) && Objects.equals(orderPriority, that.orderPriority) && Objects.equals(orderDate, that.orderDate) && Objects.equals(shipDate, that.shipDate) && Objects.equals(batch, that.batch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region, country, itemType, salesChannel, orderPriority, orderDate, shipDate, unitsSold, unitPrice, unitCost, totalRevenue, totalCost, totalProfit, batch);
    }
}
