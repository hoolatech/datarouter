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
package io.datarouter.web.handler.mav;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import io.datarouter.storage.servertype.ServerTypeDetector;
import io.datarouter.web.user.session.CurrentUserSessionInfo;
import io.datarouter.web.user.session.service.RoleManager;

@Singleton
public class DatarouterMavPropertiesFactoryConfig implements MavPropertiesFactoryConfig{

	@Inject
	private RoleManager roleManager;
	@Inject
	private CurrentUserSessionInfo currentUserSessionInfo;
	@Inject
	private ServerTypeDetector serverTypeDetector;

	@Override
	public boolean getIsProduction(){
		return serverTypeDetector.mightBeProduction();
	}

	@Override
	public boolean getIsAdmin(HttpServletRequest request){
		return currentUserSessionInfo.getRoles(request).stream()
				.anyMatch(roleManager::isAdmin);
	}

}
