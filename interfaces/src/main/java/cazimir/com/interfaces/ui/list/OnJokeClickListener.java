package cazimir.com.interfaces.ui.list;

import cazimir.com.models.Joke;

public interface OnJokeClickListener {
    void onJokeShared(Joke data);
    void onJokeVoted(Joke joke);
    void onJokeExpanded();
}
