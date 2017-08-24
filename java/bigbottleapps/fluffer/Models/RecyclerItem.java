package bigbottleapps.fluffer.Models;

public class RecyclerItem {

    private String title;
    private String image;
    private Integer progress;
    private String id;

    public RecyclerItem(String title, int likes, int dislikes, String image, String id) {
        this.title = title;
        this.image = image;
        this.progress = calcProgress(likes, dislikes);
        this.id = id;
    }

    String getId(){
        return id;
    }

    String getTitle() {
        return title;
    }

    int getProgress(){
        return progress;
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

    String getImage(){
        return this.image;
    }

}