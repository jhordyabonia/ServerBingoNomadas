package com.jhordyabonia.sbn;

import java.util.Locale;

import com.jhordyabonia.models.Store;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class Game extends FragmentActivity implements ActionBar.TabListener {

    public static final String DISPLAY="display",ONPLAY="onPlay",TABLES="tables";
    ViewPager mViewPager;   
    FragmentTables tables;
    FragmentOnPlay onPlay;
	TextToSpeech speaker;
	Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tables=new FragmentTables();
        onPlay= new FragmentOnPlay();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {      
            @Override
            public Fragment getItem(int position) {
             if(position==1) return onPlay;
             else  return tables;
            }
            @Override
            public int getCount() { return 2;}
            @Override
            public CharSequence getPageTitle(int position) {
                Locale l = Locale.getDefault();
                if(position==1) return "empezar".toUpperCase(l);
                else  return "tablas".toUpperCase(l);
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) 
            {  
            	actionBar.setSelectedNavigationItem(position);
            	if(position==1)
            	{
            		actionBar.hide();	
	                getWindow().setFlags(
                		WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
                	    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            	}else
            	{
            		actionBar.show();
            		getWindow().setFlags(
                    		WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
                    	    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            	}
            }
        });
        PagerAdapter adapter = mViewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this));
        }
        speaker=new TextToSpeech(this,  
        		new OnInitListener()
        		{
					@Override
					public void onInit(int arg0) {}
				});
        store= new Store(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem m)
    {
    	switch(m.getItemId())
    	{
    	case R.id.action_account:
    		Intent intent = new Intent(this,SiginUpChanel.class);
	    	intent.putExtra(SiginUpChanel.MODE_EDIT, 1);
	    	startActivity(intent);
    	}
	    return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
         getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }   
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
    {mViewPager.setCurrentItem(tab.getPosition());}
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
}
