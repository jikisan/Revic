package Models;

public class Events {

    private String imageName;
    private String imageUrl;
    private String eventName;
    private String eventAddress;
    private String eventDateSched;
    private String timeStart;
    private String timeEnd;
    private String eventDescription;
    private long dateTimeInMillis;
    private String timeCreated;
    private String dateCreated;
    private int ratings;
    private String latitude;
    private String longitude;
    private int applicants;
    private String userID;

    public Events() {

    }

    public Events(String imageName, String imageUrl, String eventName, String eventAddress,
                  String eventDateSched, String timeStart, String timeEnd, String eventDescription,
                  long dateTimeInMillis, String timeCreated, String dateCreated, int ratings,
                  String latitude, String longitude, int applicants, String userID) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.eventName = eventName;
        this.eventAddress = eventAddress;
        this.eventDateSched = eventDateSched;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.eventDescription = eventDescription;
        this.dateTimeInMillis = dateTimeInMillis;
        this.timeCreated = timeCreated;
        this.dateCreated = dateCreated;
        this.ratings = ratings;
        this.latitude = latitude;
        this.longitude = longitude;
        this.applicants = applicants;
        this.userID = userID;
    }



    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventDateSched() {
        return eventDateSched;
    }

    public void setEventDateSched(String eventDateSched) {
        this.eventDateSched = eventDateSched;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getApplicants() {
        return applicants;
    }

    public void setApplicants(int applicants) {
        this.applicants = applicants;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
