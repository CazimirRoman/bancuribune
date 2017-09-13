package cazimir.com.bancuribune.model;

public class Joke {
    private Integer jokeId;
    private String jokeText;
    private boolean approved;

    public Joke() {
    }

    public Joke(Integer id, String text) {
        this.jokeId = id;
        this.jokeText = text;
        this.approved = false;
    }

    public Integer getJokeId() {
        return jokeId;
    }

    public String getJokeText() {
        return jokeText;
    }

    public boolean isApproved() {
        return approved;
    }

}
