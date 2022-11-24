package Models;

public class Notifications {

    private long transactionDateInMillis;
    private String transactionDate;
    private String transactionTime;
    private String notificationType;
    private String notificationMessage;
    private String contractId;
    private String userId;
    private String chatId;
    private String eventId;

    public Notifications() {
    }

    public Notifications(long transactionDateInMillis, String transactionDate,
                         String transactionTime, String notificationType, String notificationMessage,
                         String contractId, String userId, String chatId, String eventId) {
        this.transactionDateInMillis = transactionDateInMillis;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.contractId = contractId;
        this.userId = userId;
        this.chatId = chatId;
        this.eventId = eventId;
    }

    public long getTransactionDateInMillis() {
        return transactionDateInMillis;
    }

    public void setTransactionDateInMillis(long transactionDateInMillis) {
        this.transactionDateInMillis = transactionDateInMillis;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
