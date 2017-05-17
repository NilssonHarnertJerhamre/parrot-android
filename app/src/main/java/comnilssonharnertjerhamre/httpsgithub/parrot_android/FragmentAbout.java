package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentAbout extends Fragment {

    public static FragmentAbout newInstance(){
        FragmentAbout fragment = new FragmentAbout();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false); // TODO: implement fragmentAbout.xml
    }
}