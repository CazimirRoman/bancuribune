package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnFirebaseGetAllJokesListener {
    void onSuccess(List<Joke> jokes);
    void onError(String error);
}
