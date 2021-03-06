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
package io.datarouter.storage.node.type.index;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.datarouter.model.databean.Databean;
import io.datarouter.model.index.unique.UniqueIndexEntry;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.storage.config.Config;
import io.datarouter.storage.node.op.combo.SortedMapStorage;
import io.datarouter.storage.node.op.raw.MapStorage;
import io.datarouter.storage.op.scan.ManagedIndexIndexToDatabeanScanner;
import io.datarouter.util.collection.CollectionTool;
import io.datarouter.util.iterable.IterableTool;
import io.datarouter.util.iterable.scanner.iterable.SingleUseScannerIterable;
import io.datarouter.util.tuple.Range;

public class ManualUniqueIndexNode<
		PK extends PrimaryKey<PK>,
		D extends Databean<PK,D>,
		IK extends PrimaryKey<IK>,
		IE extends UniqueIndexEntry<IK,IE,PK,D>>
implements UniqueIndexNode<PK,D,IK,IE>{

	private final MapStorage<PK,D> mainNode;
	private final SortedMapStorage<IK,IE> indexNode;

	public ManualUniqueIndexNode(MapStorage<PK,D> mainNode, SortedMapStorage<IK,IE> indexNode){
		this.mainNode = mainNode;
		this.indexNode = indexNode;
	}

	//TODO should i be passing config options around blindly?


	@Override
	public D lookupUnique(IK uniqueKey, Config config){
		if(uniqueKey == null){
			return null;
		}
		IE indexEntry = indexNode.get(uniqueKey, config);
		if(indexEntry == null){
			return null;
		}
		PK primaryKey = indexEntry.getTargetKey();
		D databean = mainNode.get(primaryKey, config);
		return databean;
	}


	@Override
	public List<D> lookupMultiUnique(Collection<IK> uniqueKeys, Config config){
		if(CollectionTool.isEmpty(uniqueKeys)){
			return new LinkedList<>();
		}
		List<IE> indexEntries = indexNode.getMulti(uniqueKeys, config);
		List<PK> primaryKeys = IterableTool.map(indexEntries, IE::getTargetKey);
		List<D> databeans = mainNode.getMulti(primaryKeys, config);
		return databeans;
	}


	@Override
	public void deleteUnique(IK indexKey, Config config){
		if(indexKey == null){
			return;
		}
		IE indexEntry = indexNode.get(indexKey, config);
		if(indexEntry == null){
			return;
		}
		PK primaryKey = indexEntry.getTargetKey();
		indexNode.delete(indexKey, config);
		mainNode.delete(primaryKey, config);
	}


	@Override
	public void deleteMultiUnique(Collection<IK> uniqueKeys, Config config){
		if(CollectionTool.isEmpty(uniqueKeys)){
			return;
		}
		List<IE> indexEntries = indexNode.getMulti(uniqueKeys, config);
		List<PK> primaryKeys = IterableTool.map(indexEntries, IE::getTargetKey);
		indexNode.deleteMulti(uniqueKeys, config);
		mainNode.deleteMulti(primaryKeys, config);
	}

	@Override
	public Iterable<IE> scanMulti(Collection<Range<IK>> ranges, Config config){
		return indexNode.scanMulti(ranges, config);
	}

	@Override
	public Iterable<IK> scanKeysMulti(Collection<Range<IK>> ranges, Config config){
		return indexNode.scanKeysMulti(ranges, config);
	}

	@Override
	public SingleUseScannerIterable<D> scanDatabeansMulti(Collection<Range<IK>> ranges, Config config){
		return new SingleUseScannerIterable<>(new ManagedIndexIndexToDatabeanScanner<>(mainNode, scanMulti(ranges,
				config), config));
	}

	@Override
	public IE get(IK uniqueKey, Config config){
		return indexNode.get(uniqueKey, config);
	}

	@Override
	public List<IE> getMulti(Collection<IK> uniqueKeys, Config config){
		return indexNode.getMulti(uniqueKeys, config);
	}

}
