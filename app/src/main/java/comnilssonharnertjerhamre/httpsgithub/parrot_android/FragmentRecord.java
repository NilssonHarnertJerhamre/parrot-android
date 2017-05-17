package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentRecord extends Fragment {

    //private record_button mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    //private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecording;
    private boolean mStartPlaying;

    public static FragmentRecord newInstance(){
        FragmentRecord fragment = new FragmentRecord();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mStartRecording = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false); // TODO: implement fragmentRecord.xml
    }


    private void onRecord(boolean start) {

        if (start) {
            AudioRecordTest();
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            AudioRecordTest();
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.d("startPlaying()", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.d("startRecording()", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    public void AudioRecordTest() {
        mFileName = Environment.getExternalStorageDirectory().getPath();
        mFileName += "/recording.aac";
        Log.e("AudioRecordTest()", "File name: "+mFileName);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    // when pressing the record button
    public void onClickRecord(View v) {
        onRecord(mStartRecording);
        if (mStartRecording) {
            // STOP RECORDING
        } else {
            // START RECORDING
        }
        mStartRecording = !mStartRecording;
    }


}
