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
package io.datarouter.storage.profile.trace.key;

import java.util.Arrays;
import java.util.List;

import io.datarouter.model.field.Field;
import io.datarouter.model.field.imp.comparable.LongField;
import io.datarouter.model.field.imp.comparable.LongFieldKey;
import io.datarouter.model.key.entity.base.BaseEntityKey;
import io.datarouter.model.key.entity.base.BaseEntityPartitioner;

public class TraceEntityKey
extends BaseEntityKey<TraceEntityKey>{

	/************* fields *************************/

	private Long traceId;

	public static class FieldKeys{
		public static final LongFieldKey traceId = new LongFieldKey("traceId");
	}

	@Override
	public List<Field<?>> getFields(){
		return Arrays.asList(
				new LongField(FieldKeys.traceId, traceId));
	}

	public static class TraceEntityPartitioner extends BaseEntityPartitioner<TraceEntityKey>{
		@Override
		public int getNumPartitions(){
			return 16;
		}
		@Override
		public int getPartition(TraceEntityKey ek){
			return (int)(ek.getTraceId() % getNumPartitions());
		}
	}

	/****************** construct *******************/

	public TraceEntityKey(){

	}

	public TraceEntityKey(Long traceId){
		this.traceId = traceId;
	}

	/********************** get/set ***************************/

	public Long getTraceId(){
		return traceId;
	}

}
