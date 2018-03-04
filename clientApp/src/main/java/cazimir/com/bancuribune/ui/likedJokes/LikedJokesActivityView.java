package cazimir.com.bancuribune.ui.likedJokes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.ui.list.MainActivityView;
import cazimir.com.bancuribune.ui.list.OnJokeClickListener;
import cazimir.com.bancuribune.utils.EmptyRecyclerView;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.utils.UtilHelper;

import static cazimir.com.bancuribune.constants.Constants.MY_STORAGE_REQUEST_CODE;

public class LikedJokesActivityView extends BaseBackActivity implements ILikedJokesActivityView {

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

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        likedJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new LikedJokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke joke) {
                Toast.makeText(LikedJokesActivityView.this, R.string.share_open, Toast.LENGTH_LONG).show();
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
        });
        likedJokesListRecyclerView.setAdapter(adapter);
        likedJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
    }

    private void shareJoke(String text) {

        this.sharedText = text;

        if (!requestWriteStoragePermissions()) {
            Bitmap bitmap = UtilHelper.drawMultilineTextToBitmap(this, R.drawable.share_background, text);
            String bitmapPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", "description");
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            sendIntent.setType("image/*");
            startActivity(Intent.createChooser(sendIntent, "Share"));
        }
    }

    private boolean requestWriteStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareJoke(sharedText);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
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
