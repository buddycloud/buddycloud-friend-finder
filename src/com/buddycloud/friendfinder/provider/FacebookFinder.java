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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Element;
import org.jamppa.component.PacketSender;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

import com.buddycloud.friendfinder.Configuration;
import com.buddycloud.friendfinder.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Abmar
 *
 */
public class FacebookFinder extends AbstractFriendFinder {

	private static final String APPID_PROP = "facebook.appId";
	private static final String APPSECRET_PROP = "facebook.appSecret";
	private static final String CODE_PROP = "facebook.code";
	private static final String CHANNELDIR_ADDRESS_PROP = "buddycloud.channeldir";
	
	private static final String GRAPH_URL = "https://graph.facebook.com";
	
	private static final String RSM_NS = "http://jabber.org/protocol/rsm";
	private static final String METADATA_NS = "http://buddycloud.com/channel_directory/metadata_query";

	/**
	 * @param properties
	 * @param packetSender
	 */
	public FacebookFinder(Properties properties, PacketSender packetSender) {
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
	public List<String> findFriends(String userJid, String accessToken) throws Exception {
		
		String friendURL = GRAPH_URL + "/me/friends?access_token=" + accessToken;
		JsonObject friendsJson = HttpUtils.consumeJSON(friendURL).getAsJsonObject();
		
		List<String> friendsResponse = new LinkedList<String>();
		
		JsonArray friendsArray = friendsJson.get("data").getAsJsonArray();
		for (JsonElement friendJson : friendsArray) {
			JsonObject friendObject = friendJson.getAsJsonObject();
			String friendName = friendObject.get("name").getAsString();
			String friendJid = searchPersonalChannel(friendName);
			friendsResponse.add(friendJid);
		}
		
		return friendsResponse;
	}

	private String searchPersonalChannel(String name) {
		IQ iq = new IQ();
		iq.setTo(getProperties().getProperty(CHANNELDIR_ADDRESS_PROP));
		
		Element queryEl = iq.getElement().addElement("query", METADATA_NS);
		queryEl.addElement("search").setText(name);
		Element rsmEl = queryEl.addElement("set", RSM_NS);
		rsmEl.addElement("max").setText("1");
		
		Packet packet = getPacketSender().syncSendPacket(iq);
		Element itemEl = packet.getElement().element("query").element("item");
		String jid = itemEl.attribute("jid").getValue();
		
		return jid;
	}

}
