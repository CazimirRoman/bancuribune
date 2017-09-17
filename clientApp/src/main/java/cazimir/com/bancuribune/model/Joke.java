package cazimir.com.bancuribune.model;

public class Joke {
    private String jokeText;
    private boolean approved;
    private long createdAt;
    private String createdBy;
    private String userName;


    public Joke() {
    }

    public Joke(String text) {
        this.jokeText = text;
        this.approved = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getJokeText() {
        return jokeText;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
