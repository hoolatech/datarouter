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
package io.datarouter.client.mysql.test.client.insert;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.datarouter.client.mysql.test.client.insert.PutOpTestBean.PutOpTestBeanFielder;
import io.datarouter.storage.Datarouter;
import io.datarouter.storage.client.ClientId;
import io.datarouter.storage.config.setting.DatarouterSettings;
import io.datarouter.storage.node.factory.NodeFactory;
import io.datarouter.storage.node.op.raw.MapStorage;
import io.datarouter.storage.router.BaseRouter;
import io.datarouter.storage.router.TestRouter;
import io.datarouter.storage.test.TestDatarouterProperties;

@Singleton
public class DatarouterPutOpTestRouter extends BaseRouter implements TestRouter{

	private final MapStorage<PutOpTestBeanKey,PutOpTestBean> putOptTest;

	@Inject
	public DatarouterPutOpTestRouter(TestDatarouterProperties datarouterProperties, Datarouter datarouter,
			DatarouterSettings datarouterSettings, NodeFactory nodeFactory, ClientId clientId){
		super(datarouter, datarouterProperties, "datarouterPutOpTest", nodeFactory, datarouterSettings);

		this.putOptTest = createAndRegister(clientId, PutOpTestBean::new, PutOpTestBeanFielder::new);
	}

	public MapStorage<PutOpTestBeanKey,PutOpTestBean> putOptTest(){
		return putOptTest;
	}

}