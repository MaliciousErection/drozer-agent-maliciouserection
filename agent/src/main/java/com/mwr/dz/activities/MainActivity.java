package com.mwr.dz.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mwr.dz.Agent;
import com.mwr.dz.EndpointAdapter;
import com.mwr.dz.R;
import com.mwr.dz.views.EndpointListView;
import com.mwr.dz.views.ServerListRowView;
import com.mwr.jdiesel.api.connectors.Endpoint;

public class MainActivity extends Activity {
	
	private EndpointListView endpoint_list_view = null;
	private ServerListRowView server_list_row_view = null;
	private static final int STORAGE_PERMISSION_CODE = 101;
	
	private void launchEndpointActivity(Endpoint endpoint) {
		Intent intent = new Intent(MainActivity.this, EndpointActivity.class);
		intent.putExtra(Endpoint.ENDPOINT_ID, endpoint.getId());
		
		MainActivity.this.startActivity(intent);
	}
	
	private void launchServerActivity() {
		MainActivity.this.startActivity(new Intent(MainActivity.this, ServerActivity.class));
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Agent.getInstance().setContext(this.getApplicationContext());
        
        setContentView(R.layout.activity_main);
		//checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        
        this.endpoint_list_view = (EndpointListView)this.findViewById(R.id.endpoint_list_view);
        this.endpoint_list_view.setAdapter(new EndpointAdapter(this.getApplicationContext(), Agent.getInstance().getEndpointManager(),
        		new EndpointAdapter.OnEndpointSelectListener() {

		        	@Override
					public void onEndpointSelect(Endpoint endpoint) {
						MainActivity.this.launchEndpointActivity(endpoint);
					}
		
					@Override
					public void onEndpointToggle(Endpoint endpoint, boolean isChecked) {
						if(isChecked)
							MainActivity.this.startEndpoint(endpoint);
						else
							MainActivity.this.stopEndpoint(endpoint);
					}
					
		}));
        
        this.server_list_row_view = (ServerListRowView)this.findViewById(R.id.server_list_row_view);
        this.server_list_row_view.setServerParameters(Agent.getInstance().getServerParameters());
        this.server_list_row_view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.launchServerActivity();
			}
        	
        });
        this.server_list_row_view.setServerViewListener(new ServerListRowView.OnServerViewListener() {
			
			@Override
			public void onToggle(boolean toggle) {
				if(toggle)
					MainActivity.this.startServer();
				else
					MainActivity.this.stopServer();
			}
			
		});
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.menu_refresh:
    		this.updateEndpointStatuses();
    		this.updateServerStatus();
    		return true;
    	
    	case R.id.menu_settings:
    		this.startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	Agent.getInstance().unbindServices();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	Agent.getInstance().bindServices();
    }
    
    private void startServer(){
    	try {
			Agent.getInstance().getServerService().startServer(Agent.getInstance().getServerParameters(), Agent.getInstance().getMessenger());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void stopServer(){
    	try {
			Agent.getInstance().getServerService().stopServer(Agent.getInstance().getServerParameters(), Agent.getInstance().getMessenger());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void startEndpoint(Endpoint endpoint){
    	try {
    		Agent.getInstance().getClientService().startEndpoint(endpoint, Agent.getInstance().getMessenger());
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void stopEndpoint(Endpoint endpoint){
    	try {
    		Agent.getInstance().getClientService().stopEndpoint(endpoint, Agent.getInstance().getMessenger());
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    protected void updateEndpointStatuses() {
    	try {
			for(Endpoint e : Agent.getInstance().getEndpointManager().all())
				e.setStatus(Endpoint.Status.UPDATING);
			
			Agent.getInstance().getClientService().getEndpointStatuses(Agent.getInstance().getMessenger());
		}
		catch(RemoteException e) {
			for(Endpoint e2 : Agent.getInstance().getEndpointManager().all())
				e2.setStatus(Endpoint.Status.UNKNOWN);
			
			Toast.makeText(this, R.string.service_offline, Toast.LENGTH_SHORT).show();
		}
    }
    
    protected void updateServerStatus() {
		try {
			Agent.getInstance().getServerParameters().setStatus(com.mwr.jdiesel.api.connectors.Server.Status.UPDATING);
			
			Agent.getInstance().getServerService().getServerStatus(Agent.getInstance().getMessenger());
		}
		catch (RemoteException e) {
			Agent.getInstance().getServerParameters().setStatus(Endpoint.Status.UNKNOWN);
			
			Toast.makeText(this, R.string.service_offline, Toast.LENGTH_SHORT).show();
		}
    }

	public void checkPermission(String permission, int requestCode) {
		// Checking if permission is not granted
		if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[] {
					permission
			}, requestCode);
		}
		else {
			Toast.makeText(MainActivity.this,"Permission already granted", Toast.LENGTH_SHORT).show();
		}
	}
    
}
