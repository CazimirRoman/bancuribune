package cazimir.com.bancuribune.ui.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> implements JokeItemClickListener {

    private List<Joke> jokes;
    private final JokeItemClickListener listener;

    public JokesAdapter(@NonNull JokeItemClickListener listener) {
        this.listener = listener;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton share;

    @Override
    public void onItemShared(Joke data) {

    }

    @Override
    public void onItemVoted(String uid) {

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView author;
        TextView share;
        TextView vote;
        TextView points;

        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.jokeText);
            author = view.findViewById(R.id.authorText);
            share = view.findViewById(R.id.share);
            vote = view.findViewById(R.id.vote);
            points = view.findViewById(R.id.points);
        }
    }

    public void add(Joke joke) {
        jokes.add(joke);
    }

    @Override
    public JokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JokesAdapter.MyViewHolder holder, final int position) {
        final Joke joke = jokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.author.setText(joke.getUserName());

        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemShared(joke);
            }
        });

        holder.vote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemVoted(jokes.get(position).getUid());
            }
        });

        holder.points.setText(String.valueOf(joke.getPoints()));
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    public List<Joke> getJokesList() {
        return this.jokes;
    }
}
