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
package io.datarouter.storage.test.node.basic.map;

import io.datarouter.storage.Datarouter;
import io.datarouter.storage.client.ClientId;
import io.datarouter.storage.config.setting.DatarouterSettings;
import io.datarouter.storage.node.entity.EntityNodeParams;
import io.datarouter.storage.node.factory.EntityNodeFactory;
import io.datarouter.storage.node.factory.NodeFactory;
import io.datarouter.storage.node.op.raw.MapStorage;
import io.datarouter.storage.router.BaseRouter;
import io.datarouter.storage.router.TestRouter;
import io.datarouter.storage.test.TestDatarouterProperties;
import io.datarouter.storage.test.node.basic.map.databean.MapStorageBean;
import io.datarouter.storage.test.node.basic.map.databean.MapStorageBean.MapStorageBeanFielder;
import io.datarouter.storage.test.node.basic.map.databean.MapStorageBeanEntity;
import io.datarouter.storage.test.node.basic.map.databean.MapStorageBeanEntityKey;
import io.datarouter.storage.test.node.basic.map.databean.MapStorageBeanKey;

public class MapStorageTestRouter extends BaseRouter implements TestRouter{

	private static final String NAME = "MapStorageTestRouter";
	private static final int VERSION_mapStorageTestRouter = 1;

	public final MapStorage<MapStorageBeanKey,MapStorageBean> mapStorageNode;

	public MapStorageTestRouter(TestDatarouterProperties datarouterProperties, Datarouter datarouter,
			NodeFactory nodeFactory, ClientId clientId, DatarouterSettings datarouterSettings, boolean entity,
			EntityNodeFactory entityNodeFactory,
			EntityNodeParams<MapStorageBeanEntityKey,MapStorageBeanEntity> entityNodeParams){
		super(datarouter, datarouterProperties.getDatarouterTestFileLocation(), NAME, nodeFactory, datarouterSettings);

		if(entity){
			mapStorageNode = new MapStorageEntityNode(entityNodeFactory, nodeFactory, this, clientId, entityNodeParams)
					.mapStorageNode;
		}else{
			mapStorageNode = create(clientId, MapStorageBean::new, MapStorageBeanFielder::new)
				.withSchemaVersion(VERSION_mapStorageTestRouter)
				.buildAndRegister();
		}
	}

}