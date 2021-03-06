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
package io.datarouter.client.mysql.op.write;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

import io.datarouter.client.mysql.ddl.domain.MysqlTableOptions;
import io.datarouter.client.mysql.node.MysqlReaderNode;
import io.datarouter.client.mysql.op.BaseMysqlOp;
import io.datarouter.client.mysql.op.Isolation;
import io.datarouter.client.mysql.util.MysqlPreparedStatementBuilder;
import io.datarouter.client.mysql.util.MysqlTool;
import io.datarouter.model.databean.Databean;
import io.datarouter.model.key.primary.PrimaryKey;
import io.datarouter.model.serialize.fielder.DatabeanFielder;
import io.datarouter.storage.Datarouter;
import io.datarouter.storage.config.Config;
import io.datarouter.storage.node.type.physical.PhysicalNode;
import io.datarouter.util.collection.CollectionTool;
import io.datarouter.util.iterable.BatchingIterable;

public class MysqlDeleteByIndexOp<
		PK extends PrimaryKey<PK>,
		D extends Databean<PK,D>,
		F extends DatabeanFielder<PK,D>,
		IK extends PrimaryKey<IK>>
extends BaseMysqlOp<Long>{

	private final PhysicalNode<PK,D,F> node;
	private final MysqlPreparedStatementBuilder mysqlPreparedStatementBuilder;
	private final Config config;
	private final Collection<IK> entryKeys;

	public MysqlDeleteByIndexOp(Datarouter datarouter, PhysicalNode<PK,D,F> node,
			MysqlPreparedStatementBuilder mysqlPreparedStatementBuilder, Collection<IK> entryKeys, Config config){
		super(datarouter, node.getClientNames(), Isolation.DEFAULT, shouldAutoCommit(entryKeys));
		this.node = node;
		this.mysqlPreparedStatementBuilder = mysqlPreparedStatementBuilder;
		this.entryKeys = entryKeys;
		this.config = config;
	}

	@Override
	public Long runOnce(){
		Connection connection = getConnection(node.getFieldInfo().getClientId().getName());
		long numModified = 0;
		for(List<IK> batch : new BatchingIterable<>(entryKeys, MysqlReaderNode.DEFAULT_ITERATE_BATCH_SIZE)){
			PreparedStatement statement = mysqlPreparedStatementBuilder.deleteMulti(config, node.getFieldInfo()
					.getTableName(), batch, MysqlTableOptions.make(node.getFieldInfo()))
					.toPreparedStatement(connection);
			numModified += MysqlTool.update(statement);
		}
		return numModified;
	}


	private static <IK extends PrimaryKey<IK>> boolean shouldAutoCommit(Collection<IK> keys){
		return CollectionTool.size(keys) <= 1;
	}

}
