package com.xbw.unmanned.model;

/**
 * Created by xubowen on 2017/5/25.
 */
public class GoodsModel {
    private String goodsName;
    private float goodsPrice;
    private String goodsImg;
    private String goodsUrl;

    public String getGoodsName(){
        return goodsName;
    }
    public float getGoodsPrice(){
        return goodsPrice;
    }
    public String getGoodsImg(){
        return goodsImg;
    }
    public String getGoodsUrl(){
        return goodsUrl;
    }
    public void setGoodsName(String s){
        this.goodsName=s;
    }
    public void setGoodsPrice(float s){
        this.goodsPrice=s;
    }
    public void setGoodsImg(String s){
        this.goodsImg=s;
    }
    public void setGoodsUrl(String s){
        this.goodsUrl=s;
    }
}
