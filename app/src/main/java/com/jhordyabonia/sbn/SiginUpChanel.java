package com.jhordyabonia.sbn;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jhordyabonia.models.Store;
import com.jhordyabonia.webservice.Asynchtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class SiginUpChanel extends Activity implements OnClickListener,Asynchtask
{
	Store store;
	public static final String MODE_EDIT="mode_edit";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);       

		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        getWindow().setFlags(
        		WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
        	    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        
		
		setContentView(R.layout.activity_sigin_up_chanel);
		store= new Store(this);

		findViewById(R.id.bingo_logo)
		.setOnClickListener(this);
		findViewById(R.id.awards_images)
		.setOnClickListener(this);
		findViewById(R.id.send)
		.setOnClickListener(this);
		
        Intent intent=getIntent();
        int  edit=0;
        if(intent!=null)
		  edit=intent.getIntExtra(MODE_EDIT, edit);
        
		try 
		{
			if(edit==0)
				if(!store.get().getString(Store.BINGO_NAME).isEmpty())
				{
					Intent intent2= new Intent(this,Game.class);
					intent2.putExtra(Game.DISPLAY,Game.TABLES);
					startActivity(intent2);
					finish();
					return;
				}
			load();
		} catch (JSONException e) {}		
	}
	private void load() throws JSONException
	{
		((TextView)findViewById(R.id.bingo_name))
		.setText(store.get().getString(Store.BINGO_NAME));
		((TextView)findViewById(R.id.bingo_date))
		.setText(store.get().getString(Store.BINGO_DATE));
		((TextView)findViewById(R.id.bingo_cost))
		.setText(store.get().getString(Store.BINGO_COST));
		logo();
		((TextView)findViewById(R.id.author_name))
		.setText(store.get().getString(Store.AUTHOR_NAME));
		((TextView)findViewById(R.id.author_cellular))
		.setText(store.get().getString(Store.AUTHOR_CELLULAR));
		((TextView)findViewById(R.id.author_email))
		.setText(store.get().getString(Store.AUTHOR_EMAIL));
		((TextView)findViewById(R.id.author_address))
		.setText(store.get().getString(Store.AUTHOR_ADDRESS));
		((TextView)findViewById(R.id.awards_name))
		.setText(store.get().getString(Store.AWARDS_NAME));
		awards_images();
		((TextView)findViewById(R.id.pay_info))
		.setText(store.get().getString(Store.PAY_INFO));
		((TextView)findViewById(R.id.pay_account_number))
		.setText(store.get().getString(Store.PAY_ACCOUNT_NUMBER));	

		((TextView)findViewById(R.id.send))
		.setText("Guardar");
	}
	private String logo(){return "";}
	private String awards_images(){return "";}
	@Override
	public void onClick(View arg0) {
		if(arg0.getId()==R.id.awards_images||arg0.getId()==R.id.bingo_logo)
		{
			Intent intent2= new Intent(this,Awards.class);
			//intent2.putExtra(Game.DISPLAY,Game.TABLES);
			startActivity(intent2);
			return;
		}
		String data[]=new String[12];
		data[0]=((TextView)findViewById(R.id.bingo_name)).getText().toString();
		data[1]=((TextView)findViewById(R.id.bingo_date)).getText().toString();
		data[2]=((TextView)findViewById(R.id.bingo_cost)).getText().toString();
		data[3]=logo();
		data[4]=((TextView)findViewById(R.id.author_name)).getText().toString();
		data[5]=((TextView)findViewById(R.id.author_address)).getText().toString();
		data[6]=((TextView)findViewById(R.id.author_cellular)).getText().toString();
		data[7]=((TextView)findViewById(R.id.author_email)).getText().toString();
		data[8]=((TextView)findViewById(R.id.awards_name)).getText().toString();
		data[9]=awards_images();
		data[10]=((TextView)findViewById(R.id.pay_info)).getText().toString();
		data[11]=((TextView)findViewById(R.id.pay_account_number)).getText().toString();
		try {
			store.put(data[0],data[1],data[2],data[3],data[4],data[5],
					data[6],data[7],data[8],data[9],data[10],data[11]);			

			 HashMap<String, String> datos=new HashMap<String, String>();
			 datos.put(Server.CELLULAR, data[6]);
			 datos.put(Server.BINGO, store.get().toString());
			 Server.setDataToSend(datos);
			 Server.send(Server.BINGO, this, this);
			
		} catch (JSONException e) 
		{
			Toast.makeText(this, "Verifica los datos e intenta de nuevo",
					Toast.LENGTH_LONG).show();			
		}
	}
	@Override
	public void processFinish(String result) {
		 
		try
		{
			new JSONObject(result);
			store.save(this);
			Intent intent= new Intent(this,Game.class);
			intent.putExtra(Game.DISPLAY,Game.TABLES);
			startActivity(intent);
			finish();
		}catch (JSONException e)
		{
			Toast.makeText(this,  "Error de conexion intenta mas tarde",
					Toast.LENGTH_LONG).show();	
		}
	}

}
