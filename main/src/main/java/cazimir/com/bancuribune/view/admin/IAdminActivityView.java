package cazimir.com.bancuribune.view.admin;

import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface IAdminActivityView extends IGeneralView {
    void getAllPendingJokes();
    void refreshJokes(List<Joke> jokes);
    void showToast(String message);
}
