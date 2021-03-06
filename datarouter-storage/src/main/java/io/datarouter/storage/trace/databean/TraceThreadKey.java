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
/**
 *
 */
package io.datarouter.storage.trace.databean;

public class TraceThreadKey extends BaseTraceThreadKey<TraceEntityKey,TraceThreadKey>{

	public TraceThreadKey(){
		entityKey = new TraceEntityKey();
	}

	public TraceThreadKey(String traceId, Long threadId){
		super(threadId);
		entityKey = new TraceEntityKey(traceId);
	}

	@Override
	public TraceThreadKey prefixFromEntityKey(TraceEntityKey entityKey){
		return new TraceThreadKey(entityKey.getTraceEntityId(), null);
	}

}