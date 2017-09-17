package cazimir.com.bancuribune.presenter;

import cazimir.com.bancuribune.model.Joke;

public interface ICommonPresenter {
    void getAllJokesData();
    void addJoke(Joke joke);
    void checkNumberOfAdds();
    void isAllowedToAdd();
    void isNotAllowedToAdd();
}
