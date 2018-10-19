package cazimir.com.bancuribune.callbacks.list;

public interface OnAllowedToAddFinishedListener {
    void isAllowedToAdd(int remainingAdds);
    void isNotAllowedToAdd(int addLimit);
}
