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
package io.datarouter.storage.test.node.basic.sorted;

import java.util.Arrays;
import java.util.List;

import io.datarouter.model.field.Field;
import io.datarouter.model.field.imp.StringField;
import io.datarouter.model.field.imp.StringFieldKey;
import io.datarouter.model.key.entity.base.BaseEntityKey;
import io.datarouter.model.key.entity.base.BaseEntityPartitioner;
import io.datarouter.model.util.CommonFieldSizes;
import io.datarouter.util.HashMethods;

public class SortedBeanEntityKey
extends BaseEntityKey<SortedBeanEntityKey>{

	/************* fields *************************/
	private String foo;
	private String bar;

	public static class FieldKeys{
		public static final StringFieldKey foo = new StringFieldKey("foo")
				.withSize(CommonFieldSizes.MAX_KEY_LENGTH_UTF8MB4);
		public static final StringFieldKey bar = new StringFieldKey("bar")
				.withSize(CommonFieldSizes.MAX_KEY_LENGTH_UTF8MB4);
	}

	@Override
	public List<Field<?>> getFields(){
		return Arrays.asList(
				new StringField(FieldKeys.foo, foo),
				new StringField(FieldKeys.bar, bar));
	}

	public static class SortedBeanEntityPartitioner4 extends BaseEntityPartitioner<SortedBeanEntityKey>{
		@Override
		public int getNumPartitions(){
			return 4;
		}
		@Override
		public int getPartition(SortedBeanEntityKey ek){
			String hashInput = ek.foo + ek.bar;
			long hash = HashMethods.longDjbHash(hashInput) % getNumPartitions();
			return (int)(hash % getNumPartitions());
		}
	}


	/****************** construct *******************/

	public SortedBeanEntityKey(){
	}

	public SortedBeanEntityKey(String foo, String bar){
		this.foo = foo;
		this.bar = bar;
	}


	/********************** get/set ***************************/

	public String getFoo(){
		return foo;
	}

	public String getBar(){
		return bar;
	}


}
