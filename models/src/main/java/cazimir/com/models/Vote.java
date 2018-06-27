package cazimir.com.models;



public class Vote {

    private String uid;
    private String jokeId;
    private String votedBy;
    private long votedAt;

    public Vote() {
        this.votedAt = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJokeId() {
        return jokeId;
    }

    public void setJokeId(String jokeId) {
        this.jokeId = jokeId;
    }

    public String getVotedBy() {
        return votedBy;
    }

    public void setVotedBy(String votedBy) {
        this.votedBy = votedBy;
    }

    public long getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(long votedAt) {
        this.votedAt = votedAt;
    }
}
