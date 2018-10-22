package cazimir.com.bancuribune.callbacks.list;

import cazimir.com.bancuribune.model.Joke;

public interface OnUpdatePointsFinishedListener {
    void OnUpdatePointsSuccess(Joke joke);
    void OnUpdatePointsFailed(String error);
}
