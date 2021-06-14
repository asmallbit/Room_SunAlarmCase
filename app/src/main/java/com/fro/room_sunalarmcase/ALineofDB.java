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

    //TODO: make the string more readable
    @Override
    public String toString() {
        return id + "   " + date + "   " + sunkey + "   "
                + sunmax + "   " + isover;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSunkey(int sunkey) {
        this.sunkey = sunkey;
    }

    public void setSunmax(int sunmax) {
        this.sunmax = sunmax;
    }

    public void setIsover(boolean isover) {
        this.isover = isover;
    }

    public int getId() {
        return id;
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

    public String deleteItem(){
        return ("ID: " + id + ", 日期: " + date + ", 光照值: " + sunkey
                + ", 光照预警上限: " + sunmax + ", 是否超过光照阈值: " + isover);
    }
}