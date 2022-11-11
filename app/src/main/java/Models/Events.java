package Models;

public class Events {

    private String imageName;
    private String imageUrl;
    private String eventName;
    private String eventAddress;
    private boolean isAvailableMon;
    private boolean isAvailableTue;
    private boolean isAvailableWed;
    private boolean isAvailableThu;
    private boolean isAvailableFri;
    private boolean isAvailableSat;
    private boolean isAvailableSun;
    private String timeStart;
    private String timeEnd;
    private String eventDescription;
    private long dateTimeInMillis;
    private String timeCreated;
    private String dateCreated;
    private int ratings;
    private String userID;

    public Events() {
    }

    public Events(String imageName, String imageUrl, String eventName, String eventAddress, boolean isAvailableMon,
                  boolean isAvailableTue, boolean isAvailableWed, boolean isAvailableThu, boolean isAvailableFri,
                  boolean isAvailableSat, boolean isAvailableSun, String timeStart, String timeEnd, String eventDescription,
                  long dateTimeInMillis, String timeCreated, String dateCreated, int ratings, String userID) {

        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.eventName = eventName;
        this.eventAddress = eventAddress;
        this.isAvailableMon = isAvailableMon;
        this.isAvailableTue = isAvailableTue;
        this.isAvailableWed = isAvailableWed;
        this.isAvailableThu = isAvailableThu;
        this.isAvailableFri = isAvailableFri;
        this.isAvailableSat = isAvailableSat;
        this.isAvailableSun = isAvailableSun;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.eventDescription = eventDescription;
        this.dateTimeInMillis = dateTimeInMillis;
        this.timeCreated = timeCreated;
        this.dateCreated = dateCreated;
        this.ratings = ratings;
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

    public boolean isAvailableMon() {
        return isAvailableMon;
    }

    public void setAvailableMon(boolean availableMon) {
        isAvailableMon = availableMon;
    }

    public boolean isAvailableTue() {
        return isAvailableTue;
    }

    public void setAvailableTue(boolean availableTue) {
        isAvailableTue = availableTue;
    }

    public boolean isAvailableWed() {
        return isAvailableWed;
    }

    public void setAvailableWed(boolean availableWed) {
        isAvailableWed = availableWed;
    }

    public boolean isAvailableThu() {
        return isAvailableThu;
    }

    public void setAvailableThu(boolean availableThu) {
        isAvailableThu = availableThu;
    }

    public boolean isAvailableFri() {
        return isAvailableFri;
    }

    public void setAvailableFri(boolean availableFri) {
        isAvailableFri = availableFri;
    }

    public boolean isAvailableSat() {
        return isAvailableSat;
    }

    public void setAvailableSat(boolean availableSat) {
        isAvailableSat = availableSat;
    }

    public boolean isAvailableSun() {
        return isAvailableSun;
    }

    public void setAvailableSun(boolean availableSun) {
        isAvailableSun = availableSun;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
