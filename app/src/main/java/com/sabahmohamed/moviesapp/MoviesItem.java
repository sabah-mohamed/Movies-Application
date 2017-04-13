package com.sabahmohamed.moviesapp;

import java.io.Serializable;

/**
 * Created by ba7 on 12/3/2016.
 */

public class MoviesItem implements Serializable {

    private String id;
    private String title;
    private String year;
    private String rating;
    private String description;
    private String imageResource;

    public MoviesItem(String id, String title, String description, String year, String rating, String imageResource) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.imageResource = imageResource;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }
}
