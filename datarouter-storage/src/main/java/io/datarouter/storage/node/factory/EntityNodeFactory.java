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
package io.datarouter.storage.node.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.datarouter.inject.DatarouterInjector;
import io.datarouter.model.entity.Entity;
import io.datarouter.model.key.entity.EntityKey;
import io.datarouter.storage.client.ClientNodeFactory;
import io.datarouter.storage.client.ClientType;
import io.datarouter.storage.client.DatarouterClients;
import io.datarouter.storage.node.entity.EntityNode;
import io.datarouter.storage.node.entity.EntityNodeParams;
import io.datarouter.storage.router.Router;

@Singleton
public class EntityNodeFactory{

	@Inject
	private DatarouterClients clients;
	@Inject
	private NodeFactory nodeFactory;
	@Inject
	private DatarouterInjector injector;

	public <EK extends EntityKey<EK>,E extends Entity<EK>>
	EntityNode<EK,E> create(String clientName, Router router, EntityNodeParams<EK,E> params){
		ClientType<?> clientType = clients.getClientTypeInstance(clientName);
		Preconditions.checkNotNull(clientType, "clientType not found for clientName:" + clientName);
		ClientNodeFactory clientNodeFactory = injector.getInstance(clientType.getClientNodeFactoryClass());
		EntityNode<EK,E> entityNode = clientNodeFactory.createEntityNode(nodeFactory, router, params, clientName);
		return entityNode;
	}

}
