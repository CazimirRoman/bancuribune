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
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.User;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.presenter.OnAddRankFinishedListener;
import cazimir.com.bancuribune.presenter.OnAddUserListener;
import cazimir.com.bancuribune.presenter.OnAdminCheckFinishedListener;
import cazimir.com.bancuribune.presenter.OnCheckIfRankDataInDBListener;
import cazimir.com.bancuribune.presenter.OnUpdateRankPointsSuccess;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;
import cazimir.com.bancuribune.utils.UtilHelperClass;

public class JokesRepository implements IJokesRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference jokesRef = database.getReference("jokes");
    private DatabaseReference votesRef = database.getReference("votes");
    private DatabaseReference ranksRef = database.getReference("ranks");
    private DatabaseReference usersRef = database.getReference("users");

    private String keyNewest;
    private String keyStep;

    @Override
    public void getAllJokes(final OnGetJokesListener listener, boolean reset) {

        if(reset){
            keyStep = null;
        }

        getNewestEntry(listener);
    }

    private void getNewestEntry(final OnGetJokesListener listener) {

        Query lastQuery = jokesRef.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    if (joke != null) {
                        keyNewest = jokeSnapshot.getKey();
                    }
                }

                sendJokesBackToView(listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    private void sendJokesBackToView(final OnGetJokesListener listener) {

        //if no keystep, start from newest entry
        if (keyStep != null) {
            jokesRef.endAt(keyStep).limitToLast(Constants.TOTAL_ITEM_EACH_LOAD)
                    .orderByKey()
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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

                            if(jokes.size() == 1){
                                listener.OnEndOfListReached();
                                keyStep = null;
                                return;
                            }

                            jokes.remove(jokes.size()-1);

                            Collections.reverse(jokes);

                            keyStep = jokes.get(jokes.size()-1).getUid();

                            listener.OnGetJokesSuccess(jokes);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.OnGetJokesFailed(databaseError.getMessage());
                        }
                    });
        } else {
            //start from newest up to 10 entries
            jokesRef.limitToLast(Constants.TOTAL_ITEM_EACH_LOAD)
                    .endAt(keyNewest)
                    .orderByKey()
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.hasChildren()) {
                                listener.OnGetJokesFailed("No more jokes to display");
                            }

                            final List<Joke> jokes = new ArrayList<>();

                            for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                                Joke joke = jokeSnapshot.getValue(Joke.class);
                                assert joke != null;
                                if (joke.isApproved()) {
                                    jokes.add(joke);
                                }
                            }

                            keyStep = jokes.get(0).getUid();

                            Collections.reverse(jokes);

                            listener.OnGetJokesSuccess(jokes);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.OnGetJokesFailed(databaseError.getMessage());
                        }
                    });
        }
    }

    @Override
    public void getAllFilteredJokes(final OnGetJokesListener listener, final String text) {
        final ArrayList<Joke> filteredJokes = new ArrayList<>();

        final String cleanedText = UtilHelperClass.removeAccents(text);

        jokesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if (UtilHelperClass.removeAccents(joke.getJokeText().trim().toLowerCase()).contains(cleanedText.toLowerCase())) {
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

        jokesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if (!joke.isApproved()) {
                        pendingJokes.add(joke);
                    }
                }

                Collections.reverse(pendingJokes);

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

                //show jokes with most votes on top
                Collections.sort(myJokes, new Comparator<Joke>() {
                    @Override
                    public int compare(Joke j1, Joke j2) {
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
    public void getVotesForUser(final OnGetLikedJokesListener listener, String userId) {
        final ArrayList<Vote> votes = new ArrayList<>();

        Query query = votesRef.orderByChild("votedBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot voteSnapshot : dataSnapshot.getChildren()) {
                    Vote vote = voteSnapshot.getValue(Vote.class);
                    assert vote != null;
                    votes.add(vote);
                }

                Collections.reverse(votes);

                getVotedJokes(listener, votes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onGetLikedJokesFailed(databaseError.getMessage());
            }
        });
    }

    private void getVotedJokes(final OnGetLikedJokesListener listener, ArrayList<Vote> votes) {

        for (final Vote vote : votes) {
            Query query = jokesRef.orderByChild("uid").equalTo(vote.getJokeId());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                        Joke joke = jokeSnapshot.getValue(Joke.class);
                        if(joke != null){
                            listener.onGetLikedJokesSuccess(joke);
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onGetLikedJokesFailed(databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void addRankToDB(final OnAddRankFinishedListener listener, final Rank rank) {
        String uid = ranksRef.push().getKey();
        rank.setUid(uid);
        ranksRef.child(uid).setValue(rank);

        ranksRef.child(uid).setValue(rank, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.OnAddRankFailure(databaseError.getMessage().toString());
                } else {
                    listener.OnAddRankSuccess(rank);
                }
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
    public void getAllJokesAddedToday(final OnAllowedToAddFinishedListener listener, String userId, final int addLimit) {

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

                    if (UtilHelperClass.isSameDay(createdAt, nowDate)) {
                        addedJokesToday.add(joke);
                    }
                }

                if (addedJokesToday.size() < addLimit) {

                    int remainingAdds = addLimit - addedJokesToday.size();

                    listener.isAllowedToAdd(remainingAdds);
                } else {
                    listener.isNotAllowedToAdd(addLimit);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getAllJokesAddedThisWeek(final OnShowReminderToAddListener listener, String userId) {
        final ArrayList<Joke> addedJokesThisWeek = new ArrayList<>();

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

                    if (UtilHelperClass.isDateInCurrentWeek(createdAt)) {
                        addedJokesThisWeek.add(joke);
                        break;
                    }
                }

                if(addedJokesThisWeek.size() == 0){
                    listener.showAddReminderToUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateJokePoints(final OnUpdatePointsFinishedListener listener, final Joke joke) {

        jokesRef.child(joke.getUid()).child("points").setValue(joke.getPoints() + 1, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.OnUpdatePointsFailed(databaseError.getMessage());
                } else {

                    Query query = jokesRef.orderByChild("uid").equalTo(joke.getUid());

                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Joke joke = dataSnapshot.getValue(Joke.class);
                            listener.OnUpdatePointsSuccess(joke);
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
            }
        });


//        Query query = jokesRef.orderByChild("uid").equalTo(uid);
//
//        query.addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Joke joke = dataSnapshot.getValue(Joke.class);
//                assert joke != null;
//                int newPoints = joke.getPoints() + 1;
//                jokesRef.child(uid).child("points").setValue(newPoints, new DatabaseReference.CompletionListener() {
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if (databaseError != null) {
//                            listener.OnUpdatePointsFailed(databaseError.getMessage());
//                        } else {
//                            listener.OnUpdatePointsSuccess();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
        ref.updateChildren(map, new DatabaseReference.CompletionListener() {

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
    public void updateRankPointsAndName(final OnUpdateRankPointsSuccess listener, final String rankName, final int points, final String uid) {

        Query query = ranksRef.orderByChild("uid").equalTo(uid);

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Rank rank = dataSnapshot.getValue(Rank.class);
                assert rank != null;

                ranksRef.child(uid).child("totalPoints").setValue(points, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            ranksRef.child(uid).child("rank").setValue(rankName, new DatabaseReference.CompletionListener() {

                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        listener.OnUpdateRankPointsSuccess();
                                    }
                                }
                            });
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
    public void checkIfVoted(final OnCheckIfVotedFinishedListener listener, final Joke joke, String userId) {

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
                    if (vote.getJokeId().equals(joke.getUid())) {
                        listener.OnHasVotedTrue();
                        return;
                    }

                }

                listener.OnHasVotedFalse(joke);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void checkIfRankDataInDB(final OnCheckIfRankDataInDBListener listener, String userId) {
        Query query = ranksRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Rank rank = null;

                for (DataSnapshot rankSnapshot : dataSnapshot.getChildren()) {
                    rank = rankSnapshot.getValue(Rank.class);
                }

                if (rank != null) {
                    listener.RankDataIsInDB(rank);

                } else {
                    listener.RankDataNotInDB();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //check permission in Firebase (these type of errors are thrown here)
            }
        });

    }

    @Override
    public void addUserToDatabase(final OnAddUserListener listener, String userId, String userName) {
        String uid = usersRef.push().getKey();
        User user = new User();
        user.setUid(uid);
        user.setUserId(userId);
        user.setName(userName);
        user.setRole(Constants.ROLE_USER);
        usersRef.child(uid).setValue(user);

        usersRef.child(uid).setValue(user, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.OnAddUserFailed(databaseError.getMessage().toString());
                } else {
                    listener.OnAddUserSuccess();
                }
            }
        });
    }

    @Override
    public void checkIfAdmin(final OnAdminCheckFinishedListener listener, String userId) {

        Query query = usersRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = null;

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    user = usersSnapshot.getValue(User.class);
                    assert user != null;
                }

                if (user != null) {
                    if (user.getRole().equals("Admin")) {
                        listener.OnAdminCheckTrue();
                    } else {
                        listener.OnAdminCheckFalse();
                    }
                }else{
                    listener.OnAdminCheckFalse();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
