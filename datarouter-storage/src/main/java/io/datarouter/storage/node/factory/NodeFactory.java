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

import io.datarouter.inject.DatarouterInjector;
import io.datarouter.storage.client.DatarouterClients;
import io.datarouter.storage.config.setting.DatarouterSettings;
import io.datarouter.storage.setting.Setting;

@Singleton
public class NodeFactory extends BaseNodeFactory{

	private final DatarouterSettings datarouterSettings;

	@Inject
	private NodeFactory(DatarouterClients clients, DatarouterSettings datarouterSettings, DatarouterInjector injector){
		super(clients, injector);
		this.datarouterSettings = datarouterSettings;
	}

	@Override
	protected Setting<Boolean> getRecordCallsites(){
		return datarouterSettings.getRecordCallsites();
	}

}
