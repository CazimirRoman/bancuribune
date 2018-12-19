package cazimir.com.bancuribune.view.profile;

import android.support.annotation.NonNull;
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
import cazimir.com.bancuribune.utils.UtilHelper;

public class MyJokesAdapter extends EmptyRecyclerView.Adapter<MyJokesAdapter.MyViewHolder> {

    private List<Joke> myJokes;
    private OnJokeClickListener mListener;


    public MyJokesAdapter(@NonNull OnJokeClickListener listener){
        mListener = listener;
        myJokes = new ArrayList<>();
    }

    //get position of joke in list based on jokeId that comes from push notif
    public int getItemPositionFromJokeId(String jokeId) {

        for (Joke joke : myJokes
                ) {
            if (joke.getUid().equals(jokeId)) {
                return myJokes.indexOf(joke);
            }
        }

        return 0;
    }

    class MyViewHolder extends EmptyRecyclerView.ViewHolder{

        ExpandableTextView expandableTextView;
        TextView approved;
        TextView points;
        TextView date;
        TextView share;
        View pointsLayout;

        MyViewHolder(View view){
            super(view);
            expandableTextView = view.findViewById(R.id.expand_joke_text_view);
            approved = view.findViewById(R.id.approved);
            points = view.findViewById(R.id.points);
            date = view.findViewById(R.id.date);
            pointsLayout = view.findViewById(R.id.my_points);
            share = view.findViewById(R.id.share);
        }
    }

    public void add(Joke joke){
        myJokes.add(joke);
    }

    @Override
    public MyJokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_joke_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyJokesAdapter.MyViewHolder holder, int position) {
        SparseBooleanArray mTogglePositions = new SparseBooleanArray();
        final Joke joke = myJokes.get(position);
        holder.expandableTextView.setText(joke.getJokeText(), mTogglePositions, position);
        if(joke.isApproved()){
            holder.approved.setVisibility(View.GONE);
            holder.points.setText(String.valueOf(joke.getPoints()));
            holder.pointsLayout.setVisibility(View.VISIBLE);

        }else{
            holder.approved.setText(R.string.not_approved);
            holder.approved.setVisibility(View.VISIBLE);
            holder.pointsLayout.setVisibility(View.GONE);
        }

        holder.date.setText(UtilHelper.convertEpochToDate(joke.getCreatedAt()));

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onJokeShared(joke);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myJokes.size();
    }
}
