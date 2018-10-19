package cazimir.com.bancuribune.callbacks.list;

public interface OnAddJokeVoteFinishedListener {
    void onAddJokeVoteSuccess();
    void onAddJokeVoteFailed(String databaseError);
}
