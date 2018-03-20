package com.rogmax.amirjaber.playground.retrofit;

import com.rogmax.amirjaber.playground.facades.MainFacade;

/**
 * Created by Amir Jaber on 1/30/2018.
 */

public class APIUtils {
    public APIUtils() {
    }

    private static final String API_URL = "http://unitestproj.tk/unicrud/";

    public static MainFacade getFacadeService() {
        return RetrofitClient.getClient(API_URL).create(MainFacade.class);
    }
}
