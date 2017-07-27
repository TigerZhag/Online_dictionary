package com.readboy.online.model;

/**
 * 想要获取网络请求数据的类,就继承接口,实现getDataResult方法就能获得请求结果result
 */
public interface HttpGetDataListener {

    /*   获取请求结果,可以在此方法对请求结果进行json解析   */
    void getDataResult(String result);
}