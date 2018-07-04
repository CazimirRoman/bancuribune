package cazimir.com.bancuribune.ui.likedJokes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.utils.EmptyRecyclerView;
import cazimir.com.constants.Constants;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.ui.likedJokes.ILikedJokesActivityView;
import cazimir.com.interfaces.ui.list.OnJokeClickListener;
import cazimir.com.models.Joke;
import cazimir.com.utils.UtilHelper;

import static cazimir.com.constants.Constants.MY_STORAGE_REQ_CODE;

public class LikedJokesActivityView extends BaseBackActivity implements ILikedJokesActivityView {

    private static final String TAG = LikedJokesActivityView.class.getSimpleName();
    private LikedJokesAdapter adapter;

    @BindView(R.id.myLikedJokeList)
    EmptyRecyclerView likedJokesListRecyclerView;
    private String sharedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycleView();
        getLikedJokes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void setBackArrowColour() {
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        likedJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new LikedJokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke joke) {
                showToast(getString(R.string.share_open));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareJoke(joke.getJokeText());
                    }
                }, 500);
            }

            @Override
            public void onJokeVoted(Joke joke) {

            }

            @Override
            public void onJokeExpanded() {
                //do nothing - it is just for reporting for the main jokes screen
            }
        });
        likedJokesListRecyclerView.setAdapter(adapter);
        likedJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
    }

    private void shareJoke(String text) {

        sharedText = text;

        if (writeStoragePermissionGranted()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            int words = UtilHelper.countWords(text);

            if (getPresenter().getCurrentUserID().equals("eXDuTHO9UPZAS02HctjQHI2hsRv2")) {
                Log.d(TAG, "shareJoke: " + "Admin user logged in");
                if (words <= Constants.MAX_JOKE_SIZE_PER_PAGE) {
                    Log.d(TAG, "shareJoke: " + "Bancul este mai mic de 45 de cuvinte. Are lungimea de: " + words + " de cuvinte");
                    Bitmap bitmap = UtilHelper.drawMultilineTextToBitmap(this, R.drawable.share_background, text);
                    String bitmapPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", "description");
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    sendIntent.setType("image/*");
                    startActivity(Intent.createChooser(sendIntent, "Share"));
                } else {
                    Log.d(TAG, "shareJoke: " + "Bancul este prea lung. Alege altul mai scurt");
                    Toast.makeText(this, "Bancul este prea lung", Toast.LENGTH_SHORT).show();
                }
            } else {
                sendIntent.putExtra(Intent.EXTRA_TEXT, text + "\n\n" + getString(R.string.share_text));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        }
    }

    private boolean writeStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQ_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_STORAGE_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareJoke(sharedText);
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_liked_jokes_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.liked_jokes_title;
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public void showMyLikedJokesList(Joke myLikedJoke) {
        adapter.add(myLikedJoke);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public void getLikedJokes() {
        getPresenter().getLikedJokes();
    }

    @Override
    public void showNoLikedJokesText() {
        findViewById(R.id.no_jokes_liked).setVisibility(View.VISIBLE);
    }
}
