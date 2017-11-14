package com.tsinova.bluetoothandroid.exception;

import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;

/**
 * Created by ihgoo on 2017/11/13.
 */

public class TsinovaApplicationNotFoundException extends RuntimeException  {

    private static final long serialVersionUID = 1L;


    public TsinovaApplicationNotFoundException(){

    }

    public TsinovaApplicationNotFoundException(String message) {
        super(message);
    }



}
