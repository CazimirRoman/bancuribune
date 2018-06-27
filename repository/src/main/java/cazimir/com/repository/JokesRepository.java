package cazimir.com.repository;

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
import java.util.List;

import cazimir.com.constants.Constants;
import cazimir.com.interfaces.repository.IJokesRepository;
import cazimir.com.interfaces.repository.OnAddRankFinishedListener;
import cazimir.com.interfaces.repository.OnAddUserListener;
import cazimir.com.interfaces.repository.OnAdminCheckFinishedListener;
import cazimir.com.interfaces.repository.OnCheckIfRankDataInDBListener;
import cazimir.com.interfaces.repository.OnShowReminderToAddListener;
import cazimir.com.interfaces.repository.OnUpdateRankPointsSuccess;
import cazimir.com.interfaces.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.interfaces.ui.admin.OnJokeApprovedListener;
import cazimir.com.interfaces.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.interfaces.ui.list.OnAddJokeVoteFinishedListener;
import cazimir.com.interfaces.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.interfaces.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.interfaces.ui.list.OnGetJokesListener;
import cazimir.com.interfaces.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.interfaces.ui.myJokes.OnGetMyJokesListener;
import cazimir.com.models.Joke;
import cazimir.com.models.Rank;
import cazimir.com.models.User;
import cazimir.com.models.Vote;
import cazimir.com.utils.UtilHelper;

public class JokesRepository implements IJokesRepository {

    private DatabaseReference jokesRef;
    private DatabaseReference votesRef;
    private DatabaseReference ranksRef;
    private DatabaseReference usersRef;

    private String keyNewest;
    private String keyStep;

    public JokesRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

//        if (!BuildConfig.DEBUG) {
//            this.jokesRef = database.getReference("_dev/jokes_dev");
//            this.votesRef = database.getReference("_dev/votes_dev");
//            this.ranksRef = database.getReference("_dev/ranks_dev");
//            this.usersRef = database.getReference("_dev/users_dev");
//        }else{
            this.jokesRef = database.getReference("jokes");
            this.votesRef = database.getReference("votes");
            this.ranksRef = database.getReference("ranks");
            this.usersRef = database.getReference("users");
//        }
    }

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
                        sendJokesBackToView(listener);
                    }else{
                        listener.onEndOfListReached();
                    }
                }


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
                                listener.onEndOfListReached();
                                keyStep = null;
                                return;
                            }

                            jokes.remove(jokes.size()-1);

                            Collections.reverse(jokes);

                            keyStep = jokes.get(jokes.size()-1).getUid();

                            listener.onGetJokesSuccess(jokes);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onGetJokesFailed(databaseError.getMessage());
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
                                listener.onGetJokesFailed("No more jokes to display");
                            }

                            final List<Joke> jokes = new ArrayList<>();

                            for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                                Joke joke = jokeSnapshot.getValue(Joke.class);
                                assert joke != null;
                                if (joke.isApproved()) {
                                    jokes.add(joke);
                                }
                            }
                            if(!jokes.isEmpty()){
                                keyStep = jokes.get(0).getUid();
                                Collections.reverse(jokes);
                                listener.onGetJokesSuccess(jokes);
                            }else{
                                listener.onEndOfListReached();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onGetJokesFailed(databaseError.getMessage());
                        }
                    });
        }
    }

    @Override
    public void getAllFilteredJokes(final OnGetJokesListener listener, final String text) {
        final ArrayList<Joke> filteredJokes = new ArrayList<>();

        final String cleanedText = UtilHelper.removeAccents(text);

        jokesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokeSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokeSnapshot.getValue(Joke.class);
                    assert joke != null;
                    if (UtilHelper.removeAccents(joke.getJokeText().trim().toLowerCase()).contains(cleanedText.toLowerCase())) {
                        filteredJokes.add(joke);
                    }
                }

                listener.onGetJokesSuccess(filteredJokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onGetJokesFailed(databaseError.getMessage());
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

                listener.onGetAllPendingJokesSuccess(pendingJokes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onGetAllPendingJokesFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getMyJokes(final OnGetMyJokesListener listener, String userId) {
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

        if(votes.isEmpty()){
            listener.onNoLikedJokes();
        }

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
                    listener.onAddRankFailed(databaseError.getMessage());
                } else {
                    listener.onAddRankSuccess(rank);
                }
            }
        });
    }

    @Override
    public void addJoke(final cazimir.com.interfaces.ui.add.OnAddJokeFinishedListener listener, Joke joke) {
        String uid = jokesRef.push().getKey();
        joke.setUid(uid);
        jokesRef.child(uid).setValue(joke, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.onAddFailed();
                } else {
                    listener.onAddSuccess();
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

                    if (UtilHelper.isSameDay(createdAt, nowDate)) {
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
    public void getAllJokesAddedOverThePastWeek(final OnShowReminderToAddListener listener, String userId, final Date lastCheckDate) {
        final ArrayList<Joke> addedJokeOverPastWeek = new ArrayList<>();

        Query query = jokesRef.orderByChild("createdBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jokesSnapshot : dataSnapshot.getChildren()) {
                    Joke joke = jokesSnapshot.getValue(Joke.class);
                    assert joke != null;
                    Date createdAt = new Date(joke.getCreatedAt());
                    long now = new Date().getTime();

                    if (UtilHelper.isInCurrentDateInterval(lastCheckDate, createdAt)) {
                        addedJokeOverPastWeek.add(joke);
                        break;
                    }
                }

                if(addedJokeOverPastWeek.size() == 0){
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
    public void setApprovedStatusToTrue(final OnJokeApprovedListener listener, final String uid) {
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
                            listener.onJokeApprovedFailed(databaseError.getMessage());
                        } else {
                            listener.onJokeApprovedSuccess();
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
                                        listener.onUpdateRankPointsSuccess();
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
                    listener.onAddJokeVoteFailed(databaseError.getMessage());
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
                        listener.onHasVotedTrue();
                        return;
                    }

                }

                listener.onHasVotedFalse(joke);

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
                    listener.rankDataIsInDB(rank);

                } else {
                    listener.rankDataNotInDB();
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
                    listener.onAddUserFailed(databaseError.getMessage());
                } else {
                    listener.onAddUserSuccess();
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
                        listener.onIsAdmin();
                    } else {
                        listener.onIsNotAdmin();
                    }
                }else{
                    listener.onIsNotAdmin();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
