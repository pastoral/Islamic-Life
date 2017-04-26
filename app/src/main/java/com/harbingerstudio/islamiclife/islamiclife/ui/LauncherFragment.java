package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.harbingerstudio.islamiclife.islamiclife.R;
import com.harbingerstudio.islamiclife.islamiclife.adapters.MainLayoutRecyclerAdapter;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListItmes;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListModel;
import com.harbingerstudio.islamiclife.islamiclife.utils.RecyclerItemClickListener;

/**
 * Created by User on 4/16/2017.
 */

public class LauncherFragment extends Fragment {
    private  View view;
    public RecyclerView recyclerView;
    private Fragment outputFragment;

    public LayoutListItmes layoutListItmes = new LayoutListItmes();
    public LayoutListModel[] layoutListModels = {new LayoutListModel(layoutListItmes.getItemTitle(0),layoutListItmes.getImageTitle(0)),
            new LayoutListModel(layoutListItmes.getItemTitle(1),layoutListItmes.getImageTitle(1)),
            new LayoutListModel(layoutListItmes.getItemTitle(2),layoutListItmes.getImageTitle(2)),
            new LayoutListModel(layoutListItmes.getItemTitle(3),layoutListItmes.getImageTitle(3)),
            new LayoutListModel(layoutListItmes.getItemTitle(4),layoutListItmes.getImageTitle(4)),
            new LayoutListModel(layoutListItmes.getItemTitle(5),layoutListItmes.getImageTitle(5)),
            new LayoutListModel(layoutListItmes.getItemTitle(6),layoutListItmes.getImageTitle(6)),
            new LayoutListModel(layoutListItmes.getItemTitle(7),layoutListItmes.getImageTitle(7))};
    private RecyclerView.LayoutManager layoutManager;
    public MainLayoutRecyclerAdapter mainLayoutRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
         view = inflater.inflate(R.layout.launcher_fragment, container,false);
       // Recy
        recyclerView = (RecyclerView)view.findViewById(R.id.layourrecycler);
        layoutManager = new LinearLayoutManager(getContext());
        //outputFragment = (FragmentManager)view.findViewById(R.id.launcherfragment);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setLayoutManager(layoutManager);
        mainLayoutRecyclerAdapter = new MainLayoutRecyclerAdapter(getContext(),layoutListModels);
        //recyclerView.hasFixedSize();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(mainLayoutRecyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position) {
                        String itemName = layoutListModels[position].getTitle();
                        Toast.makeText(getActivity(),"You clicked on" + String.valueOf(itemName), Toast.LENGTH_SHORT).show();
                        Fragment fragment = null;
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                                R.anim.fragment_slide_left_exit,
                                R.anim.fragment_slide_right_enter,
                                R.anim.fragment_slide_right_exit);

                        switch (itemName){
                            case "রোজা" :
                                fragment = new RamadanFragment();

                               // transaction.remove(new LauncherFragment());
                                //transaction.add(R.id.launcherfragment , new RamadanFragment().newInstance());
                                 transaction.replace(R.id.container_id ,fragment,"fragment-ramadan");
                                 transaction.addToBackStack("ramadan");
                                transaction.commit();
                                break;
                            case "মসজিদ" :
                                Intent intent = new Intent(getContext(), MosqueLocatorMap.class);
                                startActivity(intent);
                                break;
                        }
                    }
                }));

    }
}
