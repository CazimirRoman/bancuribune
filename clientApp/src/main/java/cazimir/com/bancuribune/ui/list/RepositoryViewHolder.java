package cazimir.com.bancuribune.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cazimir.com.bancuribune.R;

public class RepositoryViewHolder extends RecyclerView.ViewHolder implements RepositoryListView {

    TextView joke;
    TextView author;
    TextView points;

    public RepositoryViewHolder(View itemView) {
        super(itemView);
        joke = itemView.findViewById(R.id.jokeText);
        author = itemView.findViewById(R.id.authorText);
        points = itemView.findViewById(R.id.points);
    }

    @Override
    public void setJokeText(String text) {
        joke.setText(text);
    }

    @Override
    public void setAuthorText(String text) {
        author.setText(text);
    }

    @Override
    public void setPointsText(String text) {
        points.setText(text);
    }
}
