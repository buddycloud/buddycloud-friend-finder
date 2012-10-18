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
public abstract class AbstractFriendFinder implements FriendFinder {

	private final Properties properties;
	private final PacketSender packetSender;
	
	public AbstractFriendFinder(Properties properties, PacketSender packetSender) {
		this.properties = properties;
		this.packetSender = packetSender;
	}
	
	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * @return the packetSender
	 */
	public PacketSender getPacketSender() {
		return packetSender;
	}
}
