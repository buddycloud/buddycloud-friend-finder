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

import java.util.Properties;

import org.jamppa.component.PacketSender;

import com.buddycloud.friendfinder.HashUtils;
import com.buddycloud.friendfinder.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Abmar
 *
 */
public class Facebook extends AbstractContactProvider implements OAuth2ContactProvider {

	private static final String GRAPH_URL = "https://graph.facebook.com";
	private static final String PROVIDER_NAME = "facebook";
	
	/**
	 * @param properties
	 * @param packetSender
	 */
	public Facebook(Properties properties, PacketSender packetSender) {
		super(properties, packetSender);
	}

	@Override
	public ContactProfile getProfile(String accessToken) throws Exception {
		
		StringBuilder meURLBuilder = new StringBuilder(GRAPH_URL);
		meURLBuilder.append("/me?fields=id&access_token=").append(accessToken);
		String myId = HttpUtils.consumeJSON(meURLBuilder.toString())
				.getAsJsonObject().get("id").getAsString();

		ContactProfile contactProfile = new ContactProfile(
				HashUtils.encodeSHA256(PROVIDER_NAME, myId));
		
		StringBuilder friendsURLBuilder = new StringBuilder(GRAPH_URL);
		friendsURLBuilder.append("/me/friends?fields=id&access_token=").append(accessToken);
		
		while (true) {
			JsonObject friendsJson = HttpUtils.consumeJSON(friendsURLBuilder.toString()).getAsJsonObject();
			
			JsonArray friendsArray = friendsJson.get("data").getAsJsonArray();
			for (JsonElement friendJson : friendsArray) {
				JsonObject friendObject = friendJson.getAsJsonObject();
				String friendId = friendObject.get("id").getAsString();
				contactProfile.addFriendHash(
						HashUtils.encodeSHA256(PROVIDER_NAME, friendId));
			}
			JsonObject paging = friendsJson.get("paging").getAsJsonObject();
			if (!paging.has("next")) {
				break;
			}
			friendsURLBuilder = new StringBuilder(paging.get("next").getAsString());
		}
		
		return contactProfile;
	}
}
