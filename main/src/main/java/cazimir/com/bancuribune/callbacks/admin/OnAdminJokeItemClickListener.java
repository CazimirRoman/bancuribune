package cazimir.com.bancuribune.callbacks.admin;

import cazimir.com.bancuribune.model.Joke;

public interface OnAdminJokeItemClickListener {
    void onItemApproved(String uid, String jokeText);
    void onItemDeleted(Joke joke);
}
