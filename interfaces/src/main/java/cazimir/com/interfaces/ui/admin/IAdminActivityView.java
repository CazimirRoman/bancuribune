package cazimir.com.interfaces.ui.admin;

import java.util.List;
;import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Joke;

public interface IAdminActivityView extends IGeneralView {
    void getAllPendingJokes();
    void refreshJokes(List<Joke> jokes);
    void showToast(String message);
}
