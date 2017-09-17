package cazimir.com.bancuribune.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnRequestAllFinishedListener;
import cazimir.com.bancuribune.utils.Utils;

public class JokesRepository implements IJokesRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference jokesRef = database.getReference("jokes");

    @Override
    public void getAllJokes(final OnRequestAllFinishedListener listener) {

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

    @Override
    public void getAllJokesAddedToday(final OnAllowedToAddFinishedListener listener, String userId) {

        final ArrayList<Joke> addedJokesToday = new ArrayList<>();

            Query query = jokesRef.orderByChild("createdBy").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                        Joke joke = markerSnapshot.getValue(Joke.class);
                        assert joke != null;
                        Date createdAt = new Date(joke.getCreatedAt());
                        long now = new Date().getTime();
                        Date nowDate = new Date(now);

                        if (Utils.isSameDay(createdAt, nowDate)) {
                            addedJokesToday.add(joke);
                        }
                    }

                    if (addedJokesToday.size() <= Constants.ADD_JOKE_LIMIT) {
                        listener.isAllowedToAdd();
                    } else {
                        listener.isNotAllowedToAdd();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


}
