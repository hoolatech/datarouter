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
package io.datarouter.web.cache;

import java.util.Optional;

public abstract class BaseLookupCacheWrapper<K,V> implements LookupCacheGetters<K,V>{

	private final LookupCacheGetters<K,V> delegate;

	public BaseLookupCacheWrapper(LookupCacheGetters<K,V> delegate){
		this.delegate = delegate;
	}

	@Override
	public Optional<V> get(K key){
		return delegate.get(key);
	}

	@Override
	public V getOrThrow(K key){
		return delegate.getOrThrow(key);
	}

	@Override
	public boolean load(K key){
		return delegate.load(key);
	}

	@Override
	public boolean contains(K key){
		return delegate.contains(key);
	}

}
