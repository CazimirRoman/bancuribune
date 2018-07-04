package cazimir.com.bancuribune.ui.myJokes;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.utils.EmptyRecyclerView;
import cazimir.com.models.Joke;
import cazimir.com.utils.UtilHelper;

public class MyJokesAdapter extends EmptyRecyclerView.Adapter<MyJokesAdapter.MyViewHolder> {

    private List<Joke> myJokes;

    public MyJokesAdapter(){
        myJokes = new ArrayList<>();
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
            holder.approved.setText(R.string.approved);
            holder.points.setText(String.valueOf(joke.getPoints()));
        }else{
            holder.approved.setText(R.string.not_approved);
            holder.pointsLayout.setVisibility(View.GONE);
        }

        holder.date.setText(UtilHelper.convertEpochToDate(joke.getCreatedAt()));
        holder.share.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return myJokes.size();
    }
}
