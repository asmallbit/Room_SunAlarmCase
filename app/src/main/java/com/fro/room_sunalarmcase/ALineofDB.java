package com.fro.room_sunalarmcase;

public class ALineofDB{
    private String date;
    private int id, sunkey, sunmax;
    private boolean isover;

    public ALineofDB(int id, String date, int sunkey, int sunmax, boolean isover){
        this.id = id;
        this.sunkey = sunkey;
        this.date = date;
        this.sunmax = sunmax;
        this.isover = isover;
    }

    @Override
    public String toString() {
        return "ALineofDB{" +
                "date='" + date + '\'' +
                ", id=" + id +
                ", sunkey=" + sunkey +
                ", sunmax=" + sunmax +
                ", isover=" + isover +
                '}';
    }

    public String getDate() {
        return this.date;
    }

    public int getSunkey(){
        return this.sunkey;
    }

    public int getSunmax(){
        return this.sunmax;
    }

    public boolean getIsOver(){
        return this.isover;
    }
}