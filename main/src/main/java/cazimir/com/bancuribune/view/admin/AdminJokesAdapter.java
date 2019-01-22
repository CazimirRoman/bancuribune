package cazimir.com.bancuribune.view.admin;

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
import cazimir.com.bancuribune.callbacks.admin.OnAdminJokeItemClickListener;
import cazimir.com.bancuribune.model.Joke;

import static cazimir.com.bancuribune.constant.Constants.NO_MODIFICATIONS;

public class AdminJokesAdapter extends RecyclerView.Adapter<AdminJokesAdapter.MyViewHolder> {

    private List<Joke> jokes;
    private final OnAdminJokeItemClickListener listener;
    private boolean editStarted = false;

    public AdminJokesAdapter(@NonNull OnAdminJokeItemClickListener listener) {
        this.listener = listener;
        jokes = new ArrayList<>();
    }

    @BindView(R.id.share)
    ImageButton approve;

    class MyViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView expandableTextView;
        TextView expandableText;
        TextView author;
        TextView approve;
        TextView delete;
        EditText edit;

        MyViewHolder(View view) {
            super(view);
            expandableTextView = view.findViewById(R.id.expand_joke_text_view);
            expandableText = view.findViewById(R.id.expandable_text);
            author = view.findViewById(R.id.authorText);
            approve = view.findViewById(R.id.approve);
            delete = view.findViewById(R.id.delete);
            edit = view.findViewById(R.id.expandable_text_edit);
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
        SparseBooleanArray mTogglePositions = new SparseBooleanArray();
        final Joke joke = jokes.get(position);
        holder.expandableTextView.setText(joke.getJokeText(), mTogglePositions, position);
        holder.author.setText(joke.getUserName());
        holder.approve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editStarted){
                    listener.onItemApproved(joke.getUid(), holder.edit.getText().toString());
                    return;
                }

                listener.onItemApproved(joke.getUid(), NO_MODIFICATIONS);

            }
        });
        holder.expandableText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editStarted = true;
                holder.expandableTextView.setVisibility(View.GONE);
                holder.edit.setText(joke.getJokeText());
                holder.edit.setVisibility(View.VISIBLE);
                return true;
            }
        });

        holder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemDeleted(joke);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }
}
