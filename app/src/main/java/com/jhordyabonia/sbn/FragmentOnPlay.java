package com.jhordyabonia.sbn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONException;

import com.jhordyabonia.models.Store;
import com.jhordyabonia.webservice.Asynchtask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public  class FragmentOnPlay extends Fragment implements OnClickListener,Asynchtask {
    public static final String ARG_SECTION_NUMBER = "section_number";
    private String number_now=""; 
    private boolean STOP= true;
	private int count=74;
    View root,send;
    Game GAME;

    ArrayList<Integer> already=new ArrayList<Integer>();
    int id_last[]={R.id.last_0,R.id.last_4,R.id.last_3,R.id.last_2,R.id.last_1};
    int id_numbers[]={
    		R.id.TextView08,R.id.TextView09,R.id.TextView07,
    		R.id.TextView05,R.id.TextView06,R.id.TextView04,
    		R.id.TextView10,R.id.TextView02,R.id.TextView01,R.id.TextView03
    		};
    TextView numbers[]=new TextView[id_numbers.length],last[]= new TextView[id_last.length];
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
         root= inflater.inflate(R.layout.fragment_onplay, container, false);
         GAME= (Game)getActivity();
         int m=0;
         for(int t:id_last)
        	last[m++]=(TextView)root.findViewById(t);
         m=0;
         for(int t:id_numbers)
         {
        	numbers[m]=(TextView)root.findViewById(t);
       	    numbers[m++].setOnClickListener(this);
         }
         root.findViewById(R.id.play).setOnClickListener(this);
         root.findViewById(R.id.del).setOnClickListener(this);
         send=root.findViewById(R.id.send);
         send.setOnClickListener(this);
         
         ArrayAdapter<String> base =
 				new ArrayAdapter<String>(getActivity(),R.layout.base);
 		ListView view =(ListView)root.findViewById(R.id.events);
 		view.setAdapter(base);
 		base.add("Inicio ");
 		base.add(" ");
        return root;
    }
    private void play()
    {
    	(new AsyncTask<Boolean, Void, Boolean>()
				{
					@Override
					protected Boolean doInBackground(Boolean... arg0) 
					{
						try {Thread.sleep(5000);}
						catch (InterruptedException e) {}
						return false;
					}
					@Override
					protected void onPostExecute(Boolean v)
					{
						Random r=new Random();
						int n=(r.nextInt(count)+1),c=0;
						while(already.indexOf(n)!=-1)
							if(c++>=count) {n=0;STOP=true;break;}
							else n=(r.nextInt(count)+1);
						number_now=""+n;
						count=74;
						send();
						if(!STOP)
							play();
					}	
				}).execute();
    }
    public void send()
    {
    	int number=0;
		try{number=Integer.parseInt(number_now.replace(",",""));}
		catch(NumberFormatException e){}
		String n="0"+number;
		String now=n.substring(n.length()-2,n.length());
    	 HashMap<String, String> datos=new HashMap<String, String>();
		 datos.put(Server.NUMBER,now);
		 try{datos.put(Server.CELLULAR, GAME.store.get().getString(Store.AUTHOR_CELLULAR));}
		 catch(JSONException e){}
		 Server.setDataToSend(datos);
		 Server.send("onplay", null, this);
    }
	@Override
	public void processFinish(String result) 
	{
    	int number=0;    	
		try{number=Integer.parseInt(result);}
		catch(NumberFormatException e){}
		if(number<75)
		{					
			String n="0"+number;
			String now=n.substring(n.length()-2,n.length());
			if(already.indexOf(number)==-1)
			{
				String _last="";
				for(int t=0;t<last.length-1;t++)
				{	
					_last=last[t+1].getText().toString();
					last[t+1].setText(last[t].getText());
					last[t].setText(_last);
				}
				already.add(number);
				last[0].setText(now);
				GAME.speaker.speak(now, TextToSpeech.QUEUE_FLUSH, null);
			}else
				Toast.makeText(GAME, now+" Ya salío", Toast.LENGTH_LONG).show();
		}else
			Toast.makeText(GAME, number+" Es muy grande", Toast.LENGTH_LONG).show();
		//Toast.makeText(GAME, result, Toast.LENGTH_LONG).show();
		
    }
	@Override
	public void onClick(View arg0)
	{
	   switch(arg0.getId())
	  {
		case R.id.send:
			send();
		case R.id.del:
			for(TextView arg:numbers)
             {arg.setBackgroundResource(R.drawable.number);}
			number_now="";
			break;
		case R.id.events:
			break;
		case R.id.play:
			if(!STOP)
			{
				((TextView)root.findViewById(R.id.text_play))
				.setText("Empezar");
				((ImageView)root.findViewById(R.id.img_play))
				.setImageResource(android.R.drawable.ic_media_play);
				
			}else
			{
				play();
				((TextView)root.findViewById(R.id.text_play))
				.setText("Detener");
				((ImageView)root.findViewById(R.id.img_play))
				.setImageResource(android.R.drawable.ic_media_pause);
			}
			STOP=!STOP;
			break;
		default:
			if(number_now.length()<3)
			{
				arg0.setBackgroundResource(R.drawable.number_marked);
				number_now+=((TextView)arg0).getText()+",";
			}
		}
	}
}

