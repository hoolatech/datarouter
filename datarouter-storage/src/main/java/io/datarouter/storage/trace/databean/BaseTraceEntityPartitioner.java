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
package io.datarouter.storage.trace.databean;

import io.datarouter.model.key.entity.base.BaseEntityPartitioner;
import io.datarouter.util.HashMethods;

public class BaseTraceEntityPartitioner<EK extends BaseTraceEntityKey<EK>>
extends BaseEntityPartitioner<EK>{

	@Override
	public int getNumPartitions(){
		return 16;
	}

	@Override
	public int getPartition(EK ek){
		long hash = HashMethods.longDjbHash(ek.getTraceEntityId());
		return (int)(hash % getNumPartitions());
	}

}