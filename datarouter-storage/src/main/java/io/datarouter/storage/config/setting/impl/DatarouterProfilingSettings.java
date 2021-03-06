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

import io.datarouter.storage.config.profile.ConfigProfile;
import io.datarouter.storage.setting.SettingFinder;
import io.datarouter.storage.setting.SettingNode;
import io.datarouter.storage.setting.cached.CachedSetting;

@Singleton
public class DatarouterProfilingSettings extends SettingNode{

	public final CachedSetting<Boolean> saveCounts;
	public final CachedSetting<Boolean> bufferCountsInSqs;//currently need to restart webapp after changing
	public final CachedSetting<Boolean> drainSqsCounts;
	public final CachedSetting<Boolean> runMetricsAggregationJob;
	public final CachedSetting<Boolean> runServerMonitoringJob;
	public final CachedSetting<Boolean> runLatencyMonitoringJob;
	public final CachedSetting<Boolean> saveExecutorsMetrics;
	public final CachedSetting<Boolean> saveHttpClientsMetrics;
	public final CachedSetting<Boolean> runAvailabilitySwitchJob;
	public final CachedSetting<Boolean> saveTomcatPoolMetrics;

	@Inject
	public DatarouterProfilingSettings(SettingFinder finder){
		super(finder, "datarouter.profiling.");

		saveCounts = registerBoolean("saveCounts", true);
		bufferCountsInSqs = registerBoolean("bufferCountsInSqs", false);
		drainSqsCounts = registerBooleans("drainSqsCounts", defaultTo(false))
				.setProfileDefault(ConfigProfile.DEVELOPMENT, true);
		runMetricsAggregationJob = registerBoolean("runMetricsAggregationJob", false);
		runServerMonitoringJob = registerBoolean("runServerMonitoringJob", true);
		runLatencyMonitoringJob = registerBoolean("runLatencyMonitoringJob", false);
		saveExecutorsMetrics = registerBoolean("saveExecutorsMetrics", false);
		saveHttpClientsMetrics = registerBoolean("saveHttpClientsMetrics", false);
		runAvailabilitySwitchJob = registerBoolean("runAvailabilitySwitchJob", false);
		saveTomcatPoolMetrics = registerBoolean("saveTomcatPoolMetrics", false);
	}

}
