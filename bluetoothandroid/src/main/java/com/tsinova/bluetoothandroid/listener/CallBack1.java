package com.tsinova.bike.core;


/**
 *  A one-argument callbcak.
 * Created by ihgoo on 2015/10/22.
 */
public interface CallBack1<T1> {

    void onError(Exception e);

    void onSuccess(T1 t1);

}
