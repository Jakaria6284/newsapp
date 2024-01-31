package com.bizlijakaria.newsapp.Model;

public class model {
    String Image;
    String title;
    String publishat;
    String url;

    public model(String image, String title, String publishat, String url) {
        Image = image;
        this.title = title;
        this.publishat = publishat;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishat() {
        return publishat;
    }

    public void setPublishat(String publishat) {
        this.publishat = publishat;
    }
}
