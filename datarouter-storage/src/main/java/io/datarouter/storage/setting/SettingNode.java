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
package io.datarouter.storage.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Preconditions;

import io.datarouter.storage.setting.cached.CachedSetting;
import io.datarouter.storage.setting.cached.impl.BooleanCachedSetting;
import io.datarouter.storage.setting.cached.impl.CommaSeparatedStringCachedSetting;
import io.datarouter.storage.setting.cached.impl.DoubleCachedSetting;
import io.datarouter.storage.setting.cached.impl.DurationCachedSetting;
import io.datarouter.storage.setting.cached.impl.IntegerCachedSetting;
import io.datarouter.storage.setting.cached.impl.LongCachedSetting;
import io.datarouter.storage.setting.cached.impl.StringCachedSetting;
import io.datarouter.util.duration.Duration;

public abstract class SettingNode{

	private final String parentName;
	private final String name;
	private final SortedMap<String,SettingNode> children;

	private final SortedMap<String,CachedSetting<?>> settings;
	protected final SettingFinder finder;

	/*---------- construct ----------*/

	public SettingNode(SettingFinder finder, String name){
		this(finder, name, findParentName(name));
	}

	private SettingNode(SettingFinder finder, String name, String parentName){
		this.name = name;
		this.parentName = parentName;
		this.children = new ConcurrentSkipListMap<>();
		this.settings = new ConcurrentSkipListMap<>();
		this.finder = finder;
	}

	// "a.b.c." -> "a.b."
	public static String findParentName(String name){
		Preconditions.checkArgument(name.endsWith("."), "invalid name %s", name);
		name = name.substring(0, name.lastIndexOf("."));
		if(!name.contains(".")){
			return "";
		}
		return name.substring(0, name.lastIndexOf(".")) + ".";
	}

	/*---------- methods ----------*/

	protected <N extends SettingNode> N registerChild(N child){
		children.put(child.getName(), child);
		return child;
	}

	protected <S extends CachedSetting<?>> S register(S setting){
		settings.put(setting.getName(), setting);
		return setting;
	}

	public SettingNode getNodeByName(String nameParam){
		return getNodeByNameRecursively(nameParam, false);
	}

	public SettingNode getMostRecentAncestorNodeByName(String nameParam){
		return getNodeByNameRecursively(nameParam, true);
	}

	private SettingNode getNodeByNameRecursively(String nameParam, boolean stopAtMostRecentNonNullAncestor){
		if(getName().equals(nameParam)){
			return this;
		}
		if(getChildren().containsKey(nameParam) && stopAtMostRecentNonNullAncestor){
			return getChildren().get(nameParam);
		}
		if(getSettings().containsKey(removeTrailingPeriod(nameParam)) && stopAtMostRecentNonNullAncestor){
			return this;
		}
		String nextChildShortName = nameParam.substring(getName().length());
		int index = nextChildShortName.indexOf('.');
		String nextChildPath = getName() + nextChildShortName.substring(0, index + 1);
		if(getChildren().containsKey(nextChildPath)){
			SettingNode ancestor = getChildren().get(nextChildPath);
			SettingNode moreRecentAncestor = ancestor.getNodeByNameRecursively(nameParam,
					stopAtMostRecentNonNullAncestor);
			if(stopAtMostRecentNonNullAncestor && moreRecentAncestor == null){
				return ancestor;
			}
			return moreRecentAncestor;
		}
		return null;
	}

	private String removeTrailingPeriod(String name){
		if(name.endsWith(".")){
			return name.substring(0, name.length() - 1);
		}
		return name;
	}

	public List<SettingNode> getDescendanceByName(String nameParam){
		ArrayList<SettingNode> list = new ArrayList<>();
		if(getName().equals(nameParam)){
			list.add(this);
			return list;
		}
		String nextChildShortName = nameParam.substring(getName().length());
		int index = nextChildShortName.indexOf('.');
		String nextChildPath = getName() + nextChildShortName.substring(0, index + 1);
		if(getChildren().containsKey(nextChildPath)){
			list.add(this);
			list.addAll(getChildren().get(nextChildPath).getDescendanceByName(nameParam));
		}
		return list;
	}

