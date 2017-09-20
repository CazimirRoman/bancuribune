package cazimir.com.bancuribune.ui.list;

import cazimir.com.bancuribune.model.Joke;

public interface ItemClickListener {
    void onItemShared(Joke data);
    void onItemVoted(String uid);
}
