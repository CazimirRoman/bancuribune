package cazimir.com.bancuribune.repository;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;
import cazimir.com.bancuribune.utils.Utils;

public class JokesRepository implements IJokesRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference jokesRef = database.getReference("jokes");
    private DatabaseReference votesRef = database.getReference("votes");

    @Override
    public void getAllJokes(final OnGetJokesListener listener) {

        jokesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<Joke> jokes = new ArrayList<>();

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if (joke.isApproved()) {
                        jokes.add(joke);
                    }
                }

                Collections.reverse(jokes);

                listener.OnGetJokesSuccess(jokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.OnGetJokesFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getAllFilteredJokes(final OnGetJokesListener listener, final String text) {
        final ArrayList<Joke> filteredJokes = new ArrayList<>();

        final String cleanedText = Utils.removeAccents(text);

        jokesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if(joke.getJokeText().trim().toLowerCase().contains(cleanedText.toLowerCase())){
                        filteredJokes.add(joke);
                    }
                }

                listener.OnGetJokesSuccess(filteredJokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.OnGetJokesFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getAllPendingJokes(final OnGetAllPendingJokesListener listener) {
        final ArrayList<Joke> pendingJokes = new ArrayList<>();

        jokesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if(!joke.isApproved()){
                        pendingJokes.add(joke);
                    }
                }

                listener.OnGetAllPendingJokesSuccess(pendingJokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.OnGetAllPendingJokesFailed(databaseError.getMessage());
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

                Collections.sort(myJokes, new Comparator<Joke>() {
                    @Override public int compare(Joke j1, Joke j2) {
                        return j2.getPoints() - j1.getPoints();
                    }

                });

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
        jokesRef.child(uid).setValue(joke, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.OnAddFailed();
                } else {
                    listener.OnAddSuccess();
                }
            }
        });
    }

    @Override
    public void getAllJokesAddedToday(final OnAllowedToAddFinishedListener listener, String userId) {

        final ArrayList<Joke> addedJokesToday = new ArrayList<>();

        Query query = jokesRef.orderByChild("createdBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokesSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokesSnapshot.getValue(Joke.class);
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
    public void updateJokePoints(final OnUpdatePointsFinishedListener listener, final String uid) {

        Query query = jokesRef.orderByChild("uid").equalTo(uid);

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Joke joke = dataSnapshot.getValue(Joke.class);
                assert joke != null;
                int newPoints = joke.getPoints() + 1;
                jokesRef.child(uid).child("points").setValue(newPoints, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            listener.OnUpdatePointsFailed(databaseError.getMessage());
                        } else {
                            listener.OnUpdatePointsSuccess();
                        }
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateApproveStatus(final OnUpdateApproveStatusListener listener, final String uid) {
        Query query = jokesRef.orderByChild("uid").equalTo(uid);

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Joke joke = dataSnapshot.getValue(Joke.class);
                assert joke != null;

                jokesRef.child(uid).child("approved").setValue(true, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            listener.OnUpdateApproveStatusFailed(databaseError.getMessage());
                        } else {
                            listener.OnUpdateApproveStatusSuccess();
                        }
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateVotedBy(final OnUpdateVotedByFinishedListener listener, String uid, String userId) {

        DatabaseReference ref = jokesRef.child(uid).child("votedBy").push();
        Map<String, Object> map = new HashMap<>();
        map.put("userID", userId);
        ref.updateChildren(map, new DatabaseReference.CompletionListener(){

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.OnUpdateVotedByFailed(databaseError.getMessage());
                } else {
                    listener.OnUpdateVotedBySuccess();
                }
            }
        });
    }

    @Override
    public void writeJokeVote(final OnAddJokeVoteFinishedListener listener, Vote vote) {
        String newUid = votesRef.push().getKey();
        vote.setUid(newUid);
        votesRef.child(newUid).setValue(vote, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.onAddJokeVoteFailed();
                } else {
                    listener.onAddJokeVoteSuccess();
                }
            }
        });


    }

    @Override
    public void checkIfVoted(final OnCheckIfVotedFinishedListener listener, final String uid, String userId) {

        final ArrayList<Vote> votedJokes = new ArrayList<>();

        Query query = votesRef.orderByChild("votedBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot votesSnapshot : dataSnapshot.getChildren()) {
                    Vote vote = votesSnapshot.getValue(Vote.class);
                    votedJokes.add(vote);
                }

                for (Vote vote : votedJokes) {
                    if (vote.getJokeId().equals(uid)) {
                        listener.OnHasVotedTrue();
                        return;
                    }

                }

                listener.OnHasVotedFalse(uid);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
