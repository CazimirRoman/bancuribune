package cazimir.com.bancuribune.ui.list;

public interface OnAllowedToAddFinishedListener {
    void isAllowedToAdd(int remainingAdds);
    void isNotAllowedToAdd(int addLimit);
}
