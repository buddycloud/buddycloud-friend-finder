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

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.jamppa.component.utils.XMPPUtils;
import org.xmpp.packet.IQ;

import com.buddycloud.friendfinder.HashUtils;
import com.buddycloud.friendfinder.provider.ContactProfile;
import com.buddycloud.friendfinder.provider.ContactProvider;

/**
 * @author Abmar
 *
 */
public abstract class MatchContactFromContactProviderHandler extends AbstractQueryHandler {

	private ContactProvider contactProvider;
	
	public MatchContactFromContactProviderHandler(String namespace) {
		super(namespace);
	}

	/* (non-Javadoc)
	 * @see org.jamppa.component.handler.QueryHandler#handle(org.xmpp.packet.IQ)
	 */
	@Override
	public IQ handle(IQ iq) {
		Element queryElement = iq.getElement().element("query");
		String accessToken = queryElement.element("access_token").getText();
		ContactProfile profile = null;
		try {
			profile = getProvider().getProfile(accessToken);
		} catch (Exception e) {
			return XMPPUtils.error(iq, 
					"Could not retrieve contact profile. Namespace [" + getNamespace() + "]", 
					getLogger());
		}
		
		HashUtils.reportHash(iq.getFrom().toBareJID(), profile.getMyHash(), getDataSource());
		List<MatchedUser> reportedHashes = new LinkedList<MatchedUser>();
		
		for (String friendHash : profile.getMyFriendsHashes()) {
			String jid = HashUtils.retrieveJid(friendHash, getDataSource());
			if (jid != null) {
				reportedHashes.add(new MatchedUser(jid, friendHash));
			}
		}
		
		return createResponse(iq, reportedHashes);
	}

	private IQ createResponse(IQ iq, List<MatchedUser> matchedUsers) {
		IQ result = IQ.createResultIQ(iq);
		Element queryElement = result.getElement().addElement("query", getNamespace());
		for (MatchedUser user : matchedUsers) {
			Element itemEl = queryElement.addElement("item");
			itemEl.addAttribute("jid", user.getJid());
			itemEl.addAttribute("matched-hash", user.getHash());
		}
		return result;
	}
	
	private ContactProvider getProvider() {
		if (contactProvider == null) {
			contactProvider = createProvider();
		}
		return contactProvider;
	}

	/**
	 * @return
	 */
	protected abstract ContactProvider createProvider();
}
