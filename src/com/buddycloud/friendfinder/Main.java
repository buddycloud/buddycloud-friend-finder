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
package com.buddycloud.friendfinder;

import java.io.FileInputStream;
import java.util.Properties;

import org.jamppa.component.XMPPComponent;

import com.buddycloud.friendfinder.provider.FacebookFinder;


/**
 * @author Abmar
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		
		Properties properties = new Properties();
		properties.load(new FileInputStream("configuration.properties"));
		
		XMPPComponent xmppComponent = new XMPPComponent(properties);
		xmppComponent.setDescription("buddycloud's friend finder");
		xmppComponent.setName("buddycloud's friend finder ");
		xmppComponent.setDiscoInfoIdentityCategory("Friend finder");
		xmppComponent.setDiscoInfoIdentityCategoryType("Directory");
		xmppComponent.run();
		
		new FacebookFinder(properties, xmppComponent);
	}
	
}
