package cazimir.com.bancuribune.callbacks.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnUpdatePointsFinishedListener {
    void OnUpdatePointsFailed(String error);
    void OnUpdatePointsSuccess(Joke joke);
}
