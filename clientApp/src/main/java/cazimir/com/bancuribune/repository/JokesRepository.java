package cazimir.com.bancuribune.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnFirebaseGetAllJokesListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;
import cazimir.com.bancuribune.utils.Utils;

public class JokesRepository implements IJokesRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference jokesRef = database.getReference("jokes");

    @Override
    public void getAllJokes(final OnFirebaseGetAllJokesListener listener) {

        jokesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<Joke> jokes = new ArrayList<>();

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if(joke.isApproved()){
                        jokes.add(joke);
                    }
                }

                Collections.reverse(jokes);

                listener.onSuccess(jokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getMyJokes(final OnFirebaseGetMyJokesListener listener, String userId) {
        final ArrayList<Joke> myJokes = new ArrayList<>();

        Query query = jokesRef.orderByChild("createdBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    myJokes.add(joke);
                }

                Collections.reverse(myJokes);

                listener.onGetMyJokesSuccess(myJokes);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onGetMyJokesError(databaseError.getMessage());
            }
        });
    }

    @Override
    public void addJoke(final OnAddFinishedListener listener, Joke joke) {
        String uid = jokesRef.push().getKey();
        joke.setUid(uid);
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

    @Override
    public void updateJokePoints(final String uid) {

        jokesRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Joke joke = dataSnapshot.getValue(Joke.class);
                assert joke != null;
                joke.setPoints(joke.getPoints() + 1);
                jokesRef.child(uid).push().setValue(joke);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
