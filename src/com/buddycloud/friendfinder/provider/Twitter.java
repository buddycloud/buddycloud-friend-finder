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

import twitter4j.IDs;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Abmar
 *
 */
public class Twitter extends AbstractContactProvider implements OAuth1ContactProvider {

	private static final String PROVIDER_NAME = "twitter";

	/**
	 * @param properties
	 * @param packetSender
	 */
	public Twitter(Properties properties, PacketSender packetSender) {
		super(properties, packetSender);
	}

	/* (non-Javadoc)
	 * @see com.buddycloud.friendfinder.provider.ContactProvider#getProfile(java.lang.String)
	 */
	@Override
	public ContactProfile getProfile(String accessToken, String accessTokenSecret) throws Exception {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(getProperties().getProperty("twitter.consumerKey"))
		  .setOAuthConsumerSecret(getProperties().getProperty("twitter.consumerSecret"))
		  .setOAuthAccessToken(accessToken)
		  .setOAuthAccessTokenSecret(accessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
		
		Long myId = twitter.getId();
		ContactProfile contactProfile = new ContactProfile(
				HashUtils.encodeSHA256(PROVIDER_NAME, myId.toString()));
		long nextCursor = -1;
		while (true) {
			IDs friendsIDs = twitter.getFollowersIDs(nextCursor);
			for (Long friendId : friendsIDs.getIDs()) {
				contactProfile.addFriendHash(
						HashUtils.encodeSHA256(PROVIDER_NAME, friendId.toString()));
			}
			if (!friendsIDs.hasNext()) {
				break;
			}
			nextCursor = friendsIDs.getNextCursor();
		}
		return contactProfile;
	}

}
