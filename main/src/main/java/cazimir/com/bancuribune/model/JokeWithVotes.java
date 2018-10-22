package cazimir.com.bancuribune.model;

import java.util.ArrayList;
import java.util.List;

public class JokeWithVotes {

    private String uid;
    private String jokeText;
    private boolean approved;
    private long createdAt;
    private String createdBy;
    private String userName;
    private int points;
    private List<Vote> votes = new ArrayList<>();

    public JokeWithVotes() {
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public JokeWithVotes(String text) {
        this.jokeText = text;
        this.approved = false;
        this.createdAt = System.currentTimeMillis();
        this.points = 0;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(!(o instanceof Joke)){
            return false;
        }

        Joke joke = (Joke) o;

        return joke.getUid().equals(uid);
    }

    public void addVoteToList(Vote vote) {
        this.votes.add(vote);
    }
}