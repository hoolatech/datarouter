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
package io.datarouter.instrumentation.trace;

public class TraceSpanDto{

	public final Long traceId;
	public final Long threadId;
	public final Integer sequence;
	public final Integer parentSequence;
	public final String name;
	public final String info;
	public final Long created;
	public final Long duration;
	public final Long durationNano;

	public TraceSpanDto(Long traceId, Long threadId, Integer sequence, Integer parentSequence, String name, String info,
			Long created, Long duration, Long durationNano){
		this.traceId = traceId;
		this.threadId = threadId;
		this.sequence = sequence;
		this.parentSequence = parentSequence;
		this.name = name;
		this.info = info;
		this.created = created;
		this.duration = duration;
		this.durationNano = durationNano;
	}

}
