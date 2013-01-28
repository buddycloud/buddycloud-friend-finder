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

import java.util.Iterator;
import java.util.Properties;

import org.dom4j.Element;
import org.jamppa.component.PacketSender;

import com.buddycloud.friendfinder.HashUtils;
import com.buddycloud.friendfinder.HttpUtils;

/**
 * @author Abmar
 *
 */
public class Gmail extends AbstractContactProvider implements OAuth2ContactProvider {

	private static final String GOOGLE_FEED = "https://www.google.com/m8/feeds/contacts/default/full";
	private static final String PROVIDER_NAME = "email";
	private static final int PAGE_SIZE = 50;
	
	/**
	 * @param properties
	 * @param packetSender
	 */
	public Gmail(Properties properties, PacketSender packetSender) {
		super(properties, packetSender);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContactProfile getProfile(String accessToken) throws Exception {
		boolean firstPage = true;
		ContactProfile contactProfile = null;
		int startIndex = 1;
		
		while (true) {
			StringBuilder urlBuilder = new StringBuilder(GOOGLE_FEED);
			urlBuilder.append("?start-index=").append(startIndex);
			urlBuilder.append("&max-results=").append(PAGE_SIZE);
			urlBuilder.append("&access_token=").append(accessToken);
			
			String contactsURL = urlBuilder.toString();
			Element xmlContacts = HttpUtils.consumeXML(contactsURL);
			
			if (firstPage) {
				String myId = xmlContacts.element("id").getText();
				contactProfile = new ContactProfile(
						HashUtils.encodeSHA256(PROVIDER_NAME, myId));
			}
			
			if (xmlContacts.element("entry") == null) {
				break;
			}
			Iterator<Element> friendsElements = xmlContacts.elementIterator("entry");
			while (friendsElements.hasNext()) {
				Element element = (Element) friendsElements.next();
				Element emailEl = element.element("email");
				if (emailEl == null) {
					continue;
				}
				String friendId = emailEl.attributeValue("address");
				contactProfile.addFriendHash(HashUtils.encodeSHA256(
						PROVIDER_NAME, friendId));
			}
			
			startIndex += PAGE_SIZE;
			firstPage = false;
		}
		
		return contactProfile;
	}
}
