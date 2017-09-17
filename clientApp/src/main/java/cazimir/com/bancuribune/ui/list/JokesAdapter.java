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
import cazimir.com.bancuribune.presenter.CommonPresenter;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.MyViewHolder> {

    private CommonPresenter presenter;
    private List<Joke> jokes;
    public JokesAdapter(){
        jokes = new ArrayList<Joke>();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView text;

        MyViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.jokeText);
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
        if(!joke.isApproved()){
            holder.itemView.setVisibility(View.GONE);
        }else{
            holder.text.setText(joke.getJokeText());
        }

    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }
}
