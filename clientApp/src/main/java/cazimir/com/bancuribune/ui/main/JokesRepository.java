package cazimir.com.bancuribune.ui.main;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public class JokesRepository implements IJokesRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference jokesRef = database.getReference("jokes");


    @Override
    public void getAllJokes(final OnRequestFinishedListener listener) {

        jokesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<Joke> jokes = new ArrayList<>();

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    jokes.add(joke);
                }

                listener.onSuccess(jokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    @Override
    public void addJoke(final OnAddFinishedListener listener, Joke joke) {
        jokesRef.push().setValue(joke);
        listener.OnAddSuccess();
    }
}
