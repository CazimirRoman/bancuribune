package cazimir.com.bancuribune.ui.main;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

interface OnRequestFinishedListener {
    void onSuccess(List<Joke> jokes);
    void onError(String error);
}
