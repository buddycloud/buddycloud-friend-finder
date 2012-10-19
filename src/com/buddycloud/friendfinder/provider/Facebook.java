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
import java.util.Properties;

import org.jamppa.component.PacketSender;

import com.buddycloud.friendfinder.Configuration;
import com.buddycloud.friendfinder.HashUtils;
import com.buddycloud.friendfinder.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Abmar
 *
 */
public class Facebook extends AbstractContactProvider {

	private static final String APPID_PROP = "facebook.appId";
	private static final String APPSECRET_PROP = "facebook.appSecret";
	private static final String CODE_PROP = "facebook.code";
	
	private static final String GRAPH_URL = "https://graph.facebook.com";
	
	/**
	 * @param properties
	 * @param packetSender
	 */
	public Facebook(Properties properties, PacketSender packetSender) {
		super(properties, packetSender);
	}

	
	@Override
	public String getAuthenticationURL(String userJid) 
			throws UnsupportedEncodingException {
		
		String appId = getProperties().getProperty(APPID_PROP);
		String appSecret = getProperties().getProperty(APPSECRET_PROP);
		String code = getProperties().getProperty(CODE_PROP);
		
		String callbackURL = getProperties().getProperty(Configuration.CALLBACK_URL);
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
	public ContactProfile getProfile(String accessToken) throws Exception {
		
		String meURL = GRAPH_URL + "/me?fields=id&access_token=" + accessToken;
		String myId = HttpUtils.consumeJSON(meURL).getAsJsonObject().get("id").getAsString();
		
		ContactProfile contactProfile = new ContactProfile(HashUtils.encodeSHA256(myId));
		
		String friendURL = GRAPH_URL + "/me/friends?access_token=" + accessToken;
		JsonObject friendsJson = HttpUtils.consumeJSON(friendURL).getAsJsonObject();
		
		JsonArray friendsArray = friendsJson.get("data").getAsJsonArray();
		for (JsonElement friendJson : friendsArray) {
			JsonObject friendObject = friendJson.getAsJsonObject();
			String friendId = friendObject.get("id").getAsString();
			contactProfile.addFriendHash(HashUtils.encodeSHA256(friendId));
		}
		
		return contactProfile;
	}
}
