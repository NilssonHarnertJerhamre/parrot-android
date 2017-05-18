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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
        mediaPlayer = new MediaPlayer();
        queue = Volley.newRequestQueue(getActivity());
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
            case R.id.upload_button:
                onUpload();
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

    public void onUpload(){
        ByteArrayOutputStream buffer;
        byte[] bytes;
        String mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        try {
            file = getStringFromFile(mFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "http://ec2-52-35-30-107.us-west-2.compute.amazonaws.com:45678/chirp";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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

                Map<String, String>  params = new HashMap<String, String>();
                params.put("parrot", String.valueOf(DataHolder.getData()));
                params.put("chirp", file);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

}
