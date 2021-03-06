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
package io.datarouter.client.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.datarouter.client.mysql.field.codec.factory.MysqlFieldCodecFactory;
import io.datarouter.client.mysql.node.MysqlNode;
import io.datarouter.client.mysql.op.read.MysqlGetOpExecutor;
import io.datarouter.client.mysql.util.MysqlPreparedStatementBuilder;
import io.datarouter.model.databean.Databean;
import io.datarouter.model.entity.Entity;
import io.datarouter.model.key.entity.EntityKey;
import io.datarouter.model.key.primary.EntityPrimaryKey;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.model.serialize.fielder.DatabeanFielder;
import io.datarouter.storage.Datarouter;
import io.datarouter.storage.client.DatarouterClients;
import io.datarouter.storage.client.imp.BaseClientNodeFactory;
import io.datarouter.storage.client.imp.WrappedNodeFactory;
import io.datarouter.storage.client.imp.WrappedSubEntityNodeFactory;
import io.datarouter.storage.node.DatarouterNodes;
import io.datarouter.storage.node.NodeParams;
import io.datarouter.storage.node.adapter.availability.PhysicalIndexedSortedMapStorageAvailabilityAdapterFactory;
import io.datarouter.storage.node.adapter.callsite.physical.PhysicalIndexedSortedMapStorageCallsiteAdapter;
import io.datarouter.storage.node.adapter.counter.physical.PhysicalIndexedSortedMapStorageCounterAdapter;
import io.datarouter.storage.node.entity.EntityNodeParams;
import io.datarouter.storage.node.op.combo.IndexedSortedMapStorage.PhysicalIndexedSortedMapStorageNode;

@Singleton
public class MysqlClientNodeFactory extends BaseClientNodeFactory{

	@Inject
	private Datarouter datarouter;
	@Inject
	private MysqlFieldCodecFactory fieldCodecFactory;
	@Inject
	private MysqlGetOpExecutor mysqlGetOpExecutor;
	@Inject
	private DatarouterClients datarouterClients;
	@Inject
	private DatarouterNodes datarouterNodes;
	@Inject
	private MysqlPreparedStatementBuilder mysqlPreparedStatementBuilder;
	@Inject
	private PhysicalIndexedSortedMapStorageAvailabilityAdapterFactory
			physicalIndexedSortedMapStorageAvailabilityAdapterFactory;

	public class MysqlWrappedNodeFactory<
			PK extends PrimaryKey<PK>,
			D extends Databean<PK,D>,
			F extends DatabeanFielder<PK,D>>
	extends WrappedNodeFactory<PK,D,F,PhysicalIndexedSortedMapStorageNode<PK,D,F>>{

		@Override
		public PhysicalIndexedSortedMapStorageNode<PK,D,F> createNode(NodeParams<PK,D,F> nodeParams){
			return new MysqlNode<>(nodeParams, fieldCodecFactory, datarouter, mysqlGetOpExecutor,
					datarouterClients, datarouterNodes, mysqlPreparedStatementBuilder);
		}

		@Override
		public List<UnaryOperator<PhysicalIndexedSortedMapStorageNode<PK,D,F>>> getAdapters(){
			return Arrays.asList(
					PhysicalIndexedSortedMapStorageCounterAdapter::new,
					physicalIndexedSortedMapStorageAvailabilityAdapterFactory::create,
					PhysicalIndexedSortedMapStorageCallsiteAdapter::new);
		}

	}

	public class MysqlWrappedSubEntityNodeFactory<
			EK extends EntityKey<EK>,
			E extends Entity<EK>,
			PK extends EntityPrimaryKey<EK,PK>,
			D extends Databean<PK,D>,
			F extends DatabeanFielder<PK,D>>
	extends WrappedSubEntityNodeFactory<EK,E,PK,D,F,PhysicalIndexedSortedMapStorageNode<PK,D,F>>{

		private final MysqlWrappedNodeFactory<PK,D,F> factory = new MysqlWrappedNodeFactory<>();

		@Override
		public PhysicalIndexedSortedMapStorageNode<PK,D,F> createSubEntityNode(
				EntityNodeParams<EK,E> entityNodeParams, NodeParams<PK,D,F> nodeParams){
			return factory.createNode(nodeParams);
		}

		@Override
		public List<UnaryOperator<PhysicalIndexedSortedMapStorageNode<PK,D,F>>> getAdapters(){
			return factory.getAdapters();
		}

	}

	@Override
	protected <PK extends PrimaryKey<PK>,
			D extends Databean<PK,D>,
			F extends DatabeanFielder<PK,D>>
	WrappedNodeFactory<PK,D,F,?> makeWrappedNodeFactory(){
		return new MysqlWrappedNodeFactory<>();
	}

	@Override
	protected <EK extends EntityKey<EK>,
			E extends Entity<EK>,
			PK extends EntityPrimaryKey<EK,PK>,
			D extends Databean<PK,D>,
			F extends DatabeanFielder<PK,D>>
	WrappedSubEntityNodeFactory<EK,E,PK,D,F,?> makeWrappedSubEntityNodeFactory(){
		return new MysqlWrappedSubEntityNodeFactory<>();
	}

}
