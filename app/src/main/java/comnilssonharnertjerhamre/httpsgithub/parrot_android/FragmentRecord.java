package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentRecord extends Fragment implements View.OnClickListener {

    ImageButton recordButton;
    Button playBackButton;
    Button uploadButton;

    int delay = 10; //milliseconds
    Handler h = new Handler();
    private MediaRecorder mRecorder = null;
    private MediaPlayer mediaPlayer;
    String file;

    private RequestQueue queue;

    private boolean mStartRecording;



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
        uploadButton = (Button) view.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(this);

        mStartRecording = true;
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        // Init MediaPlayer and Network module.
        mediaPlayer = new MediaPlayer();
        queue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    /*
    OnClick listener for recoding chirp, playing/stoping chirp and uploading chirp.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_button:
                onRecord();
                break;
            case R.id.playback_button:
                if(mediaPlayer.isPlaying()) {
                    stop();
                } else {
                    onPlayBack();
                }
                break;
            case R.id.upload_button:
                onUpload();
                break;
        }
    }

    /*
    Starting to record audio file.
     */

    public void onRecord(){
        // IF recording
        if(mStartRecording) { // Should start recording

            // path to save file to.
            String mFileName = getActivity().getExternalCacheDir().getAbsolutePath() + "/temp.3gp";

            // Settings for MediaPlayer
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("mRecorder", "prepare() failed");
            }

            // Feedback so that user can see that a recording is ongoing.
            recordButton.setImageResource(R.drawable.microphone_inuse);

            // Stared recording.
            mRecorder.start();

        }
        else{ // Recoding is ongoing and therefore should be paused.
            // Set image back.
            recordButton.setImageResource(R.drawable.microphone);
            // Stop recording
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            // Enabled record button and Play/Stop button
            uploadButton.setEnabled(true);
            playBackButton.setEnabled(true);
        }

        mStartRecording = !mStartRecording;
    }

    /*
    Play recorded audio file.
     */
    public void onPlayBack() {
        // Stop and ongoing audio file.
        stop();

        // Set text of button to stop.
        playBackButton.setText("Stop");

        // Path to file and setting of MediaPlayer
        String mFileName = getActivity().getExternalCacheDir().getAbsolutePath() + "/temp.3gp";
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(mFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Start recording
                mediaPlayer.start();

                h.postDelayed(new Runnable() {
                    public void run() {

                        if (mediaPlayer.isPlaying()) {
                            h.postDelayed(this, delay);
                        }
                    }
                }, delay);
            }
        });

        // Stopping audio playback when audiotrack is over.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
    }

    /*
    Stop currently playing audiotrack.
     */
    private void stop() {

        playBackButton.setText("Play");
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    /*
    Upload file to FTP server.
     */
    public void onUpload() {

        // Disable upload and recording button while uploading.
        uploadButton.setEnabled(false);
        uploadButton.setText("Uploading...");
        recordButton.setEnabled(false);
        // Set opacity to 50% so that user can see that you cant interact with button.
        recordButton.setAlpha(0.5f);

        // Sending info to server that will store that info in a database. And will return the id for
        // that chirp. The id will then be use as filename for storing on FTP server.
        String url = "http://ec2-34-210-104-209.us-west-2.compute.amazonaws.com:45678/chirp";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        // Server responded that it was ok to upload data to server and therefore
                        // OK to upload file to FTP server

                        String path = getActivity().getExternalCacheDir().getAbsolutePath() + "/temp.3gp";
                        // Upload file to FTP server.
                        FTPHandler.upload(Integer.parseInt(response),path);

                        // Wait for upload to be complete and then enable record and upload buttons.
                        h.postDelayed(new Runnable(){
                            public void run(){

                                if(FTPHandler.uploading == true) {
                                    h.postDelayed(this, delay);
                                } else {
                                    uploadButton.setText("Send");
                                    recordButton.setEnabled(true);
                                    recordButton.setAlpha(1.0f);
                                    Toast.makeText(getActivity(), "Upload completed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, delay);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "felfelfelfel");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                // Attatch data to POST request
                Map<String, String>  params = new HashMap<String, String>();
                params.put("parrot", String.valueOf(DataHolder.getData()));
                params.put("chirp", String.valueOf(DataHolder.getData()));

                return params;
            }
        };
        // Send request.
        queue.add(postRequest);

    }
}
