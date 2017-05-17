package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by antonjerhamre on 2017-05-17.
 */

public class FragmentRecord extends Fragment {

    public static FragmentRecord newInstance(){
        FragmentRecord fragment = new FragmentRecord();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false); // TODO: implement fragmentRecord.xml
    }
}
