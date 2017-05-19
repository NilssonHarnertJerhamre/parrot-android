package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentAbout extends Fragment implements View.OnClickListener  {

    public static FragmentAbout newInstance(){
        FragmentAbout fragment = new FragmentAbout();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        Button button_sleep = (Button) view.findViewById(R.id.sleep);
        button_sleep.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sleep:
                DataHolder.setData(-1);
                getActivity().recreate();
                break;
        }
    }
}