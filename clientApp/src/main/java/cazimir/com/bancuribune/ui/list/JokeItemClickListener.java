package cazimir.com.bancuribune.ui.list;

import cazimir.com.bancuribune.model.Joke;

public interface JokeItemClickListener {
    void onItemShared(Joke data);
    void onItemVoted(String uid);
}
