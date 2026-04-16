package com.example.mediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer activeYouTubePlayer;
    private TextInputEditText urlInput;
    private Uri mediaUri;
    private boolean isYouTube = false;

    ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            mediaUri = result.getData().getData();
                            playLocalVideo(mediaUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        urlInput = findViewById(R.id.urlInput);

        // MediaController for local videos
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youtubePlayer) {
                activeYouTubePlayer = youtubePlayer;
            }
        });
    }

    private void playLocalVideo(Uri uri) {
        isYouTube = false;
        youtubePlayerView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        if (activeYouTubePlayer != null) {
            activeYouTubePlayer.pause();
        }
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void playYouTubeVideo(String videoId) {
        isYouTube = true;
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);
        youtubePlayerView.setVisibility(View.VISIBLE);
        if (activeYouTubePlayer != null) {
            activeYouTubePlayer.loadVideo(videoId, 0);
        } else {
            Toast.makeText(this, "YouTube Player initializing...", Toast.LENGTH_SHORT).show();
        }
    }

    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        filePickerLauncher.launch(intent);
    }

    public void openURL(View view) {
        if (urlInput.getText() == null) return;
        String url = urlInput.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            return;
        }


        String youtubeId = extractYouTubeId(url);
        if (youtubeId != null) {
            playYouTubeVideo(youtubeId);
        } else {
            mediaUri = Uri.parse(url);
            playLocalVideo(mediaUri);
        }
    }

    private String extractYouTubeId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed/|youtu.be/|/v/)[^#&?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public void playMedia(View view) {
        if (isYouTube) {
            if (activeYouTubePlayer != null) activeYouTubePlayer.play();
        } else {
            videoView.start();
        }
    }

    public void pauseMedia(View view) {
        if (isYouTube) {
            if (activeYouTubePlayer != null) activeYouTubePlayer.pause();
        } else {
            videoView.pause();
        }
    }

    public void stopMedia(View view) {
        if (isYouTube) {
            if (activeYouTubePlayer != null) activeYouTubePlayer.pause();
        } else {
            videoView.stopPlayback();
        }
    }

    public void restartMedia(View view) {
        if (isYouTube) {
            if (activeYouTubePlayer != null) activeYouTubePlayer.seekTo(0);
        } else {
            videoView.seekTo(0);
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }
}