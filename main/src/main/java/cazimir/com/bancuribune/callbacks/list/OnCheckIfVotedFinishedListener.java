package cazimir.com.bancuribune.callbacks.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnCheckIfVotedFinishedListener {
    void onHasVotedTrue();
    void onHasVotedFalse(Joke joke);
}
