package cazimir.com.bancuribune.ui.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnCheckIfVotedFinishedListener {
    void OnHasVotedTrue();
    void OnHasVotedFalse(Joke joke);
}
