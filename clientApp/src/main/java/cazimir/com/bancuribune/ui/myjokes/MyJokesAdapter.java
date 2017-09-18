package cazimir.com.bancuribune.ui.myjokes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.list.ItemClickListener;

public class MyJokesAdapter extends RecyclerView.Adapter<MyJokesAdapter.MyViewHolder> implements ItemClickListener {

    private List<Joke> myJokes;
    private final ItemClickListener itemClickListener;

    public MyJokesAdapter(@NonNull ItemClickListener listener){
        this.itemClickListener = listener;
        myJokes = new ArrayList<>();
    }

    @Override
    public void onItemClicked(Joke data) {

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView approved;

        MyViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.jokeText);
            approved = view.findViewById(R.id.approved);
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
        final Joke joke = myJokes.get(position);
        holder.text.setText(joke.getJokeText());
        if(joke.isApproved()){
            holder.approved.setText(R.string.approved);
        }else{
            holder.approved.setText(R.string.not_approved);
        }
    }

    @Override
    public int getItemCount() {
        return myJokes.size();
    }
}
