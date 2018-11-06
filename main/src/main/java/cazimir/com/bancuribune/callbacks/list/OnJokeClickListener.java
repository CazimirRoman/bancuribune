package cazimir.com.bancuribune.callbacks.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnJokeClickListener {
    void onJokeShared(Joke data);
    void onJokeVoted(Joke joke, int position);
    void onJokeExpanded();
    void onJokeUnlike(Joke joke, int position);
}
