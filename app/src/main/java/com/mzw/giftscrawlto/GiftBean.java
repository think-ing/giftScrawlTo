package com.mzw.giftscrawlto;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by think on 2018/4/15.
 */

public class GiftBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;
    public String fileName;
    public int price;
    public int sign = 0;
    public float currentX = 0;
    public float currentY = 0;
    public Bitmap mBitmap;
    public String name;

    public GiftBean(int id, String fileName, int price, float currentX, float currentY, String name) {
        this.id = id;
        this.fileName = fileName;
        this.price = price;
        this.currentX = currentX;
        this.currentY = currentY;
        this.name = name;
    }
    public GiftBean(int id, String fileName, int price, int sign, String name) {
        this.id = id;
        this.fileName = fileName;
        this.price = price;
        this.sign = sign;
        this.name = name;
    }

    public GiftBean(int id, String fileName, int price) {
        this.id = id;
        this.fileName = fileName;
        this.price = price;
    }

    public GiftBean(float currentX, float currentY) {
        this.currentX = currentX;
        this.currentY = currentY;
    }

    public GiftBean(int id, String fileName, int price, float currentX, float currentY, Bitmap mBitmap) {
        this.id = id;
        this.fileName = fileName;
        this.price = price;
        this.currentX = currentX;
        this.currentY = currentY;
        this.mBitmap = mBitmap;
    }

    @Override
    public String toString() {
        return "GiftBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", price=" + price +
                ", sign=" + sign +
                ", currentX=" + currentX +
                ", currentY=" + currentY +
                ", mBitmap=" + mBitmap +
                ", name='" + name + '\'' +
                '}';
    }
}
