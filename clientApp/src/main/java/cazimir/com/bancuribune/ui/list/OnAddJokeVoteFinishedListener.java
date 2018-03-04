package cazimir.com.bancuribune.ui.list;

public interface OnAddJokeVoteFinishedListener {
    void onAddJokeVoteSuccess();
    void onAddJokeVoteFailed(String databaseError);
}
