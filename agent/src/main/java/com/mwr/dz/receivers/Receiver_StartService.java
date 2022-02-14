package com.mwr.dz.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class Receiver_StartService extends BroadcastReceiver {
	
	public static final String PWN_INTENT = "com.mwr.dz.PWN";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent start_service = new Intent();
		start_service.putExtras(intent);
		
		if(intent.getCategories().contains("com.yaydevhackmodyay.drozer.START_EMBEDDED")) {
			start_service.addCategory("com.yaydevhackmodyay.drozer.START_EMBEDDED");
			start_service.setComponent(new ComponentName("com.yaydevhackmodyay.drozer", "com.mwr.dz.services.ServerService"));
		}
		else {
			if(intent.getCategories().contains("com.yaydevhackmodyay.drozer.CREATE_ENDPOINT"))
				start_service.addCategory("com.yaydevhackmodyay.drozer.CREATE_ENDPOINT");
			if(intent.getCategories().contains("com.yaydevhackmodyay.drozer.START_ENDPOINT"))
				start_service.addCategory("com.yaydevhackmodyay.drozer.START_ENDPOINT");
			
			start_service.setComponent(new ComponentName("com.yaydevhackmodyay.drozer", "com.mwr.dz.services.ClientService"));
		}

		context.startService(start_service);
	}

}
