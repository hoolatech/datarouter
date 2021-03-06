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
package io.datarouter.util.collection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.datarouter.util.StreamTool;
import io.datarouter.util.string.StringTool;

public class MapTool{

	/*------------------------- size ----------------------------------------*/

	public static <K,V> boolean isEmpty(Map<K,V> map){
		if(map == null){
			return true;
		}
		return map.isEmpty();
	}

	public static <K,V> boolean notEmpty(Map<K,V> map){
		return !isEmpty(map);
	}

	public static <K,V> int size(Map<K,V> map){
		return map == null ? 0 : map.size();
	}

	/*------------------------- null safe -----------------------------------*/

	public static <K,V> Map<K,V> nullSafe(Map<K,V> in){
		if(in == null){
			return new HashMap<>();
		}
		return in;
	}

	/*------------------------- counting ------------------------------------*/

	//convenience method
	public static <T> Long increment(Map<T,Long> map, T key){
		return increment(map, key, 1L);
	}


	//1 level: Map<T,Long>
	private static <T> Long increment(Map<T,Long> map, T key, Long delta){
		if(!map.containsKey(key)){
			map.put(key, delta);
			return delta;
		}
		map.put(key, map.get(key) + delta);
		return map.get(key);
	}

	// 2 levels: Map<T,Map<U,Long>>
	public static <T,U> Long increment(Map<T,Map<U,Long>> map, T element, U subElement, Long delta){
		if(!map.containsKey(element)){
			map.put(element, new TreeMap<U,Long>());
		}
		Map<U,Long> subMap = map.get(element);
		if(!subMap.containsKey(subElement)){
			subMap.put(subElement, 0L);
		}
		subMap.put(subElement, subMap.get(subElement) + delta);
		return subMap.get(subElement);
	}

	/*------------------------- filtering -----------------------------------*/

	public static <K,V> K getFirstKeyWhereValueEquals(Map<K,V> map, V value){
		for(Entry<K,V> entry : nullSafe(map).entrySet()){
			if(Objects.equals(value, entry.getValue())){
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Build a map from the string with the format "
	 * <code>key[keyValueSeparator]value[entrySeperator]key[keyValueSeparator]value...</code>"
	 * @param string The input {@link String}
	 * @param entrySeperator The separator between tow entries
	 * @param keyValueSeparator The separator between the key and the value
	 * @return a {@link Map}
	 */
	public static Map<String,String> getMapFromString(String string, String entrySeperator, String keyValueSeparator){
		Map<String,String> map = new TreeMap<>();
		if(StringTool.isEmpty(string)){
			return map;
		}
		String[] entries = string.split(entrySeperator);
		String[] keyVal;
		for(String entry : entries){
			if(StringTool.notEmpty(entry)){
				keyVal = entry.split(keyValueSeparator);
				map.put(keyVal[0], keyVal.length > 1 ? keyVal[1] : null);
			}
		}
		return map;
	}


	/*------------------------- transform -----------------------------------*/

	/**
	 * Transforms values into a map with keys produced by keyMapper. Exammple:
	 * <pre>getBy(employees, ssnGetter) -&gt; Map&lt;SSN, Employee&gt;</pre>
	 */
	public static <K,V> Map<K,V> getBy(Iterable<V> values, Function<V,K> keyMapper){
		return StreamTool.stream(values).collect(CollectorTool.toMap(keyMapper));
	}

	/**
	 * Transforms values into a map with keys and values produced by keyMapper and valueMapper. Example:
	 * <pre>getBy(employees, ssnGetter, phoneGetter) -&gt; Map&lt;SSN, Phone&gt;</pre>
	 */
	public static <T,K,V> Map<K,V> getBy(Iterable<T> elements, Function<T,K> keyMapper, Function<T,V> valueMapper){
		return StreamTool.stream(elements).collect(CollectorTool.toMap(keyMapper, valueMapper));
	}

	/**
	 * same as {@link #getBy(Iterable, Function, Function)} but allows null values and overwrites duplicate keys
	 */
	public static <T,K,V> Map<K,V> getByNullable(Iterable<T> elements, Function<T,K> keyMapper,
			Function<T,V> valueMapper){
		Map<K,V> map = new LinkedHashMap<>();
		for(T element : elements){
			map.put(keyMapper.apply(element), valueMapper.apply(element));
		}
		return map;
	}

	/*------------------------- tests ----------------------------------------*/

	public static class MapToolTests{

		@Test
		public void getMapFromString(){
			String string = "key1: val1;key2: val2";
			Map<String,String> res = MapTool.getMapFromString(string, ";", ": ");
			Assert.assertEquals(res.size(), 2);
			Assert.assertEquals(res.get("key2"), "val2");
		}

		@Test
		public void testGetByKeyMapper(){
			List<String> strings = Arrays.asList("aaa", "b", "ca", "eeee", "ca");
			AtomicLong counterA = new AtomicLong(0);
			Function<String,String> valueMapper = str -> {
				if(str.contains("a")){
					return counterA.incrementAndGet() + "a";
				}
				if(str.contains("b")){
					return "b";
				}
				return str;
			};
			Map<Integer,String> containsByLength = MapTool.getBy(strings, String::length, valueMapper);
			Assert.assertEquals(containsByLength.keySet(), Arrays.asList(3, 1, 2, 4));
			Assert.assertEquals(containsByLength.values(), Arrays.asList("1a", "b", "3a", "eeee"));
		}

		@Test
		public void testGetByNullableKeyValueMapper(){
			List<String> strings = Arrays.asList("aaa", "b", "ca", "eeee", "ca");
			AtomicLong counterA = new AtomicLong(0);
			Function<String,String> valueMapper = str -> {
				if(str.contains("a")){
					return counterA.incrementAndGet() + "a";
				}
				if(str.contains("b")){
					return "b";
				}
				return null;
			};
			Map<Integer,String> containsByLength = MapTool.getByNullable(strings, String::length, valueMapper);
			Assert.assertEquals(containsByLength.keySet(), Arrays.asList(3, 1, 2, 4));
			Assert.assertEquals(containsByLength.values(), Arrays.asList("1a", "b", "3a", null));
		}
	}

}
