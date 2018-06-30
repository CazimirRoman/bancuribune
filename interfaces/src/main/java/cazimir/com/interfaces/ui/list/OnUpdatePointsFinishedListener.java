package cazimir.com.interfaces.ui.list;

import cazimir.com.models.Joke;

public interface OnUpdatePointsFinishedListener {
    void OnUpdatePointsFailed(String error);
    void OnUpdatePointsSuccess(Joke joke);
}
