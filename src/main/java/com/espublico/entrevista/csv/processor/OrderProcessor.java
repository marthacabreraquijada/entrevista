package com.espublico.entrevista.csv.processor;

import com.espublico.entrevista.csv.constants.OrderConstants;
import com.espublico.entrevista.hibernate.entity.OrdersEntity;
import com.espublico.entrevista.hibernate.util.HibernateUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.CaseUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class OrderProcessor implements OrderConstants {
    protected Iterable<CSVRecord> records;
    protected long batch;

    public OrderProcessor(String fileName) throws NullPointerException, IOException {
        FileReader file = new FileReader(fileName);
        this.records = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(file);
        this.batch = Instant.now().getEpochSecond();

    }

    public HashMap<String,HashMap<String,Long>> process() {
        this.saveOrders();
        this.createFile();
        HashMap<String,HashMap<String,Long>> summary = this.summary();
        HibernateUtil.shutdown();
        return summary;
    }

    private HashMap<String,HashMap<String,Long>> summary() {
        //Region, Country, Item Type, Sales Channel, Order Priority.
        Transaction transaction = null;
        var wrapper = new Object(){Session session = null; HashMap<String,HashMap<String,Long>> summary = new HashMap<>();};
        wrapper.summary.put(REGION, new HashMap<>());
        wrapper.summary.put(COUNTRY, new HashMap<>());
        wrapper.summary.put(ITEM_TYPE, new HashMap<>());
        wrapper.summary.put(SALES_CHANNEL, new HashMap<>());
        wrapper.summary.put(ORDER_PRIORITY, new HashMap<>());


        try {
            wrapper.session = HibernateUtil.getSessionFactory().openSession();
            transaction = wrapper.session.beginTransaction();
            wrapper.summary.forEach((type, typeSummary) -> {
                String column = CaseUtils.toCamelCase(type, false, ' ');
                List<Object[]> typeCount = wrapper.session.createQuery("select o." + column + ", count(o.id) from OrdersEntity as o where batch = :batch group by " + column + " order by " + column).setParameter("batch", this.batch).getResultList();
                typeCount.forEach((typeRow) -> {
                    typeSummary.put((String) typeRow[0], (Long)typeRow[1]);
                });
            });

            transaction.commit();
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error creando el archivo: " + e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (wrapper.session != null) {
                wrapper.session.close();
            }
        }
        return wrapper.summary;

    }
    private boolean createFile() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            List<OrdersEntity> orders = session.createQuery("from OrdersEntity where batch = :batch order by id", OrdersEntity.class).setParameter("batch", this.batch).getResultList();
            FileWriter out = new FileWriter("result." + this.batch + ".csv");
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {
                orders.forEach((order) -> {
                    try {
                        printer.printRecord(
                                order.getRegion(),
                                order.getCountry(),
                                order.getItemType(),
                                order.getSalesChannel(),
                                order.getOrderPriority(),
                                order.getOrderDate(),
                                order.getId(),
                                order.getShipDate(),
                                order.getUnitsSold(),
                                order.getUnitPrice(),
                                order.getUnitCost(),
                                order.getTotalRevenue(),
                                order.getTotalCost(),
                                order.getTotalProfit()
                                );
                    } catch (IOException e) {
                        System.out.println("Ha ocurrido un error insertando la orden: " + order.getId());
                    }
                });
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error creando el archivo: " + e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

     private boolean saveOrders() {
         Session session = null;
         Transaction transaction = null;
         try {

             session = HibernateUtil.getSessionFactory().openSession();
             transaction = session.beginTransaction();

             for (CSVRecord record : this.records) {
                 OrdersEntity order = new OrdersEntity();
                 // Set all properties
                 order.setId(Integer.parseInt(record.get(ORDER_ID)));
                 order.setRegion(record.get(REGION));
                 order.setCountry(record.get(COUNTRY));
                 order.setItemType(record.get(ITEM_TYPE));
                 order.setSalesChannel(record.get(SALES_CHANNEL));
                 order.setOrderPriority(record.get(ORDER_PRIORITY));
                 String orderDate = record.get(ORDER_DATE);
                 String shipDate = record.get(SHIP_DATE);
                 SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                 order.setOrderDate(new Date(format.parse(orderDate).getTime()));
                 order.setShipDate(new Date(format.parse(shipDate).getTime()));
                 order.setUnitsSold(Integer.parseInt(record.get(UNITS_SOLD)));
                 order.setUnitPrice(Double.parseDouble(record.get(UNIT_PRICE)));
                 order.setUnitCost(Double.parseDouble(record.get(UNIT_COST)));
                 order.setTotalRevenue(Double.parseDouble(record.get(TOTAL_REVENUE)));
                 order.setTotalCost(Double.parseDouble(record.get(TOTAL_COST)));
                 order.setTotalProfit(Double.parseDouble(record.get(TOTAL_PROFIT)));
                 order.setBatch(this.batch);
                 session.save(order);
             }

             transaction.commit();
             return true;
         } catch (Exception e) {
             if (transaction != null) {
                 transaction.rollback();
             }
             return false;
         } finally {
             if (session != null) {
                 session.close();
             }
         }

     }


}

