package com.rogmax.amirjaber.playground.models;

/**
 * Created by Amir Jaber on 3/6/2018.
 */

public class PagerDto {

    private String id;
    private String title;

    public PagerDto(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
