package cazimir.com.bancuribune.ui.admin;

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
import cazimir.com.interfaces.ui.admin.OnAdminJokeItemClickListener;
import cazimir.com.models.Joke;

public class AdminJokesAdapter extends RecyclerView.Adapter<AdminJokesAdapter.MyViewHolder> {

    private List<Joke> jokes;
    private final OnAdminJokeItemClickListener listener;

    public AdminJokesAdapter(@NonNull OnAdminJokeItemClickListener listener) {
        this.listener = listener;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton approve;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView author;
        TextView approve;

        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.jokeText);
            author = view.findViewById(R.id.authorText);
            approve = view.findViewById(R.id.approve);
        }
    }

    public void add(Joke joke) {
        jokes.add(joke);
    }

    @Override
    public AdminJokesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_joke_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdminJokesAdapter.MyViewHolder holder, final int position) {
        final Joke joke = jokes.get(position);
        holder.text.setText(joke.getJokeText());
        holder.author.setText(joke.getUserName());
        holder.approve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemApproved(joke.getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }
}
