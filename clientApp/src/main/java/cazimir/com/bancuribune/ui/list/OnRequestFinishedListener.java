package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnRequestFinishedListener {
    void onSuccess(List<Joke> jokes);
    void onError(String error);
}
