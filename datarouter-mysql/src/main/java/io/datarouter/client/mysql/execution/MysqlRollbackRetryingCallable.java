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
package io.datarouter.client.mysql.execution;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.datarouter.storage.op.executor.impl.SessionExecutorPleaseRetryException;
import io.datarouter.util.concurrent.ThreadTool;
import io.datarouter.util.retry.Retryable;

public class MysqlRollbackRetryingCallable<T> implements Retryable<T>{
	private static final Logger logger = LoggerFactory.getLogger(MysqlRollbackRetryingCallable.class);

	private final SessionExecutor<T> callable;
	private final int numAttempts;
	private final long initialBackoffMs;

	//TODO accept a callableSupplier for mutable ops
	public MysqlRollbackRetryingCallable(SessionExecutor<T> callable, int numAttempts, long initialBackoffMs){
		this.callable = callable;
		this.numAttempts = numAttempts;
		this.initialBackoffMs = initialBackoffMs;
	}


	@Override
	public T call(){
		long backoffMs = initialBackoffMs;
		for(int attemptNum = 1; attemptNum <= numAttempts; ++attemptNum){
			try{
				return callable.call();
			}catch(SessionExecutorPleaseRetryException e){//fragile; SessionExecutor must throw this exact exception
				if(attemptNum < numAttempts){
					logger.warn("rollback on attempt {}/{}, sleeping {}ms", attemptNum, numAttempts, backoffMs, e);
					ThreadTool.sleep(backoffMs);
				}else{
					logger.error("rollback on final attempt {}", attemptNum, e);
					Throwable rollbackCause = e.getCause();
					throw new RuntimeException(rollbackCause);
				}
			}
			backoffMs = backoffMs * 2 + ThreadLocalRandom.current().nextLong(0, initialBackoffMs);
		}
		throw new RuntimeException("shouldn't get here.  for-loop has bug?");
	}

}