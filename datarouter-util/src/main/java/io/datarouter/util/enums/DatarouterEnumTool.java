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
package io.datarouter.util.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.datarouter.util.ComparableTool;
import io.datarouter.util.collection.CollectionTool;
import io.datarouter.util.lang.ObjectTool;
import io.datarouter.util.string.StringTool;

public class DatarouterEnumTool{

	/*--------------- comparator that compares the persistent values --------*/

	public static class IntegerEnumComparator<T extends IntegerEnum<T>> implements Comparator<T>{
		@Override
		public int compare(T valueA, T valueB){
			if(ObjectTool.bothNull(valueA, valueB)){
				return 0;
			}
			if(ObjectTool.isOneNullButNotTheOther(valueA, valueB)){
				return valueA == null ? -1 : 1;
			}
			return ComparableTool.nullFirstCompareTo(valueA.getPersistentInteger(), valueB.getPersistentInteger());
		}
	}

	public static <T extends IntegerEnum<T>> int compareIntegerEnums(T valueA, T valueB){
		if(ObjectTool.bothNull(valueA, valueB)){
			return 0;
		}
		if(ObjectTool.isOneNullButNotTheOther(valueA, valueB)){
			return valueA == null ? -1 : 1;
		}
		return ComparableTool.nullFirstCompareTo(valueA.getPersistentInteger(), valueB.getPersistentInteger());
	}

	public static <T extends StringEnum<T>> int compareStringEnums(T valueA, T valueB){
		if(ObjectTool.bothNull(valueA, valueB)){
			return 0;
		}
		if(ObjectTool.isOneNullButNotTheOther(valueA, valueB)){
			return valueA == null ? -1 : 1;
		}
		return ComparableTool.nullFirstCompareTo(valueA.getPersistentString(), valueB.getPersistentString());
	}

	/*------------------------- methods -------------------------------------*/

	public static <T extends IntegerEnum<T>> T getEnumFromInteger(T[] values, Integer value, T defaultEnum){
		if(value == null){
			return defaultEnum;
		}
		for(T type : values){
			if(type.getPersistentInteger().equals(value)){
				return type;
			}
		}
		return defaultEnum;
	}

	public static <T extends PersistentString> T getEnumFromString(T[] enumEntries, String value, T defaultEnum,
			boolean caseSensitive){
		for(T enumEntry : enumEntries){
			String persistentString = enumEntry.getPersistentString();
			if(persistentString == null){
				if(value == null){
					return enumEntry;
				}
				continue;
			}
			if(caseSensitive && persistentString.equals(value)
					|| !caseSensitive && persistentString.equalsIgnoreCase(value)){
				return enumEntry;
			}
		}
		return defaultEnum;
	}

	public static <T extends PersistentString> T getEnumFromString(T[] values, String value, T defaultEnum){
		return getEnumFromString(values, value, defaultEnum, true);
	}

	/*------------------------- multiple values -----------------------------*/

	public static <E extends StringEnum<E>> List<String> getPersistentStrings(Collection<E> enums){
		List<String> strings = new ArrayList<>();
		for(E stringEnum : CollectionTool.nullSafe(enums)){
			strings.add(stringEnum.getPersistentString());
		}
		return strings;
	}

	public static <E extends StringEnum<E>> List<E> fromPersistentStrings(E enumInstance,
			Collection<String> persistentStrings){
		List<E> enums = new ArrayList<>();
		for(String persistentString : CollectionTool.nullSafe(persistentStrings)){
			enums.add(enumInstance.fromPersistentString(persistentString));
		}
		return enums;
	}

	public static <E extends StringEnum<E>> Validated<List<E>> uniqueListFromCsvNames(E[] values, String csvNames,
			boolean defaultAll){
		Set<E> result = new LinkedHashSet<>();
		Validated<List<E>> validated = new Validated<>();

		if(StringTool.notEmpty(csvNames)){
			String[] types = csvNames.split("[,\\s]+");
			for(String name : types){
				if(StringTool.isEmpty(name)){
					continue;
				}
				E type = getEnumFromString(values, name, null, false);
				if(type == null){
					validated.addError(name);
				}else{
					result.add(type);
				}
			}
		}
		if(result.isEmpty()){
			if(defaultAll){
				for(E e : values){
					result.add(e);
				}
			}else{
				validated.addError("No value found");
			}
		}
		List<E> listResult = new ArrayList<>();
		listResult.addAll(result);
		validated.set(listResult);
		return validated;
	}
}
