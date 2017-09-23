package cazimir.com.bancuribune.ui.admin;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;


public interface IAdminActivityView {
    void getAllPendingJokes();
    void refreshJokes(List<Joke> jokes);
    void showToast(String message);
}
