package com.wishlist.wishlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by honey on 4/20/2016.
 */
public class Product {
    private String productName;
    private String productDescription;
    private Date addingDate;
    private String productAdder;
    private boolean status;
    private int productId;
    private String serverId;
    private boolean hasImage;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean ownsImage() {
        return hasImage;
    }

    public String getServerId() {
        return serverId;
    }

    public Product(String productName, String productDescription, String productAdder, String serverId, Date addingDate, boolean hasImage, boolean status) {
        this.productName = productName;
        this.addingDate = addingDate;
        this.productDescription = productDescription;
        this.productAdder = productAdder;
        this.status = status;
        this.serverId = serverId;
        this.hasImage = hasImage;
    }

    public Product(String productName, String productDescription, String productAdder, Date addingDate, boolean status) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productAdder = productAdder;
        this.addingDate = addingDate;
        this.status = status;
    }

    public static List<Product> productDummyData(){
        List<Product> productList=new ArrayList();
        Date date = new Date();

        Product product=new Product("Sugar", "Please bring it today Please bring it today s d d d d d d d dPlease bring it today  Please bring it today Please bring it today", "Mommy", date, true);
        Product product1=new Product("Milk", "fat free", "Mommy", date, true);
        Product product2=new Product("Water", "", "Arifa", date, true);
        Product product3=new Product("Coffee", "cappuccino", "Antti", date, true);
        Product product4=new Product("Pizza", "No food today", "Daddy", date, true);
        Product product5=new Product("Cornfloor", "for chips", "Mommy", date, true);
        Product product6=new Product("bulb", "200w", "Mikka", date, true);
        Product product7=new Product("yougurt", "2kg", "Mikka", date, true);
        Product product8=new Product("tissue", "8kpl", "Antti", date, true);


        productList.add(product);
        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        productList.add(product6);
        productList.add(product7);
        productList.add(product8);
        return productList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Date getAddingDate() {
        return addingDate;
    }

    public void setAddingDate(Date addingDate) {
        this.addingDate = addingDate;
    }

    public String getProductAdder() {
        return productAdder;
    }

    public void setProductAdder(String productAdder) {
        this.productAdder = productAdder;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
