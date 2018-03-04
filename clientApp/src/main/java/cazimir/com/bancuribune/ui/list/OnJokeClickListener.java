package cazimir.com.bancuribune.ui.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnJokeClickListener {
    void onJokeShared(Joke data);
    void onJokeVoted(Joke joke);
}
