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
package com.buddycloud.friendfinder.handler;

import com.buddycloud.friendfinder.provider.ContactProvider;
import com.buddycloud.friendfinder.provider.Facebook;

/**
 * @author Abmar
 *
 */
public class MatchContactFromFacebookHandler extends MatchContactFromContactProviderHandler {

	private static final String NAMESPACE = "http://buddycloud.com/friend_finder/match_facebook";
	
	public MatchContactFromFacebookHandler() {
		super(NAMESPACE);
	}

	/* (non-Javadoc)
	 * @see com.buddycloud.friendfinder.handler.MatchContactFromContactProviderHandler#createProvider()
	 */
	@Override
	protected ContactProvider createProvider() {
		return new Facebook(getProperties(), getPacketSender());
	}
}
