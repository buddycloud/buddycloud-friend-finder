/*
 * Copyright 2011 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.friendfinder.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.buddycloud.friendfinder.Configuration;
import com.buddycloud.friendfinder.HttpUtils;
import com.buddycloud.friendfinder.model.Friend;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Abmar
 *
 */
public class Facebook implements ContactProvider {

	private static final String APPID_PROP = "facebook.appId";
	private static final String APPSECRET_PROP = "facebook.appSecret";
	private static final String CODE_PROP = "facebook.code";
	
	private static final String GRAPH_URL = "https://graph.facebook.com";
	
	@Override
	public String getAuthenticationURL(Properties properties, String userJid) 
			throws UnsupportedEncodingException {
		
		String appId = properties.getProperty(APPID_PROP);
		String appSecret = properties.getProperty(APPSECRET_PROP);
		String code = properties.getProperty(CODE_PROP);
		
		String callbackURL = properties.getProperty(Configuration.CALLBACK_URL);
		callbackURL += "?provider=facebook&user_jid=" + userJid;
		
		String callbackURLEncoded = URLEncoder.encode(callbackURL, "UTF-8");
		
		String returnURL = GRAPH_URL + "/oauth/access_token" +
			"?client_id=" + appId + 
			"&redirect_uri=" + callbackURLEncoded + 
			"&client_secret=" + appSecret + 
			"&code=" + code + "&display=popup";
		
		return returnURL;
	}

	@Override
	public List<Friend> getFriends(Properties properties, String userJid,
			String accessToken) throws Exception {
		
		String friendURL = GRAPH_URL + "/me/friends?access_token=" + accessToken;
		JsonObject friendsJson = HttpUtils.consumeJSON(friendURL).getAsJsonObject();
		
		List<Friend> friendsResponse = new LinkedList<Friend>();
		
		JsonArray friendsArray = friendsJson.get("data").getAsJsonArray();
		for (JsonElement friendJson : friendsArray) {
			JsonObject friendObject = friendJson.getAsJsonObject();
			String friendId = friendObject.get("id").getAsString();
			String friendName = friendObject.get("name").getAsString();
			friendsResponse.add(new Friend(friendName, friendId));
		}
		
		return friendsResponse;
	}

	@Override
	public void inviteFriend(Properties properties, Friend friend,
			String userJid, String accessToken) throws Exception {
		
		String friendURL = GRAPH_URL + "/" + friend.getId() + "/feed" +
				"?link=http://developers.facebook.com/docs/reference/dialogs/" +
				"&picture=http://fbrell.com/f8.jpg" +
				"&name=Buddycloud invitation" +
				"&caption=Come to buddycloud!" +
				"&description=Buddycloud invitation!";
		
		HttpUtils.post(friendURL, new HashMap<String, String>());
	}

}
