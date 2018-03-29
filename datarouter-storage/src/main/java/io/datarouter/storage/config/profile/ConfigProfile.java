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
package io.datarouter.storage.config.profile;

import java.util.function.Supplier;

/**
 * Standard config profiles.  Applications can add a similar enum with more.
 */
public enum ConfigProfile implements Supplier<DatarouterConfigProfile>{

	DEVELOPMENT("development"),
	STAGING("staging"),
	PRODUCTION("production");

	private final DatarouterConfigProfile profile;

	private ConfigProfile(String profileName){
		this.profile = new DatarouterConfigProfile(profileName);
	}

	@Override
	public DatarouterConfigProfile get(){
		return profile;
	}

}