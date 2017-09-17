package cazimir.com.bancuribune.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> {

    private List<Joke> jokes;
    public JokesAdapter(){
        jokes = new ArrayList<>();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView author;

        MyViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.jokeText);
            author = view.findViewById(R.id.authorText);
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
    public void onBindViewHolder(JokesAdapter.MyViewHolder holder, int position) {
        Joke joke = jokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.author.setText(joke.getUserName());
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }
}
