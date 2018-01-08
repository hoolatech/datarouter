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
package io.datarouter.storage.node.type.index.base;

import io.datarouter.model.databean.Databean;
import io.datarouter.model.index.IndexEntry;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.model.serialize.fielder.DatabeanFielder;
import io.datarouter.storage.node.NodeParams;
import io.datarouter.storage.node.op.combo.IndexedMapStorage;
import io.datarouter.storage.node.type.index.ManagedNode;
import io.datarouter.storage.serialize.fieldcache.DatabeanFieldInfo;

public abstract class BaseManagedNode<
		PK extends PrimaryKey<PK>,
		D extends Databean<PK,D>,
		IK extends PrimaryKey<IK>,
		IE extends IndexEntry<IK, IE, PK, D>,
		IF extends DatabeanFielder<IK,IE>>
implements ManagedNode<PK,D,IK,IE,IF>{

	private String name;
	protected DatabeanFieldInfo<IK, IE, IF> fieldInfo;
	protected IndexedMapStorage<PK, D> node;

	public BaseManagedNode(IndexedMapStorage<PK, D> node, NodeParams<IK, IE, IF> params, String name){
		this.node = node;
		this.name = name;
		this.fieldInfo = new DatabeanFieldInfo<>(params);
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public DatabeanFieldInfo<IK, IE, IF> getFieldInfo(){
		return fieldInfo;
	}

}
