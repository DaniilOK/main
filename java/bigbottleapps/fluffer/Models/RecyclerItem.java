package bigbottleapps.fluffer.Models;

public class RecyclerItem {

    private String title;
    private String image;
    private Integer progress;
    private String id;
    private String date;
    private int type;

    public RecyclerItem(String title, int likes, int dislikes, String image, String id, int type, String date) {
        this.title = title;
        this.image = image;
        this.progress = calcProgress(likes, dislikes);
        this.id = id;
        this.type = type;
        this.date = date;
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
        if((likes==0)&&(dislikes==0))
            return -1;
        if ((likes!=0)&&(dislikes!=0))
            return likes*100/(likes+dislikes);
        return 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String getImage(){
        return this.image;
    }

    int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}