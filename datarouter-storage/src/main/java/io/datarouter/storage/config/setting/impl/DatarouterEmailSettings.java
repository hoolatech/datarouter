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
package io.datarouter.storage.config.setting.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.datarouter.storage.setting.SettingFinder;
import io.datarouter.storage.setting.SettingNode;
import io.datarouter.storage.setting.cached.CachedSetting;

@Singleton
public class DatarouterEmailSettings extends SettingNode{

	public final CachedSetting<String> smtpHost;
	public final CachedSetting<Integer> smtpPort;
	public final CachedSetting<String> smtpUsername;
	public final CachedSetting<String> smtpPassword;

	@Inject
	public DatarouterEmailSettings(SettingFinder finder){
		super(finder, "datarouter.email.");

		smtpHost = registerString("smtpHost", "127.0.0.1");
		smtpPort = registerInteger("smtpPort", 25);
		smtpUsername = registerString("smtpUsername", "");
		smtpPassword = registerString("smtpPassword", "");
	}

}
