package cazimir.com.bancuribune.view.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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
import cazimir.com.bancuribune.callbacks.list.OnJokeClickListener;
import cazimir.com.bancuribune.callbacks.list.OnUpdateListFinished;
import cazimir.com.bancuribune.model.Joke;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> {

    private static final String TAG = JokesAdapter.class.getSimpleName();
    private List<Joke> jokes;
    private final OnJokeClickListener listener;

    public JokesAdapter(@NonNull OnJokeClickListener listener) {
        this.listener = listener;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton share;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView expandableTextView;
        TextView author;
        TextView share;
        TextView vote;
        TextView points;
        TextView heart;


        MyViewHolder(View view) {
            super(view);
            expandableTextView = view.findViewById(R.id.expand_joke_text_view);
            author = view.findViewById(R.id.authorText);
            share = view.findViewById(R.id.share);
            vote = view.findViewById(R.id.vote);
            points = view.findViewById(R.id.points);
            heart = view.findViewById(R.id.heart_icon);
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
        SparseBooleanArray mTogglePositions = new SparseBooleanArray();
        final Joke joke = jokes.get(position);
        holder.expandableTextView.setText(joke.getJokeText(), mTogglePositions, position);
        holder.author.setText(joke.getUserName());
        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onJokeShared(joke);
            }
        });

        holder.heart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onJokeVoted(jokes.get(position), position);
            }
        });

        holder.points.setText(String.valueOf(joke.getPoints()));

        holder.expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if(isExpanded){
                    listener.onJokeExpanded();
                }
            }
        });
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
