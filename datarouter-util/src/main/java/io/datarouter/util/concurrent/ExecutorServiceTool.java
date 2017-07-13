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
package io.datarouter.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTool{

	public static void shutdown(ExecutorService exec){
		exec.shutdown();
		awaitTerminationForever(exec);
	}

	public static void awaitTerminationForever(ExecutorService exec){
		awaitTermination(exec, Long.MAX_VALUE, TimeUnit.DAYS);
	}

	public static void awaitTermination(ExecutorService exec, long timeout, TimeUnit unit){
		try{
			exec.awaitTermination(timeout, unit);
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
	}

}
