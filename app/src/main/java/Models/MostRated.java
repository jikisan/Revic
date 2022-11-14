package Models;

public class MostRated {

    private String eventsId;
    private String eventsName;
    private String eventsUrl;
    private long ratingsCount;
    private int applicants;

    public MostRated() {
    }

    public MostRated(String eventsId, String eventsName, String eventsUrl, long ratingsCount, int applicants) {
        this.eventsId = eventsId;
        this.eventsName = eventsName;
        this.eventsUrl = eventsUrl;
        this.ratingsCount = ratingsCount;
        this.applicants = applicants;
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
}
