package cazimir.com.interfaces.ui.list;

import cazimir.com.models.Joke;

public interface OnCheckIfVotedFinishedListener {
    void onHasVotedTrue();
    void onHasVotedFalse(Joke joke);
}
