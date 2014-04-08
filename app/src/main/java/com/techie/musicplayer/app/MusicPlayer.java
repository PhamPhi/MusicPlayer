package com.techie.musicplayer.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MusicPlayer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Drawable ijmu = getResources().getDrawable(R.drawable.main_background);
        Bitmap bitmap = CommonUtils.drawableToBitmap(ijmu);
        Bitmap result = CommonUtils.onFastblur(bitmap, 500);

        RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.layout_main);
        Drawable background = new BitmapDrawable(getResources(), result);
        main_layout.setBackground(background);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
