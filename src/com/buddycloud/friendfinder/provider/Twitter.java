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

/**
 * @author Abmar
 *
 */
public class Twitter extends AbstractContactProvider {

	/**
	 * @param properties
	 * @param packetSender
	 */
	public Twitter(Properties properties, PacketSender packetSender) {
		super(properties, packetSender);
	}

	/* (non-Javadoc)
	 * @see com.buddycloud.friendfinder.provider.ContactProvider#getAuthenticationURL(java.lang.String)
	 */
	@Override
	public String getAuthenticationURL(String userJid) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.buddycloud.friendfinder.provider.ContactProvider#getProfile(java.lang.String)
	 */
	@Override
	public ContactProfile getProfile(String accessToken) throws Exception {
		return null;
	}

}
