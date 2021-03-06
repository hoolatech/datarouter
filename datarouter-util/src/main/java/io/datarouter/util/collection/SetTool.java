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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.datarouter.util.array.ArrayTool;

public class SetTool{

	public static <T> Set<T> concatenate(Set<T> set, T element){
		HashSet<T> newSet = new HashSet<>(set);
		newSet.add(element);
		return newSet;
	}

	public static <T> Set<T> wrap(T element){
		Set<T> set = new HashSet<>();
		if(element != null){
			set.add(element);
		}
		return set;
	}

	public static <T> Set<T> nullsafe(Set<T> in){
		return in == null ? Collections.emptySet() : in;
	}

	@SafeVarargs
	public static <T> Set<T> union(Collection<T>... operands){
		return unionWithSupplier(HashSet<T>::new, operands);
	}

	@SafeVarargs
	public static <T,S extends Set<T>> S unionWithSupplier(Supplier<S> setSupplier, Collection<T>... operands){
		S union = setSupplier.get();
		if(ArrayTool.notEmpty(operands)){
			Arrays.stream(operands).forEach(union::addAll);
		}
		return union;
	}

	private static <T> SortedSet<T> nullSafeTreeSet(SortedSet<T> in){
		if(in == null){
			return new TreeSet<>();
		}
		return in;
	}

	public static <T> SortedSet<T> nullSafeSortedAddAll(SortedSet<T> set, Collection<T> newItems){
		set = nullSafeTreeSet(set);
		set.addAll(CollectionTool.nullSafe(newItems));
		return set;
	}

	@SafeVarargs
	public static <E extends Enum<E>> Set<E> unmodifiableEnumSetOf(E... enums){
		if(enums == null || enums.length == 0){
			return Collections.emptySet();
		}
		E first = enums[0];
		EnumSet<E> result = EnumSet.noneOf(first.getDeclaringClass());
		for(E enum1 : enums){
			result.add(enum1);
		}
		return Collections.unmodifiableSet(result);
	}

	public static class SetToolTests{

		@Test
		public void testNullSafeAddAllWithEmptySet(){
			SortedSet<String> set = new TreeSet<>();
			set.add("b");
			Set<String> toAdd = new HashSet<>();
			toAdd.add("a");
			set = nullSafeSortedAddAll(set, toAdd);
			set.add("c");
			Assert.assertEquals(new String[]{"a", "b", "c"}, set.toArray());
		}

		@Test
		public void testNullSafeAddAllWithNullSet(){
			SortedSet<String> set = null;
			Set<String> toAdd = new HashSet<>();
			toAdd.add("a");
			set = nullSafeSortedAddAll(set, toAdd);
			set.add("c");
			Assert.assertEquals(new String[]{"a", "c"}, set.toArray());
		}
	}

}
