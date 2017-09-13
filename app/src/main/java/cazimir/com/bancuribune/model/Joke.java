package cazimir.com.bancuribune.model;

import java.util.Random;

public class Joke {
    private Integer id;

    public Joke(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

    public Joke() {
        this.id = new Random().nextInt(50) + 1;
        this.text = "Jokes text " + this.id;
    }

    private String text;
}
