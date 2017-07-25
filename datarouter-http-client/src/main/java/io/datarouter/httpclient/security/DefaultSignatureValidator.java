/**
 * Copyright © 2009 HotPads (admin@hotpads.com)
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
package io.datarouter.httpclient.security;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DefaultSignatureValidator implements SignatureValidator{
	private static final Logger logger = LoggerFactory.getLogger(DefaultSignatureValidator.class);

	private static final String HASHING_ALGORITHM = "SHA-256";
	private String salt;

	public DefaultSignatureValidator(String salt){
		this.salt = salt;
	}

	public boolean checkHexSignature(Map<String,String> params, String candidateSignature){
		// params might have it's own order, so see if it works without enforcing order first
		if(getHexSignatureWithoutSettingParameterOrder(params).equals(candidateSignature)){
			logger.warn("Successfully checked signature without checking parameter order");
			return true;
		}
		return getHexSignature(params).equals(candidateSignature);
	}

	@Override
	public boolean checkHexSignatureMulti(HttpServletRequest request){
		return checkHexSignature(multiToSingle(request.getParameterMap()),
				request.getParameter(SecurityParameters.SIGNATURE));
	}

	public byte[] sign(Map<String,String> map){
		return signWithoutSettingParameterOrder(new TreeMap<>(map));
	}

	public byte[] signWithoutSettingParameterOrder(Map<String, String> map){
		ByteArrayOutputStream signature = new ByteArrayOutputStream();
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance(HASHING_ALGORITHM);
		}catch(NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		for(Entry<String,String> entry : map.entrySet()){
			String parameterName = entry.getKey();
			if(parameterName.equals(SecurityParameters.SIGNATURE) || "submitAction".equals(parameterName)){
				continue;
			}
			try{
				String value = entry.getValue();
				String keyValue = parameterName.concat(value == null ? "" : value);
				String keyValueSalt = keyValue.concat(salt);
				md.update(keyValueSalt.getBytes(StandardCharsets.UTF_8));
				signature.write(md.digest());
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		return signature.toByteArray();
	}

	public String getHexSignature(Map<String,String> params){
		return Hex.encodeHexString(sign(params));
	}

	public String getHexSignatureWithoutSettingParameterOrder(Map<String,String> params){
		return Hex.encodeHexString(signWithoutSettingParameterOrder(params));
	}

	private Map<String, String> multiToSingle(Map<String, String[]> data){
		Map<String, String> map = new HashMap<>();
		for(Entry<String, String[]> entry : data.entrySet()){
			map.put(entry.getKey(), entry.getValue()[0]);
		}
		return map;
	}

	/*********************** Tests ******************/
	public static class DefaultSignatureValidatorTests{

		private Map<String,String> params;
		private DefaultSignatureValidator validator;

		@BeforeTest
		public void setup(){
			validator = new DefaultSignatureValidator("329jfsJLKFj2fjjfL2319Jvn2332we");

			params = new LinkedHashMap<>();
			params.put(SecurityParameters.SIGNATURE, "foobar");
			params.put("submitAction", "showListing");
			params.put("param1", "test1");
			params.put("param2", "test2");
			params.put("paramWithoutValue", null);
			params.put("csrfToken", "B8kgUfjdsa1234jsl9sdfkJ==");
			params.put("apiKey", "jklfds90j2r13kjJfjklJF923j2rjLKJfjs");
			params.put("csrfIv", "x92jfjJdslSJFj29lsfjsf==");
			params = Collections.unmodifiableMap(params);
		}

		@Test
		public void testSettingParameterOrder(){
			String originalSignature = validator.getHexSignature(params);
			Map<String,String> reorderedParams = new HashMap<>(params);
			String reorderedSignature = validator.getHexSignature(reorderedParams);

			Assert.assertEquals(originalSignature, reorderedSignature);
		}

		@Test
		public void testNotSettingParameterOrder(){
			String originalSignature = validator.getHexSignatureWithoutSettingParameterOrder(params);
			Map<String,String> reorderedParams = new HashMap<>(params);
			String reorderedSignature = validator.getHexSignatureWithoutSettingParameterOrder(reorderedParams);

			Assert.assertNotEquals(originalSignature, reorderedSignature);
		}

		@Test
		public void testCheckHexSignatureHandlesBothCases(){
			String signatureWithoutSettingOrder = validator.getHexSignatureWithoutSettingParameterOrder(params);
			Map<String,String> reorderedParams = new HashMap<>(params);
			String reorderedSignatureWithoutSettingOrder =
					validator.getHexSignatureWithoutSettingParameterOrder(reorderedParams);

			Assert.assertTrue(validator.checkHexSignature(params, signatureWithoutSettingOrder));
			Assert.assertTrue(validator.checkHexSignature(reorderedParams, reorderedSignatureWithoutSettingOrder));
			// check fails if signature was generated without setting order, and params are then reordered
			Assert.assertFalse(validator.checkHexSignature(reorderedParams, signatureWithoutSettingOrder));

			String signatureSettingOrder = validator.getHexSignature(params);
			String reorderedSignature = validator.getHexSignature(reorderedParams);

			Assert.assertTrue(validator.checkHexSignature(params, reorderedSignature));
			Assert.assertTrue(validator.checkHexSignature(reorderedParams, signatureSettingOrder));
		}
	}

}
