package com.harbingerstudio.islamiclife.islamiclife.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.harbingerstudio.islamiclife.islamiclife.R;

/**
 * Created by User on 5/3/2017.
 */

public class DailyPrayerTimeAdapter extends RecyclerView.Adapter<DailyPrayerTimeAdapter.DailyPrayerTimeHolder> {

    private String[] PRAYER_NAME_BAN , PRAYER_NAME_ENG, prayerTime;
    private LayoutInflater inflater;

    public DailyPrayerTimeAdapter(Context context, String[] str1 , String[] str2, String[] prayerTime){
        this.inflater = LayoutInflater.from(context);
        PRAYER_NAME_ENG = str1;
        PRAYER_NAME_BAN = str2;
        this.prayerTime = prayerTime;
    }

    class DailyPrayerTimeHolder extends RecyclerView.ViewHolder{
        private TextView prayernameenglish, prayernamebangla ;
        private TextView txtclock;
        private CardView namazcard;
        public DailyPrayerTimeHolder(View view){
            super(view);
            prayernameenglish = (TextView)view.findViewById(R.id.prayernameenglish);
            prayernamebangla = (TextView)view.findViewById(R.id.prayernamebangla);
            txtclock = (TextView)view.findViewById(R.id.txtclock);
            namazcard = (CardView)view.findViewById(R.id.namazcard);
        }
    }

    @Override
    public DailyPrayerTimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.daily_prayer_list, parent, false);
        return new DailyPrayerTimeHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyPrayerTimeHolder holder, int position) {
        holder.prayernameenglish.setText(PRAYER_NAME_ENG[position]);
        holder.prayernamebangla.setText(PRAYER_NAME_BAN[position]);
        holder.txtclock.setText(timeFormatting(prayerTime[position]));
        holder.namazcard.setCardBackgroundColor(getRandomColor());
    }

    @Override
    public int getItemCount() {
       return PRAYER_NAME_ENG.length;
    }

    private String timeFormatting(String s){
        String time, getString, remainString;
        int intTime;
        getString = s.substring(0,2);
        remainString = s.substring(2);
        intTime = Integer.parseInt(getString);
        if(intTime > 12){
            intTime = intTime - 12;
            time = "0" + String.valueOf(intTime)+ remainString + "PM";
        }
        else{
            time = s + "AM";
        }
        return time;
    }

    private int getRandomColor(){
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        return color;
    }
}
