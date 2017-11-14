package com.tsinova.bluetoothandroid.exception;

/**
 * Created by ihgoo on 2017/11/13.
 */

public class TsinovaBikeBtNumberNotFoundException extends RuntimeException  {

    private static final long serialVersionUID = 1L;


    public TsinovaBikeBtNumberNotFoundException(){

    }

    public TsinovaBikeBtNumberNotFoundException(String message) {
        super(message);
    }



}