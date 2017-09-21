package cazimir.com.bancuribune.ui.list;

public interface OnCheckIfVotedFinishedListener {
    void OnHasVotedTrue();
    void OnHasVotedFalse(String uid);
}
