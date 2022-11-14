package Models;

public class Applications {

    private String creatorUserId;
    private String applicantUsersId;
    private String eventId;
    private String timeCreated;
    private String dateCreated;
    private long dateTimeInMillis;
    private String status;

    public Applications() {
    }

    public Applications(String creatorUserId, String applicantUsersId, String eventId,
                        String timeCreated, String dateCreated, long dateTimeInMillis,
                        String status) {
        this.creatorUserId = creatorUserId;
        this.applicantUsersId = applicantUsersId;
        this.eventId = eventId;
        this.timeCreated = timeCreated;
        this.dateCreated = dateCreated;
        this.dateTimeInMillis = dateTimeInMillis;
        this.status = status;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getApplicantUsersId() {
        return applicantUsersId;
    }

    public void setApplicantUsersId(String applicantUsersId) {
        this.applicantUsersId = applicantUsersId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public long getDateTimeInMillis() {
        return dateTimeInMillis;
    }

    public void setDateTimeInMillis(long dateTimeInMillis) {
        this.dateTimeInMillis = dateTimeInMillis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