	public Setting<?> getDescendantSettingByName(String settingNameParam){
		if(getSettings().containsKey(settingNameParam)){
			return getSettings().get(settingNameParam);
		}
		String nextChildShortName = settingNameParam.substring(getName().length());
		int index = nextChildShortName.indexOf('.');
		String nextChildPath = getName() + nextChildShortName.substring(0, index + 1);
		if(getChildren().containsKey(nextChildPath)){
			return getChildren().get(nextChildPath).getDescendantSettingByName(settingNameParam);
		}
		return null;
	}

	public List<SettingNode> getListChildren(){
		ArrayList<SettingNode> list = new ArrayList<>();
		for(String childName : children.keySet()){
			list.add(children.get(childName));
		}
		return list;
	}

	public ArrayList<CachedSetting<?>> getListSettings(){
		return new ArrayList<>(settings.values());
	}

	public String getShortName(){
		String shortName = getName().substring(getParentName().length());
		return shortName.substring(0, shortName.length() - 1);
	}

	/*----------- register ---------------*/

	public StringCachedSetting registerString(String name, String defaultValue){
		return registerStrings(name, defaultTo(defaultValue));
	}

	public CommaSeparatedStringCachedSetting registerCommaSeparatedString(String name, Set<String> defaultValue){
		return registerCommaSeparatedStrings(name, defaultTo(defaultValue));
	}

	public BooleanCachedSetting registerBoolean(String name, Boolean defaultValue){
		return registerBooleans(name, defaultTo(defaultValue));
	}

	public IntegerCachedSetting registerInteger(String name, Integer defaultValue){
		return registerIntegers(name, defaultTo(defaultValue));
	}

	public LongCachedSetting registerLong(String name, Long defaultValue){
		return registerLongs(name, defaultTo(defaultValue));
	}

	public DoubleCachedSetting registerDouble(String name, Double defaultValue){
		return registerDoubles(name, defaultTo(defaultValue));
	}

	public DurationCachedSetting registerDuration(String name, Duration defaultValue){
		return registerDurations(name, defaultTo(defaultValue));
	}

	/*----------- register with defaults ---------------*/

	public static <T> DefaultSettingValue<T> defaultTo(T globalDefault){
		return new DefaultSettingValue<>(globalDefault);
	}

	public StringCachedSetting registerStrings(String name, DefaultSettingValue<String> defaultValue){
		return register(new StringCachedSetting(finder, getName() + name, defaultValue));
	}

	public CommaSeparatedStringCachedSetting registerCommaSeparatedStrings(String name,
			DefaultSettingValue<Set<String>> defaultValue){
		return register(new CommaSeparatedStringCachedSetting(finder, getName() + name, defaultValue));
	}

	public BooleanCachedSetting registerBooleans(String name, DefaultSettingValue<Boolean> defaultValue){
		return register(new BooleanCachedSetting(finder, getName() + name, defaultValue));
	}

	public IntegerCachedSetting registerIntegers(String name, DefaultSettingValue<Integer> defaultValue){
		return register(new IntegerCachedSetting(finder, getName() + name, defaultValue));
	}

	public LongCachedSetting registerLongs(String name, DefaultSettingValue<Long> defaultValue){
		return register(new LongCachedSetting(finder, getName() + name, defaultValue));
	}

	public DoubleCachedSetting registerDoubles(String name, DefaultSettingValue<Double> defaultValue){
		return register(new DoubleCachedSetting(finder, getName() + name, defaultValue));
	}

	public DurationCachedSetting registerDurations(String name, DefaultSettingValue<Duration> defaultValue){
		return register(new DurationCachedSetting(finder, getName() + name, defaultValue));
	}

	/*---------- get/set ----------*/

	public String getName(){
		return name;
	}

	public String getParentName(){
		return parentName;
	}

	public SortedMap<String,CachedSetting<?>> getSettings(){
		return settings;
	}

	public SortedMap<String,SettingNode> getChildren(){
		return children;
	}

	public static class SettingNodeTests{

		@Test
		public void testInferParentName(){
			SettingNode node = new SettingNode(null, "services.myapp.mysetting."){};
			Assert.assertEquals(node.getParentName(), "services.myapp.");

			node = new SettingNode(null, "services.myapp."){};
			Assert.assertEquals(node.getParentName(), "services.");

			node = new SettingNode(null, "services."){};
			Assert.assertEquals(node.getParentName(), "");
		}

	}

}
