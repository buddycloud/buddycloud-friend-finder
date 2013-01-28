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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jamppa.component.db.ComponentDataSource;

/**
 * @author Abmar
 *
 */
public class HashUtils {

	private static final Logger LOGGER = Logger.getLogger(HashUtils.class);
	
	public static boolean reportHash(String jid, String hash, 
			ComponentDataSource dataSource) {
		
		if (retrieveJid(hash, dataSource) != null) {
			return false;
		}
		
		PreparedStatement statement = null;
		try {
			statement = dataSource.prepareStatement(
					"INSERT INTO \"contact-matches\"(\"credential-hash\", \"jid\") VALUES (?, ?)", 
					hash, jid);
			statement.execute();
			return true;
		} catch (SQLException e) {
			LOGGER.error("Could not report hash " + hash + " for jid " + jid, e);
			throw new RuntimeException(e);
		} finally {
			ComponentDataSource.close(statement);
		}
	}
	
	public static String retrieveJid(String hash, 
			ComponentDataSource dataSource) {
		PreparedStatement statement = null;
		try {
			statement = dataSource.prepareStatement(
					"SELECT \"jid\" FROM \"contact-matches\" WHERE \"credential-hash\"=?", 
					hash);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
			return null;
		} catch (SQLException e) {
			LOGGER.error("Could not retrieve jid from hash " + hash, e);
			throw new RuntimeException(e);
		} finally {
			ComponentDataSource.close(statement);
		}
	}

	public static String encodeSHA256(String str) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(str.getBytes("UTF-8"));
		byte[] digest = md.digest();
		return Base64.encodeBase64String(digest);
	}
	
	public static String encodeSHA256(String provider, String contactId)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update((provider + ":" + contactId).getBytes("UTF-8"));
		byte[] digest = md.digest();
		return Base64.encodeBase64String(digest);
	}
}
