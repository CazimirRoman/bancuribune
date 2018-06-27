package cazimir.com.interfaces.ui.list;

public interface OnAddJokeVoteFinishedListener {
    void onAddJokeVoteSuccess();
    void onAddJokeVoteFailed(String databaseError);
}
