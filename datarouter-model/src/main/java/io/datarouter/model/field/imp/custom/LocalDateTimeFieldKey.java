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
package io.datarouter.model.field.imp.custom;

import java.time.LocalDateTime;

import io.datarouter.model.field.BaseFieldKey;
import io.datarouter.model.field.encoding.FieldGeneratorType;

public class LocalDateTimeFieldKey extends BaseFieldKey<LocalDateTime>{

	private static final int DEFAULT_NUM_FRACTIONAL_SECONDS = 3;

	private final int numFractionalSeconds;

	/**
	 * Defines a LocalDateFieldKey with millis precision
	 */
	public LocalDateTimeFieldKey(String name){
		super(name, LocalDateTime.class);
		this.numFractionalSeconds = DEFAULT_NUM_FRACTIONAL_SECONDS;
	}

	private LocalDateTimeFieldKey(String name, String columnName, boolean nullable,
			FieldGeneratorType fieldGeneratorType, LocalDateTime defaultValue, int numFractionalSeconds){
		super(name, columnName, nullable, LocalDateTime.class, fieldGeneratorType, defaultValue);
		this.numFractionalSeconds = numFractionalSeconds;
	}

	public int getNumFractionalSeconds(){
		return numFractionalSeconds;
	}

	public LocalDateTimeFieldKey withColumnName(String columnNameOverride){
		return new LocalDateTimeFieldKey(name, columnNameOverride, nullable, fieldGeneratorType, defaultValue,
				numFractionalSeconds);
	}

	/**
	 * Defines a LocalDateFieldKey with 0-9 fractional seconds of precision
	 */
	public LocalDateTimeFieldKey overrideNumFractionalSeconds(int numFractionalSeconds){
		if(numFractionalSeconds < 0 || numFractionalSeconds > 9){
			throw new RuntimeException("numFractionalSeconds cannot be less than 0 or greater than 9");
		}
		return new LocalDateTimeFieldKey(name, columnName, nullable, fieldGeneratorType, defaultValue,
				numFractionalSeconds);
	}

	@Override
	public LocalDateTimeField createValueField(final LocalDateTime value){
		return new LocalDateTimeField(this, value);
	}

}
