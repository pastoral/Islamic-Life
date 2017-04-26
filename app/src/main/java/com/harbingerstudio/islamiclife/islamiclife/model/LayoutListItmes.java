package com.harbingerstudio.islamiclife.islamiclife.model;

import com.harbingerstudio.islamiclife.islamiclife.R;

/**
 * Created by User on 4/14/2017.
 */

public class LayoutListItmes {
    public  String[] itemArray = {"নামায", "রোজা", "মসজিদ","কোরআন","হাদীস", "কিব্লাহ","তাসবীহ", "দুআ"};
    public  int[] itemWpArray = {R.drawable.namaj1, R.drawable.ramadan1, R.drawable.mosque1,
                                        R.drawable.quran1,R.drawable.hadith1,R.drawable.kiblah1,R.drawable.tasbih1, R.drawable.dua1};
     public String getItemTitle(int pos){
         return itemArray[pos];
     }
    public int getImageTitle(int pos){
        return itemWpArray[pos];
    }
}
