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
package io.datarouter.storage.node.entity;

import java.util.Map;

import io.datarouter.model.entity.Entity;
import io.datarouter.model.key.entity.EntityKey;
import io.datarouter.storage.client.Client;

public interface PhysicalEntityNode<
		EK extends EntityKey<EK>,
		E extends Entity<EK>>
extends EntityNode<EK,E>{

	String getClientName();
	String getTableName();

	Map<String,? extends SubEntitySortedMapStorageReaderNode<EK,?,?,?>> getNodeByQualifierPrefix();

	Client getClient();

}
