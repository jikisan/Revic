package Models;

public class Photos {

    String userID;
    String link;
    String photoName;

    public Photos() {
    }

    public Photos(String userID, String link, String photoName) {
        this.userID = userID;
        this.link = link;
        this.photoName = photoName;
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

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

}
