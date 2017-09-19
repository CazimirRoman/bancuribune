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

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> implements ItemClickListener {

    private List<Joke> jokes;
    private final ItemClickListener itemClickListener;

    public JokesAdapter(@NonNull ItemClickListener listener){
        this.itemClickListener = listener;
        jokes = new ArrayList<>();
    }
    @BindView(R.id.share) ImageButton share;

    @Override
    public void onItemClicked(Joke data) {

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView author;
        TextView share;

        MyViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.jokeText);
            author = view.findViewById(R.id.authorText);
            share = view.findViewById(R.id.share);
        }
    }

    public void add(Joke joke){
        jokes.add(joke);
    }

    @Override
    public JokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JokesAdapter.MyViewHolder holder, int position) {
        final Joke joke = jokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.author.setText(joke.getUserName());

        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(joke);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }
}
