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
package io.datarouter.storage.op.scan.queue.group;

import io.datarouter.model.databean.Databean;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.storage.config.Config;
import io.datarouter.storage.node.op.raw.read.GroupQueueStorageReader;
import io.datarouter.storage.queue.GroupQueueMessage;
import io.datarouter.util.iterable.scanner.batch.BaseBatchBackedScanner;

public class PeekGroupUntilEmptyQueueStorageScanner<PK extends PrimaryKey<PK>,D extends Databean<PK,D>>
extends BaseBatchBackedScanner<GroupQueueMessage<PK,D>,GroupQueueMessage<PK,D>>{

	private final GroupQueueStorageReader<PK,D> queueStorageReader;
	private final Config config;

	public PeekGroupUntilEmptyQueueStorageScanner(GroupQueueStorageReader<PK,D> queueStorageReader, Config config){
		this.queueStorageReader = queueStorageReader;
		this.config = config;
		this.currentBatchIndex = -1;
	}

	@Override
	protected void loadNextBatch(){
		currentBatchIndex = 0;
		currentBatch = queueStorageReader.peekMulti(config);
		noMoreBatches = currentBatch.size() == 0;
	}

	@Override
	protected void setCurrentFromResult(GroupQueueMessage<PK,D> result){
		current = result;
	}

}
