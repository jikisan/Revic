package Models;

public class MostRated {

    private String eventsId;
    private String eventsName;
    private String eventsUrl;
    private long ratingsCount;
    private int applicants;
    private double distance;
    private String eventAddress;

    public MostRated() {
    }

    public MostRated(String eventsId, String eventsName, String eventsUrl, long ratingsCount, int applicants, double distance, String eventAddress) {
        this.eventsId = eventsId;
        this.eventsName = eventsName;
        this.eventsUrl = eventsUrl;
        this.ratingsCount = ratingsCount;
        this.applicants = applicants;
        this.distance = distance;
        this.eventAddress = eventAddress;
    }

    public String getEventsId() {
        return eventsId;
    }

    public void setEventsId(String eventsId) {
        this.eventsId = eventsId;
    }

    public String getEventsName() {
        return eventsName;
    }

    public void setEventsName(String eventsName) {
        this.eventsName = eventsName;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public int getApplicants() {
        return applicants;
    }

    public void setApplicants(int applicants) {
        this.applicants = applicants;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }
}
