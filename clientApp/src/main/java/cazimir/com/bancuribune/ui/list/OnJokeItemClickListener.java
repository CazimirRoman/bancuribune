package cazimir.com.bancuribune.ui.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnJokeItemClickListener {
    void onItemShared(Joke data);
    void onItemVoted(Joke joke);
}
