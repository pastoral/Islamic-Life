package com.harbingerstudio.islamiclife.islamiclife.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harbingerstudio.islamiclife.islamiclife.R;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListItmes;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListModel;

import java.util.Random;

/**
 * Created by User on 4/14/2017.
 */

public class MainLayoutRecyclerAdapter extends RecyclerView.Adapter<MainLayoutRecyclerAdapter.MainLayoutRecyclerHolder> {
// private TextView txtTitle;
  //  private ImageView imageTitle;
    private LayoutInflater inflater;
    private LayoutListModel[] layoutListModels;
    private Random mRandom = new Random();
    class MainLayoutRecyclerHolder extends RecyclerView.ViewHolder{
        TextView titleText;
        ImageView titleImage;
        CardView cardView;
        public MainLayoutRecyclerHolder(View view){
            super(view);
            titleText = (TextView)view.findViewById(R.id.titletext);
            titleImage = (ImageView)view.findViewById(R.id.titleimage);
            cardView = (CardView)view.findViewById(R.id.tilecard);
        }
    }
    public MainLayoutRecyclerAdapter(Context context, LayoutListModel[] layoutListModels){
        this.inflater = LayoutInflater.from(context);
        this.layoutListModels = layoutListModels;
    }

    @Override
    public MainLayoutRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_layout_list_item,parent,false);
        return new MainLayoutRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(MainLayoutRecyclerHolder holder, int position) {
        holder.titleText.setText(layoutListModels[position].getTitle());
        holder.titleImage.setImageResource(layoutListModels[position].getImageurl());
       // holder.cardView.getLayoutParams().height = getRandomIntInRange(290,125);
    }

    @Override
    public int getItemCount() {
        return layoutListModels.length;
    }
    protected int getRandomIntInRange(int max, int min){
        return mRandom.nextInt((max-min)+min)+min;
    }
}
