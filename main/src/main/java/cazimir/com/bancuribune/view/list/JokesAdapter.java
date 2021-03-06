package cazimir.com.bancuribune.view.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
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

import static cazimir.com.bancuribune.constant.Constants.NO_MODIFICATIONS;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> {

    private static final String TAG = JokesAdapter.class.getSimpleName();
    private List<Joke> jokes;
    private OnJokeClickListener mListener;
    private boolean mAdmin;
    private boolean editStarted = false;

    public JokesAdapter(@NonNull OnJokeClickListener listener, boolean admin) {
        mListener = listener;
        mAdmin = admin;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton share;

    public int getPositionBasedOnJokeId(String jokeId) {

        for (Joke joke: jokes){
            if(joke.getUid().equals(jokeId))
            return jokes.indexOf(joke);
        }

        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView expandableTextView;
        TextView expandableText;
        TextView author;
        TextView share;
        TextView vote;
        TextView points;
        TextView heart;
        EditText edit;
        TextView approve;

        MyViewHolder(View view) {
            super(view);
            expandableTextView = view.findViewById(R.id.expand_joke_text_view);
            expandableText = view.findViewById(R.id.expandable_text);
            author = view.findViewById(R.id.authorText);
            share = view.findViewById(R.id.share);
            vote = view.findViewById(R.id.vote);
            points = view.findViewById(R.id.points);
            heart = view.findViewById(R.id.heart_icon);
            edit = view.findViewById(R.id.expandable_text_edit);
            approve = view.findViewById(R.id.approve);

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
    public void onBindViewHolder(@NonNull final JokesAdapter.MyViewHolder holder, final int position) {
        SparseBooleanArray mTogglePositions = new SparseBooleanArray();
        final Joke joke = jokes.get(position);
        holder.expandableTextView.setText(joke.getJokeText(), mTogglePositions, position);
        holder.author.setText(joke.getUserName());
        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onJokeShared(joke);
            }
        });

        holder.heart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onJokeVoted(jokes.get(position), position);
            }
        });

        holder.points.setText(String.valueOf(joke.getPoints()));

        holder.expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if(isExpanded){
                    mListener.onJokeExpanded();
                }
            }
        });

        holder.approve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editStarted){
                    mListener.onJokeModified(joke.getUid(), holder.edit.getText().toString());
                    holder.approve.setVisibility(View.GONE);
                    return;
                }

                mListener.onJokeModified(joke.getUid(), NO_MODIFICATIONS);

            }
        });

        if(mAdmin){
            holder.expandableText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editStarted = true;
                    holder.expandableTextView.setVisibility(View.GONE);
                    holder.edit.setText(joke.getJokeText());
                    holder.edit.setVisibility(View.VISIBLE);
                    holder.approve.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
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
