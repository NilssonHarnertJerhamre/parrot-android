package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static android.R.attr.button;
import static android.R.attr.delay;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentRecord extends Fragment implements View.OnClickListener {


    ImageButton recordButton;
    Button playBackButton;

    int delay = 10; //milliseconds
    Handler h = new Handler();
    private MediaRecorder mRecorder = null;
    private MediaPlayer mediaPlayer;

    private boolean mStartRecording;
    private boolean mStartPlaying;



    public static FragmentRecord newInstance(){
        FragmentRecord fragment = new FragmentRecord();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        recordButton = (ImageButton) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(this);
        playBackButton = (Button) view.findViewById(R.id.playback_button);
        playBackButton.setOnClickListener(this);
        mStartRecording = true;
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onStop(){
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_button:
                onRecord();
                break;
            case R.id.playback_button:
                onPlayBack();
                break;
        }
    }


    public void onRecord(){
        if(mStartRecording) {
            String mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest.3gp";
            //File file = new File(getContext().getCacheDir().toString() + "/" + "temp" + ".mp3");

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //mRecorder.setOutputFile(file.toString());
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("mRecorder", "prepare() failed");
            }

            mRecorder.start();

        }
        else{
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        mStartRecording = !mStartRecording;
    }

    public void onPlayBack() {
        stop();


        String mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        //File file = new File(getContext().getCacheDir().toString() + "/" + "temp" + ".mp3");

        /*
        try {
            Log.d("URL", file.toURL().toString());
            Log.d("URL", mFileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        */
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            //mediaPlayer.setDataSource(file.toURL().toString());
            mediaPlayer.setDataSource(mFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();

                h.postDelayed(new Runnable() {
                    public void run() {


                        //pb.setProgress(mediaPlayer.getCurrentPosition());

                        if (mediaPlayer.isPlaying()) {
                            h.postDelayed(this, delay);
                        }
                    }
                }, delay);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
    }


    private void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
}
