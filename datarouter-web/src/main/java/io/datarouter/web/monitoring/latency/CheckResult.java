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
package io.datarouter.web.monitoring.latency;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import io.datarouter.util.duration.Duration;

public class CheckResult{

	private final long dateMs;
	private final Optional<String> failureMessage;
	private final Optional<Duration> latency;

	private CheckResult(long dateMs, Optional<String> failureMessage, Optional<Duration> latency){
		this.dateMs = dateMs;
		this.failureMessage = failureMessage;
		this.latency = latency;
	}

	public static CheckResult newSuccess(long dateMs, Duration latency){
		return new CheckResult(dateMs, Optional.empty(), Optional.of(latency));
	}

	public static CheckResult newFailure(long dateMs, String failureMessage){
		return new CheckResult(dateMs, Optional.of(StringUtils.abbreviate(failureMessage, 200)), Optional.empty());
	}

	public String getCssClass(){
		if(failureMessage.isPresent()){
			return "black";
		}
		if(latency.get().to(TimeUnit.MILLISECONDS) < 200){
			return "green";
		}
		if(latency.get().to(TimeUnit.MILLISECONDS) < 600){
			return "orange";
		}
		return "red";

	}

	public Optional<Duration> getLatency(){
		return latency;
	}

	public String getLatencyString(){
		return latency
				.map(duration -> duration.toString(TimeUnit.MILLISECONDS))
				.orElse("");
	}

	public boolean isFailure(){
		return failureMessage.isPresent();
	}

	public long getDateMs(){
		return dateMs;
	}

	@Override
	public String toString(){
		if(failureMessage.isPresent()){
			return "Failure " + new Duration(System.currentTimeMillis() - dateMs, TimeUnit.MILLISECONDS) + " ago: "
					+ failureMessage.get();
		}
		return getLatencyString() + " (" + new Duration(System.currentTimeMillis() - dateMs, TimeUnit.MILLISECONDS)
				+ " ago)";
	}

}
