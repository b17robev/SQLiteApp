package org.brohede.marcus.sqliteapp;

/**
 * Created by marcus on 2018-04-25.
 */

public class Mountain {

    private String name;
    private int height;
    private String imageUrl;
    private String location;
    private String wikipediaPage;
    private int id;

    public Mountain(String name, int height, String location, String imageUrl, String wikipediaPage, int id){
        this.name = name;
        this.height = height;
        this.location = location;
        this.imageUrl = imageUrl;
        this.wikipediaPage = wikipediaPage;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getHeight(){
        return height;
    }

    public String getLocation(){
        return location;
    }

    public String getImage(){
        return imageUrl;
    }

    public String getWikipediaPage(){
        return wikipediaPage;
    }

    public int getId(){
        return id;
    }

    public String infoText(){
        return name + " is a part of the " + location + " mountain range and is " + height + "m high.";
    }
}