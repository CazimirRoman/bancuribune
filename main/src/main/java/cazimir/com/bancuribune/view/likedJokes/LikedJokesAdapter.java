package cazimir.com.bancuribune.view.likedJokes;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.callbacks.list.OnJokeClickListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.utils.EmptyRecyclerView;

public class LikedJokesAdapter extends EmptyRecyclerView.Adapter<LikedJokesAdapter.MyViewHolder> {

    private List<Joke> myLikedJokes;
    private final OnJokeClickListener listener;


    public LikedJokesAdapter(OnJokeClickListener listener){
        myLikedJokes = new ArrayList<>();
        this.listener = listener;
    }

    public void resetLikedJokesList() {
        myLikedJokes = new ArrayList<>();
    }

    class MyViewHolder extends EmptyRecyclerView.ViewHolder{

        ExpandableTextView expandableTextView;
        TextView approved;
        TextView points;
        TextView share;
        TextView remove;
        View pointsLayout;

        MyViewHolder(View view){
            super(view);
            expandableTextView = view.findViewById(R.id.expand_joke_text_view);
            approved = view.findViewById(R.id.approved);
            points = view.findViewById(R.id.points);
            pointsLayout = view.findViewById(R.id.my_points);
            share = view.findViewById(R.id.share);
            remove = view.findViewById(R.id.heart_icon);
        }
    }

    public void add(Joke joke){
        myLikedJokes.add(joke);
    }

    public void remove(Joke joke){
        myLikedJokes.remove(myLikedJokes.get(myLikedJokes.indexOf(joke)));
    }

    @Override
    public LikedJokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_joke_row, parent, false);
        return new LikedJokesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LikedJokesAdapter.MyViewHolder holder, final int position) {
        SparseBooleanArray mTogglePositions = new SparseBooleanArray();
        final Joke joke = myLikedJokes.get(position);
        holder.expandableTextView.setText(joke.getJokeText(), mTogglePositions, position);
        holder.points.setText(String.valueOf(joke.getPoints()));
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onJokeShared(joke);
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onJokeUnlike(myLikedJokes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myLikedJokes.size();
    }
}
