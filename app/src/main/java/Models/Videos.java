package Models;

public class Videos {

    String userID;
    String link;
    String videoName;

    public Videos() {
    }

    public Videos(String userID, String link, String videoName) {
        this.userID = userID;
        this.link = link;
        this.videoName = videoName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
