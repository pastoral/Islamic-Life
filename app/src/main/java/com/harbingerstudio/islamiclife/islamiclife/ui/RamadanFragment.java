package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harbingerstudio.islamiclife.islamiclife.R;

/**
 * Created by User on 4/19/2017.
 */

public class RamadanFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ramadan_fragment, container, false);
        return view;
    }
    public static RamadanFragment newInstance() {
        RamadanFragment fragment = new RamadanFragment();
       // ...
        // do some initial setup if needed, for example Listener etc
      //  ...
        return fragment;
    }
}
