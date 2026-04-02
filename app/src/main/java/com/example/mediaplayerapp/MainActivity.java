package com.example.mediaplayerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediaplayer.R;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    EditText urlInput;
    Uri mediaUri;

    // ✅ NEW file picker (modern way)
    ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            mediaUri = result.getData().getData();
                            videoView.setVideoURI(mediaUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        urlInput = findViewById(R.id.urlInput);
    }

    // ✅ Open file from device
    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    // ✅ Open URL
    public void openURL(View view) {
        String url = urlInput.getText().toString();
        mediaUri = Uri.parse(url);
        videoView.setVideoURI(mediaUri);
    }

    // ✅ Play
    public void playMedia(View view) {
        videoView.start();
    }

    // ✅ Pause
    public void pauseMedia(View view) {
        videoView.pause();
    }

    // ✅ Stop
    public void stopMedia(View view) {
        videoView.stopPlayback();
    }

    // ✅ Restart
    public void restartMedia(View view) {
        videoView.seekTo(0);
        videoView.start();
    }
}