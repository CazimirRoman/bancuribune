package cazimir.com.interfaces.ui.list;

public interface OnAllowedToAddFinishedListener {
    void isAllowedToAdd(int remainingAdds);
    void isNotAllowedToAdd(int addLimit);
}
