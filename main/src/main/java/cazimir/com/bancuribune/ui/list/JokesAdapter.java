package cazimir.com.bancuribune.ui.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.interfaces.ui.list.OnJokeClickListener;
import cazimir.com.interfaces.ui.list.OnUpdateListFinished;
import cazimir.com.models.Joke;
import cazimir.com.utils.UtilHelper;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> {

    private List<Joke> jokes;
    private final OnJokeClickListener listener;

    public JokesAdapter(@NonNull OnJokeClickListener listener) {
        this.listener = listener;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton share;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView text;
        TextView author;
        TextView share;
        TextView vote;
        TextView points;
        TextView date;
        TextView heart;


        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.expand_joke_text_view);
            author = view.findViewById(R.id.authorText);
            share = view.findViewById(R.id.share);
            vote = view.findViewById(R.id.vote);
            points = view.findViewById(R.id.points);
            date = view.findViewById(R.id.date);
            heart = view.findViewById(R.id.heart_icon);
        }
    }

    public void add(Joke joke) {
        jokes.add(joke);
    }

    @Override
    public JokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_list_row, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final JokesAdapter.MyViewHolder holder, final int position) {
        final Joke joke = jokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.author.setText(joke.getUserName());
        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onJokeShared(joke);
            }
        });

        holder.vote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onJokeVoted(jokes.get(position));
            }
        });

        holder.points.setText(String.valueOf(joke.getPoints()));
        holder.date.setText(UtilHelper.convertEpochToDate(joke.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    public void updatePoints(OnUpdateListFinished listener, Joke joke){
        int index = jokes.indexOf(joke);
        jokes.set(index, joke);
        notifyItemChanged(index);
        listener.onUpdateSuccess(index);

    }
}
