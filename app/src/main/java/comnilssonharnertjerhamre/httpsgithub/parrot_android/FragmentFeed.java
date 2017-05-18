package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentFeed extends Fragment {


    int delay = 10; //milliseconds
    Handler h = new Handler();

    MediaPlayer mediaPlayer;
    ImageButton currently_playing;
    RequestQueue queue;

    public static FragmentFeed newInstance(){
        FragmentFeed fragment = new FragmentFeed();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        ShakeDetector.create(getActivity(), new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                Toast.makeText(getActivity(), "Updating feed...", Toast.LENGTH_SHORT).show();
                update();
            }
        });

        mediaPlayer = new MediaPlayer();
        queue = Volley.newRequestQueue(getActivity());

        update();
    }

    @Override
    public void onStop() {
        super.onStop();
        ShakeDetector.stop();

    }

    @Override
    public void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    public void update() {
        ((LinearLayout) getActivity().findViewById(R.id.layout_feed)).removeAllViews();

        String url = "http://ec2-52-35-30-107.us-west-2.compute.amazonaws.com:45678/chirps";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Success", response);

                stop();



                try {

                    JSONArray obj = new JSONArray(response);

                    Log.d("asd", ""+ obj.length());

                    for(int i = 0; i < obj.length(); i++) {
                        int id = obj.getJSONObject(i).getInt("id");
                        String parrot = obj.getJSONObject(i).getString("parrot");
                        String sent = obj.getJSONObject(i).getString("sent");

                        createChirp(id, parrot, sent);
                    }

                } catch (Throwable t) {
                    Log.e("FragmentFeed", "Could not parse malformed JSON: \"" + response + "\"");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLError", "Getting chirps didn't work");
            }
        });
        queue.add(stringRequest);
    }

    public void createChirp(int id, String parrot, String datetime) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams params;

        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(8,0,8,0);

        LinearLayout ll_grid = new LinearLayout(getActivity());
        ll_grid.setOrientation(LinearLayout.HORIZONTAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_grid.setLayoutParams(params);


        LinearLayout ll_texts = new LinearLayout(getActivity());
        ll_texts.setOrientation(LinearLayout.VERTICAL);
        params = new LinearLayout.LayoutParams(width - 2 * 8 - 96, 96);
        ll_texts.setLayoutParams(params);

        TextView tv_uname = new TextView(getActivity());
        tv_uname.setText(parrot);
        tv_uname.setTextSize(24);
        tv_uname.setGravity(Gravity.CENTER_VERTICAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
        tv_uname.setLayoutParams(params);

        TextView tv_sent = new TextView(getActivity());
        tv_sent.setText(datetime);
        tv_sent.setTextSize(12);
        tv_uname.setGravity(Gravity.CENTER_VERTICAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 32);
        tv_sent.setLayoutParams(params);

        ll_texts.addView(tv_uname);
        ll_texts.addView(tv_sent);

        ImageButton ib = new ImageButton(getActivity());
        params = new LinearLayout.LayoutParams(96, 96);
        ib.setLayoutParams(params);
        ib.setImageResource(R.drawable.download);
        ib.setOnClickListener(getOnClickDoSomething(ib));
        ib.setTag(R.string.IB_STATE, R.string.IB_STATE_DOWNLOADER);
        ib.setTag(R.string.IB_ID, id);

        ll_grid.addView(ll_texts);
        ll_grid.addView(ib);

        ProgressBar pb = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
        pb.setProgress(70);
        pb.setVisibility(View.INVISIBLE);
        ib.setTag(R.string.IB_PB, pb);

        root.addView(ll_grid);
        root.addView(pb);

        ((LinearLayout) getActivity().findViewById(R.id.layout_feed)).addView(root);

    }

    View.OnClickListener getOnClickDoSomething(final ImageButton button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if ((int)button.getTag(R.string.IB_STATE) == R.string.IB_STATE_DOWNLOADER) {
                    downloader(button);
                } else if((int)button.getTag(R.string.IB_STATE) == R.string.IB_STATE_PLAYER) {
                    if(currently_playing == button) {
                        stop();
                    } else {
                        play(button);
                    }
                }
            }
        };
    }

    public void downloader(final ImageButton button) {


        button.setEnabled(false);

        Toast.makeText(getActivity(), "Downloading chirp", Toast.LENGTH_SHORT).show();

        String url = "http://www.google.com";
        url = "http://ec2-52-35-30-107.us-west-2.compute.amazonaws.com:45678/chirp?id=" + button.getTag(R.string.IB_ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Success", response);

                File file = new File(getContext().getCacheDir().toString() + "/" + button.getTag(R.string.IB_ID)+".mp3");

                Log.d("New file", file.toString());

                FileOutputStream outputStream;

                try {
                    outputStream = new FileOutputStream(file);//
                    outputStream.write(response.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                button.setTag(R.string.IB_STATE, R.string.IB_STATE_PLAYER);
                toggleIBImage(button);

                button.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URLError", "Getting chirp didn't work");
                button.setEnabled(true);
            }
        });
        queue.add(stringRequest);
    }

    private void play(final ImageButton button) {

        stop();

        File file = new File(getContext().getCacheDir().toString() + "/" + button.getTag(R.string.IB_ID)+".mp3");

        try {
            Log.d("URL", file.toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String url;
        url = "https://archive.org/download/testmp3testfile/mpthreetest.mp3";
        url = "file://storage/extSdCard/count_down.mp3";

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final ProgressBar pb = (ProgressBar) button.getTag(R.string.IB_PB);
                button.setImageResource(R.drawable.stop);
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(0);
                currently_playing = button;
                mediaPlayer.start();
                pb.setMax(mediaPlayer.getDuration());


                h.postDelayed(new Runnable(){
                    public void run(){

                        Log.d("UpdateProgressbar", mediaPlayer.getCurrentPosition()+"");
                        pb.setProgress(mediaPlayer.getCurrentPosition());

                        if(mediaPlayer.isPlaying()) {
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
        if(currently_playing != null) {
            currently_playing.setImageResource(R.drawable.play);
            ProgressBar pb = (ProgressBar) currently_playing.getTag(R.string.IB_PB);
            pb.setVisibility(View.INVISIBLE);
        }
        currently_playing = null;
    }

    private void toggleIBImage(final ImageButton button) {
        if ((int)button.getTag(R.string.IB_STATE) == R.string.IB_STATE_DOWNLOADER) {
            button.setImageResource(R.drawable.download);
        } else if((int)button.getTag(R.string.IB_STATE) == R.string.IB_STATE_PLAYER) {
            button.setImageResource(R.drawable.play);
        }
    }
}