package cazimir.com.bancuribune.presenter;

import cazimir.com.bancuribune.model.Joke;

public interface ICommonPresenter {
    void getAllJokesData();
    void getMyJokes();
    void addJoke(Joke joke);
    void checkNumberOfAdds();
    void isAllowedToAdd();
    void isNotAllowedToAdd();
    void logOutUser();
}
