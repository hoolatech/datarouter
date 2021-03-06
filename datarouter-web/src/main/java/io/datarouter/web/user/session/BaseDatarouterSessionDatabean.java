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
package io.datarouter.web.user.session;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.datarouter.model.databean.BaseDatabean;
import io.datarouter.model.field.Field;
import io.datarouter.model.field.imp.DateField;
import io.datarouter.model.field.imp.DateFieldKey;

public abstract class BaseDatarouterSessionDatabean<
		PK extends BaseDatarouterSessionDatabeanKey<PK>,
		D extends BaseDatarouterSessionDatabean<PK,D>>
extends BaseDatabean<PK,D>{

	protected PK key;
	private Date created;//track how old the session is
	private Date updated;//last heartbeat time

	public static class FieldKeys{
		public static final DateFieldKey created = new DateFieldKey("created");
		public static final DateFieldKey updated = new DateFieldKey("updated");
	}

	public List<Field<?>> getNonKeyFields(){
		return Arrays.asList(
			new DateField(FieldKeys.created, created),
			new DateField(FieldKeys.updated, updated));
	}

	protected BaseDatarouterSessionDatabean(PK key){
		this.updated = new Date();
		this.key = key;
	}

	@Override
	public PK getKey(){
		return key;
	}

	public void setKey(PK key){
		this.key = key;
	}

	public String getSessionToken(){
		return key.getSessionToken();
	}

	public void setSessionToken(String token){
		this.key.setSessionToken(token);
	}

	public Date getUpdated(){
		return updated;
	}

	public void setUpdated(Date updated){
		this.updated = updated;
	}

	public Date getCreated(){
		return created;
	}

	public void setCreated(Date created){
		this.created = created;
	}

}
