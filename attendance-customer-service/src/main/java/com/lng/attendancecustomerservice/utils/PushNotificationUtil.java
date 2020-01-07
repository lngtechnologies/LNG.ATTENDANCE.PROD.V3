package com.lng.attendancecustomerservice.utils;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;


public class PushNotificationUtil {
	
	PushNotificationKey pushNotificationKey = new PushNotificationKey();

	public void SendPushNotification(String token, String body, String title) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		
		try {
			
			URIBuilder builder = new URIBuilder("https://fcm.googleapis.com/fcm/send");
			
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Authorization", "key="+pushNotificationKey.getAttndDevKey().trim());
			
			JSONObject notificationDetails = new JSONObject();
			JSONObject notification = new JSONObject();
			JSONObject data = new JSONObject();
			
			notificationDetails.put("to", token);
			notificationDetails.put("collapse_key", "type_a");
			
			notification.put("body", body);
			notification.put("title", title);
			
			data.put("body", body);
			data.put("title", title);
			data.put("key_1", "Value for key_1");
			data.put("key_2", "Value for key_2");
			
			notificationDetails.put("notification", notification);
			notificationDetails.put("data", data);
		    
			StringEntity reqEntity = new StringEntity(notificationDetails.toJSONString());
			request.setEntity(reqEntity);
			
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) 
			{
				System.out.println(EntityUtils.toString(entity));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
