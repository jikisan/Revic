package Models;

public class Ratings {

    String ratingOfId;
    String ratingOfName;
    String ratedById;
    String ratedByName;
    double ratingValue;
    String ratingMessage;
    String ratingType;
    String contractID;

    public Ratings() {
    }

    public Ratings(String ratingOfId, String ratingOfName, String ratedById, String ratedByName,
                   double ratingValue, String ratingMessage, String ratingType, String contractID)
    {
        this.ratingOfId = ratingOfId;
        this.ratingOfName = ratingOfName;
        this.ratedById = ratedById;
        this.ratedByName = ratedByName;
        this.ratingValue = ratingValue;
        this.ratingMessage = ratingMessage;
        this.ratingType = ratingType;
        this.contractID = contractID;
    }

    public String getRatingOfId() {
        return ratingOfId;
    }

    public void setRatingOfId(String ratingOfId) {
        this.ratingOfId = ratingOfId;
    }

    public String getRatingOfName() {
        return ratingOfName;
    }

    public void setRatingOfName(String ratingOfName) {
        this.ratingOfName = ratingOfName;
    }

    public String getRatedById() {
        return ratedById;
    }

    public void setRatedById(String ratedById) {
        this.ratedById = ratedById;
    }

    public String getRatedByName() {
        return ratedByName;
    }

    public void setRatedByName(String ratedByName) {
        this.ratedByName = ratedByName;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }

    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }
}
