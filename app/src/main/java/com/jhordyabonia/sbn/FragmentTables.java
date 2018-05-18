package com.jhordyabonia.sbn;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jhordyabonia.models.Adapter;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.webservice.Asynchtask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

 public class FragmentTables extends Fragment implements OnClickListener,SearchView.OnQueryTextListener,Asynchtask {
	DialogFragment add;
	Adapter base;
	TextView date,count;
	 ListView view;
    View root;
    Game GAME;
	private SearchView mSearchView;        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
         root= inflater.inflate(R.layout.fragment_tables, container, false);
         GAME= (Game)getActivity(); 
         

         date=(TextView)root.findViewById(R.id.last_singup);
         count=(TextView)root.findViewById(R.id.number_singup);
         root.findViewById(R.id.add).setOnClickListener(this);
         root.findViewById(R.id.search).setOnClickListener(this);
         base = new Adapter(GAME,new ArrayList<Adapter.Item>());
 		 view =(ListView)root.findViewById(R.id.tables);
  	     mSearchView = (SearchView) root.findViewById(R.id.search);
 		 view.setAdapter(base);
 		 view.setDividerHeight(0);
 	     makeDialog(); 	     
       
        view.setTextFilterEnabled(true);
        setupSearchView();
       return root;     
   }

   private void setupSearchView() {
       mSearchView.setIconifiedByDefault(false);
       mSearchView.setOnQueryTextListener(this);
       mSearchView.setSubmitButtonEnabled(false);
       mSearchView.setQueryHint("Buscar");
   }

   public boolean onQueryTextChange(String newText) {
       if (TextUtils.isEmpty(newText)) {
           view.clearTextFilter();
       } else {
           view.setFilterText(newText.toString());
       }
       return true;
   }

   public boolean onQueryTextSubmit(String query) {
       return false;
   }
    @Override
    public void onResume()
    {
    	super.onResume();
    	load();
    }
    private void load()
    {
    	 String title="Bingo Nomada";
    	 base.clear();
         try
         {
        	 JSONObject store = GAME.store.get();
        	 date.setText("Ultimo registro " + GAME.store.last_logup());
        	 count.setText(store.getJSONArray(Game.TABLES).length()+" Tablas registradas");
        	 title=store.getString(Store.BINGO_NAME);
        	 JSONArray tables=store.getJSONArray(Game.TABLES);
        	 for(int y=0;y<tables.length();y++)
        	 {
        		 JSONObject obj=new JSONObject(tables.optString(y));
				 Adapter.Item tt= new Adapter.Item(obj.getString(Server.NAME)
						 ,obj.getString(Server.CELLULAR),obj.getString(Server.EMAIL));
					
        			base.add(tt);    
        	 }
        } catch(JSONException e){}
       ((TextView)root.findViewById(R.id.title))
        .setText(title);
    }
    private void makeDialog()
	{
		add = new DialogFragment() 
		{
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				AlertDialog.Builder builder = 
						new AlertDialog.Builder(GAME);

			    View nView = getActivity().getLayoutInflater()
		    			.inflate(R.layout.add, null);
				final EditText cellular= (EditText)nView.findViewById(R.id.add_table);
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{	
						if(which==DialogInterface.BUTTON_POSITIVE)
						{
							HashMap<String, String> datos=new HashMap<String, String>();
							datos.put(Server.CELLULAR,cellular.getText().toString());
							Server.setDataToSend(datos);
							Server.send("add", GAME, FragmentTables.this);
						}
						else dialog.dismiss();						
					}
				};

				builder.setTitle("Registrar tabla")
				.setIcon(R.drawable.ic_launcher)
				.setView(nView)
				.setNegativeButton("Cerrar", listener)
				.setPositiveButton(" Ok ", listener)
				.setCancelable(false);				
				return builder.create();
			}
		};	
	}	
	@Override
	public void processFinish(String table)
    {
    	try
    	{
    		GAME.store.putTable(table);
    		GAME.store.save(GAME);
    		
       	 	JSONObject obj=new JSONObject(table);
    		Adapter.Item tt= new Adapter.Item(obj.getString(Server.NAME)
					 ,obj.getString(Server.CELLULAR),obj.getString(Server.EMAIL));
					
     		base.add(tt);
     		base.setDropDownViewResource(base.getCount()-1);
    	}catch(JSONException e){}    	
    }
	@Override
	public void onClick(View arg0)
	{
	   switch(arg0.getId())
	  {
	  	case R.id.search:
			break;
		case R.id.add:
			add.show(GAME.getSupportFragmentManager(), "missiles");	
			break;
		default:				
		}
	}
}

