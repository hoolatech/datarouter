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
package io.datarouter.web.user.databean;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.datarouter.model.databean.BaseDatabean;
import io.datarouter.model.field.Field;
import io.datarouter.model.field.imp.DateField;
import io.datarouter.model.field.imp.DateFieldKey;
import io.datarouter.model.field.imp.StringField;
import io.datarouter.model.field.imp.StringFieldKey;
import io.datarouter.model.serialize.fielder.BaseDatabeanFielder;
import io.datarouter.model.util.CommonFieldSizes;

public class SamlAuthnRequestRedirectUrl
extends BaseDatabean<SamlAuthnRequestRedirectUrlKey,SamlAuthnRequestRedirectUrl>{

	private SamlAuthnRequestRedirectUrlKey key;

	private String redirectUrl;
	private Date created;

	public SamlAuthnRequestRedirectUrl(){
		this.key = new SamlAuthnRequestRedirectUrlKey();
	}

	public SamlAuthnRequestRedirectUrl(String authnRequestId, String redirectUrl){
		this.key = new SamlAuthnRequestRedirectUrlKey(authnRequestId);
		this.redirectUrl = redirectUrl;
		this.created = new Date();
	}

	public static class FieldKeys{
		public static final StringFieldKey redirectUrl = new StringFieldKey("redirectUrl")
				.withSize(CommonFieldSizes.MAX_LENGTH_TEXT);
		public static final DateFieldKey created = new DateFieldKey("created");
	}

	public static class SamlAuthnRequestRedirectUrlFielder
	extends BaseDatabeanFielder<SamlAuthnRequestRedirectUrlKey,SamlAuthnRequestRedirectUrl>{

		public SamlAuthnRequestRedirectUrlFielder(){
			super(SamlAuthnRequestRedirectUrlKey.class);
		}

		@Override
		public List<Field<?>> getNonKeyFields(SamlAuthnRequestRedirectUrl databean){
			return Arrays.asList(
					new StringField(FieldKeys.redirectUrl, databean.redirectUrl),
					new DateField(FieldKeys.created, databean.created));
		}

	}

	@Override
	public SamlAuthnRequestRedirectUrlKey getKey(){
		return key;
	}

	@Override
	public Class<SamlAuthnRequestRedirectUrlKey> getKeyClass(){
		return SamlAuthnRequestRedirectUrlKey.class;
	}

	public String getRedirectUrl(){
		return redirectUrl;
	}

	public Date getCreated(){
		return created;
	}

}
