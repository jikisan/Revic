package Models;

public class Contracts {

    private String imageUrl;
    private String userName;
    private String userCategory;
    private String eventName;
    private String eventDate;
    private long eventDateInMillis;
    private String eventId;
    private String employeeId;
    private String creatorUserId;
    private String contractStatus;
    private Boolean isRatedByMusician;
    private Boolean isRatedByCreator;

    public Contracts() {
    }

    public Contracts(String imageUrl, String userName, String userCategory, String eventName,
                     String eventDate, long eventDateInMillis, String eventId, String employeeId,
                     String creatorUserId, String contractStatus, Boolean isRatedByMusician,
                     Boolean isRatedByCreator) {
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userCategory = userCategory;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDateInMillis = eventDateInMillis;
        this.eventId = eventId;
        this.employeeId = employeeId;
        this.creatorUserId = creatorUserId;
        this.contractStatus = contractStatus;
        this.isRatedByMusician = isRatedByMusician;
        this.isRatedByCreator = isRatedByCreator;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public long getEventDateInMillis() {
        return eventDateInMillis;
    }

    public void setEventDateInMillis(long eventDateInMillis) {
        this.eventDateInMillis = eventDateInMillis;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Boolean getRatedByMusician() {
        return isRatedByMusician;
    }

    public void setRatedByMusician(Boolean ratedByMusician) {
        isRatedByMusician = ratedByMusician;
    }

    public Boolean getRatedByCreator() {
        return isRatedByCreator;
    }

    public void setRatedByCreator(Boolean ratedByCreator) {
        isRatedByCreator = ratedByCreator;
    }
}
