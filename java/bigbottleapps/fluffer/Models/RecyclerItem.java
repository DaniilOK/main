package bigbottleapps.fluffer.Models;

import android.graphics.drawable.Drawable;

public class RecyclerItem {

    private String title;
    private String description;
    private String image;
    private Integer likes;
    private Integer dislikes;
    private Integer progress;
    private Integer current;
    private String id;
    private String user;
    private Drawable upBlack, upBlue, downBlack, downBlue;

    public RecyclerItem(String title, int likes, int dislikes, String image, String description, String id, String user
            , Drawable upBlack, Drawable upBlue, Drawable downBlack, Drawable downBlue, Integer current) {
        this.title = title;
        this.likes = likes;
        this.dislikes = dislikes;
        this.description = description;
        this.image = image;
        this.progress = calcProgress(likes, dislikes);
        this.id = id;
        this.user = user;
        this.upBlack = upBlack;
        this.upBlue = upBlue;
        this.downBlack = downBlack;
        this.downBlue = downBlue;
        this.current = current;
    }

    String getId(){
        return id;
    }

    String getUser(){
        return user;
    }

    String getTitle() {
        return title;
    }

    int getLikes() {
        return likes;
    }

    void setLikes(int likes) {
        this.likes = likes;
    }

    int getDislikes() {
        return dislikes;
    }

    void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    int getProgress(){
        return progress;
    }

    void setProgress(int likes, int dislikes){
        this.progress = calcProgress(likes, dislikes);
    }

    private int calcProgress(int likes, int dislikes){
        if((likes==0)&&(dislikes!=0))
            return 0;
        if((likes!=0)&&(dislikes==0))
            return 100;
        if ((likes!=0)&&(dislikes!=0))
            return likes*100/(likes+dislikes);
        return 0;
    }

    String getDescription(){
        return description;
    }

    String getImage(){
        return this.image;
    }

    Integer getCurrent() {
        return current;
    }

    Drawable getUpBlack() {
        return upBlack;
    }

    Drawable getUpBlue() {
        return upBlue;
    }

    Drawable getDownBlack() {
        return downBlack;
    }

    Drawable getDownBlue() {
        return downBlue;
    }
}