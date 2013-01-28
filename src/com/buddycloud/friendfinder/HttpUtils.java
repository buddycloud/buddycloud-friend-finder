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

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * @author Abmar
 *
 */
public class HttpUtils {

	public static JsonElement consumeJSON(String URL) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse httpResponse = client.execute(httpGet);
		
		JsonElement parse = new JsonParser().parse(new JsonReader(
				new InputStreamReader(httpResponse.getEntity().getContent())));
		
		return parse;
	}
	
	public static Element consumeXML(String URL) throws Exception {
		SAXReader reader = new SAXReader();
        Document document = reader.read(new java.net.URL(URL));
		return document.getRootElement();
	}
	
	public static void post(String URL, Map<String, String> params) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		HttpParams httpParams = new BasicHttpParams();
		for (Entry<String, String> entryParam : params.entrySet()) {
			httpParams.setParameter(entryParam.getKey(), entryParam.getValue());
		}
		httpPost.setParams(httpParams);
		client.execute(httpPost);
	}
}
