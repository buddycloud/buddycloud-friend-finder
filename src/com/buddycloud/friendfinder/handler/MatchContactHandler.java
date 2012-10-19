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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.buddycloud.friendfinder.HashUtils;

/**
 * @author Abmar
 *
 */
public class MatchContactHandler extends AbstractQueryHandler {

	/**
	 * 
	 */
	private static final String NAMESPACE = "http://buddycloud.com/friend_finder/match";

	/**
	 * @param namespace
	 */
	public MatchContactHandler() {
		super(NAMESPACE);
	}

	/* (non-Javadoc)
	 * @see org.jamppa.component.handler.QueryHandler#handle(org.xmpp.packet.IQ)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IQ handle(IQ iq) {
		Element queryElement = iq.getElement().element("query");
		Iterator<Element> itemIterator = queryElement.elementIterator("item");
		
		List<MatchedUser> reportedHashes = new LinkedList<MatchedUser>();
		
		while (itemIterator.hasNext()) {
			Element item = (Element) itemIterator.next();
			Attribute meAttr = item.attribute("me");
			Attribute hashAttr = item.attribute("item-hash");
			
			String hash = hashAttr.getValue();
			
			if (meAttr != null && meAttr.getValue().equals("true")) {
				HashUtils.reportHash(iq.getFrom().toBareJID(),
						hash, getDataSource());
			} else {
				String jid = HashUtils.retrieveJid(hash, 
						getDataSource());
				if (jid != null) {
					reportedHashes.add(new MatchedUser(jid, hash));
				}
			}
		}
		
		return createResponse(iq, reportedHashes);
	}

	/**
	 * @param iq
	 * @param matchedUsers
	 * @return
	 */
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
}
