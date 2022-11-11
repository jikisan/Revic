package Models;

public class Posts {


    private String postMessage;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private long dateTimeInMillis;
    private String timeCreated;
    private String dateCreated;
    private int ratings;
    private String userId;

    public Posts() {
    }

    public Posts(String postMessage, String fileUrl, String fileName, String fileType,
                 long dateTimeInMillis, String timeCreated, String dateCreated, int ratings,
                 String userId) {
        this.postMessage = postMessage;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.dateTimeInMillis = dateTimeInMillis;
        this.timeCreated = timeCreated;
        this.dateCreated = dateCreated;
        this.ratings = ratings;
        this.userId = userId;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getDateTimeInMillis() {
        return dateTimeInMillis;
    }

    public void setDateTimeInMillis(long dateTimeInMillis) {
        this.dateTimeInMillis = dateTimeInMillis;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
