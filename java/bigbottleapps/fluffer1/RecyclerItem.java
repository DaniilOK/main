package bigbottleapps.fluffer1;

public class RecyclerItem {

    private String title;
    private String description;
    private String image;
    private Integer likes;
    private Integer dislikes;
    private Integer progress;


    public RecyclerItem(String title, int likes, int dislikes, String image, String description) {
        this.title = title;
        this.likes = likes;
        this.dislikes = dislikes;
        this.description = description;
        this.image = image;
        this.progress = calcProgress(likes, dislikes);;
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

    int calcProgress(int likes, int dislikes){
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
}
