package cazimir.com.bancuribune.ui.likedJokes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.EmptyRecyclerView;
import cazimir.com.bancuribune.model.Joke;

public class LikedJokesAdapter extends EmptyRecyclerView.Adapter<LikedJokesAdapter.MyViewHolder> {

    private List<Joke> myLikedJokes;

    public LikedJokesAdapter(){
        myLikedJokes = new ArrayList<>();
    }

    class MyViewHolder extends EmptyRecyclerView.ViewHolder{

        TextView text;
        TextView approved;
        TextView points;
        TextView date;
        View pointsLayout;

        MyViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.jokeText);
            approved = view.findViewById(R.id.approved);
            points = view.findViewById(R.id.points);
            date = view.findViewById(R.id.date);
            pointsLayout = view.findViewById(R.id.my_points);
        }
    }

    public void add(Joke joke){
        myLikedJokes.add(joke);
    }

    @Override
    public LikedJokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_joke_list_row, parent, false);
        return new LikedJokesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LikedJokesAdapter.MyViewHolder holder, int position) {
        final Joke joke = myLikedJokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.points.setText(String.valueOf(joke.getPoints()));
        holder.date.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return myLikedJokes.size();
    }
}
