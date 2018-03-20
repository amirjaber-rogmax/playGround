package com.rogmax.amirjaber.playground.models;

import java.util.List;

/**
 * Created by Amir Jaber on 2/12/2018.
 */

public class Value {
    private String value;
    private String message;
    private List<LocationDto> result;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LocationDto> getResult() {
        return result;
    }

    public void setResult(List<LocationDto> result) {
        this.result = result;
    }
}
