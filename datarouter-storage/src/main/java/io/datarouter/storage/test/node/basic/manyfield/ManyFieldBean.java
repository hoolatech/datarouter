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
package io.datarouter.storage.test.node.basic.manyfield;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.datarouter.model.databean.BaseDatabean;
import io.datarouter.model.field.Field;
import io.datarouter.model.field.imp.StringField;
import io.datarouter.model.field.imp.StringFieldKey;
import io.datarouter.model.field.imp.array.BooleanArrayField;
import io.datarouter.model.field.imp.array.BooleanArrayFieldKey;
import io.datarouter.model.field.imp.array.ByteArrayField;
import io.datarouter.model.field.imp.array.ByteArrayFieldKey;
import io.datarouter.model.field.imp.array.DelimitedStringArrayField;
import io.datarouter.model.field.imp.array.DelimitedStringArrayFieldKey;
import io.datarouter.model.field.imp.array.DoubleArrayField;
import io.datarouter.model.field.imp.array.DoubleArrayFieldKey;
import io.datarouter.model.field.imp.array.IntegerArrayField;
import io.datarouter.model.field.imp.array.IntegerArrayFieldKey;
import io.datarouter.model.field.imp.array.UInt63ArrayField;
import io.datarouter.model.field.imp.array.UInt63ArrayFieldKey;
import io.datarouter.model.field.imp.comparable.BooleanField;
import io.datarouter.model.field.imp.comparable.BooleanFieldKey;
import io.datarouter.model.field.imp.comparable.CharacterField;
import io.datarouter.model.field.imp.comparable.CharacterFieldKey;
import io.datarouter.model.field.imp.comparable.DoubleField;
import io.datarouter.model.field.imp.comparable.DoubleFieldKey;
import io.datarouter.model.field.imp.comparable.FloatField;
import io.datarouter.model.field.imp.comparable.FloatFieldKey;
import io.datarouter.model.field.imp.comparable.IntegerField;
import io.datarouter.model.field.imp.comparable.IntegerFieldKey;
import io.datarouter.model.field.imp.comparable.LongField;
import io.datarouter.model.field.imp.comparable.LongFieldKey;
import io.datarouter.model.field.imp.comparable.ShortField;
import io.datarouter.model.field.imp.comparable.ShortFieldKey;
import io.datarouter.model.field.imp.comparable.SignedByteField;
import io.datarouter.model.field.imp.comparable.SignedByteFieldKey;
import io.datarouter.model.field.imp.custom.LocalDateTimeField;
import io.datarouter.model.field.imp.custom.LocalDateTimeFieldKey;
import io.datarouter.model.field.imp.custom.LongDateField;
import io.datarouter.model.field.imp.custom.LongDateFieldKey;
import io.datarouter.model.field.imp.enums.IntegerEnumField;
import io.datarouter.model.field.imp.enums.IntegerEnumFieldKey;
import io.datarouter.model.field.imp.enums.StringEnumField;
import io.datarouter.model.field.imp.enums.StringEnumFieldKey;
import io.datarouter.model.field.imp.enums.VarIntEnumField;
import io.datarouter.model.field.imp.enums.VarIntEnumFieldKey;
import io.datarouter.model.field.imp.positive.UInt63Field;
import io.datarouter.model.field.imp.positive.UInt63FieldKey;
import io.datarouter.model.field.imp.positive.VarIntField;
import io.datarouter.model.field.imp.positive.VarIntFieldKey;
import io.datarouter.model.serialize.fielder.BaseDatabeanFielder;
import io.datarouter.model.util.CommonFieldSizes;
import io.datarouter.util.array.LongArray;
import io.datarouter.util.iterable.IterableTool;
import io.datarouter.util.lang.ObjectTool;

public class ManyFieldBean extends BaseDatabean<ManyFieldBeanKey,ManyFieldBean>{

	public static final int DEFAULT_STRING_LENGTH = CommonFieldSizes.DEFAULT_LENGTH_VARCHAR;

	private static final int LEN_STRING_ENUM_FIELD = 20;

	private ManyFieldBeanKey key;

	private Boolean booleanField;
	private Byte byteField;
	private Short shortField;
	private Integer integerField;
	private Long longField;
	private Float floatField;
	private Double doubleField;
	private Date longDateField;
	private LocalDateTime localDateTimeField;
	private Character characterField;
	private String stringField;
	private Integer varIntField;
	private TestEnum intEnumField;
	private TestEnum varIntEnumField;
	private TestEnum stringEnumField;

	private byte[] stringByteField;

	private byte[] data;

	private List<Long> longArrayField;
	private List<Boolean> booleanArrayField;
	private List<Integer> integerArrayField;
	private List<Double> doubleArrayField;
	private List<String> delimitedStringArrayField;
	private byte[] byteArrayField;
	private String testSchemaUpdateField;
	private Long incrementField;

	public static class FieldKeys{
		public static final BooleanFieldKey booleanField = new BooleanFieldKey("booleanField");
		public static final SignedByteFieldKey byteField = new SignedByteFieldKey("byteField");
		public static final ShortFieldKey shortField = new ShortFieldKey("shortField");
		public static final IntegerFieldKey integerField = new IntegerFieldKey("integerField");
		public static final LongFieldKey longField = new LongFieldKey("longField");
		public static final FloatFieldKey floatField = new FloatFieldKey("floatField");
		public static final DoubleFieldKey doubleField = new DoubleFieldKey("doubleField");
		public static final LongDateFieldKey longDateField = new LongDateFieldKey("longDateField");
		public static final LocalDateTimeFieldKey localDateTimeField = new LocalDateTimeFieldKey("localDateTimeField");
		public static final CharacterFieldKey characterField = new CharacterFieldKey("characterField");
		public static final StringFieldKey stringField = new StringFieldKey("stringField")
				.withSize(CommonFieldSizes.MAX_KEY_LENGTH_UTF8MB4);
		public static final VarIntFieldKey varIntField = new VarIntFieldKey("varIntField");
		public static final IntegerEnumFieldKey<TestEnum> intEnumField = new IntegerEnumFieldKey<>("intEnumField",
				TestEnum.class);
		public static final VarIntEnumFieldKey<TestEnum> varIntEnumField = new VarIntEnumFieldKey<>("varIntEnumField",
				TestEnum.class);
		public static final StringEnumFieldKey<TestEnum> stringEnumField = new StringEnumFieldKey<>("stringEnumField",
				TestEnum.class).withSize(LEN_STRING_ENUM_FIELD);
		public static final ByteArrayFieldKey stringByteField = new ByteArrayFieldKey("stringByteField")
				.withSize(CommonFieldSizes.MAX_LENGTH_LONGBLOB);
		public static final ByteArrayFieldKey data = new ByteArrayFieldKey("data")
				.withSize(CommonFieldSizes.MAX_LENGTH_LONGBLOB);
		public static final UInt63ArrayFieldKey longArrayField = new UInt63ArrayFieldKey("longArrayField");
		public static final BooleanArrayFieldKey booleanArrayField = new BooleanArrayFieldKey("booleanArrayField");
		public static final IntegerArrayFieldKey integerArrayField = new IntegerArrayFieldKey("integerArrayField");
		public static final ByteArrayFieldKey byteArrayField = new ByteArrayFieldKey("byteArrayField")
				.withSize(CommonFieldSizes.MAX_LENGTH_VARBINARY);
		public static final DoubleArrayFieldKey doubleArrayField = new DoubleArrayFieldKey("doubleArrayField");
		public static final DelimitedStringArrayFieldKey delimitedStringArrayField = new DelimitedStringArrayFieldKey(
				"delimitedStringArrayField");
		public static final StringFieldKey testSchemaUpdateField = new StringFieldKey("testSchemaUpdateField")
				.withSize(CommonFieldSizes.MAX_KEY_LENGTH_UTF8MB4);
		public static final UInt63FieldKey incrementField = new UInt63FieldKey("incrementField");
	}

	public boolean equalsAllPersistentFields(ManyFieldBean that){
		if(ObjectTool.notEquals(key, that.key)){
			return false;
		}
		if(ObjectTool.notEquals(booleanField, that.booleanField)){
			return false;
		}
		if(ObjectTool.notEquals(byteField, that.byteField)){
			return false;
		}
		if(ObjectTool.notEquals(shortField, that.shortField)){
			return false;
		}
		if(ObjectTool.notEquals(integerField, that.integerField)){
			return false;
		}
		if(ObjectTool.notEquals(longField, that.longField)){
			return false;
		}
		if(ObjectTool.notEquals(floatField, that.floatField)){
			return false;
		}
		if(ObjectTool.notEquals(doubleField, that.doubleField)){
			return false;
		}
		if(ObjectTool.notEquals(longDateField, that.longDateField)){
			return false;
		}
		if(ObjectTool.notEquals(localDateTimeField, that.localDateTimeField)){
			return false;
		}
		if(ObjectTool.notEquals(characterField, that.characterField)){
			return false;
		}
		if(ObjectTool.notEquals(stringField, that.stringField)){
			return false;
		}
		if(ObjectTool.notEquals(varIntField, that.varIntField)){
			return false;
		}
		if(ObjectTool.notEquals(intEnumField, that.intEnumField)){
			return false;
		}
		if(ObjectTool.notEquals(varIntEnumField, that.varIntEnumField)){
			return false;
		}
		if(ObjectTool.notEquals(stringEnumField, that.stringEnumField)){
			return false;
		}
		if(ObjectTool.notEquals(stringByteField, that.stringByteField)){
			return false;
		}
		if(ObjectTool.notEquals(data, that.data)){
			return false;
		}
		if(ObjectTool.notEquals(longArrayField, that.longArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(booleanArrayField, that.booleanArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(integerArrayField, that.integerArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(byteArrayField, that.byteArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(doubleArrayField, that.doubleArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(delimitedStringArrayField, that.delimitedStringArrayField)){
			return false;
		}
		if(ObjectTool.notEquals(testSchemaUpdateField, that.testSchemaUpdateField)){
			return false;
		}
		if(ObjectTool.notEquals(incrementField, that.incrementField)){
			return false;
		}
		return true;
	}

	public static class ManyFieldTypeBeanFielder extends BaseDatabeanFielder<ManyFieldBeanKey,ManyFieldBean>{
		public ManyFieldTypeBeanFielder(){
			super(ManyFieldBeanKey.class);
		}

		@Override
		public List<Field<?>> getNonKeyFields(ManyFieldBean databean){
			return Arrays.asList(
					new BooleanField(FieldKeys.booleanField, databean.booleanField),
					new SignedByteField(FieldKeys.byteField, databean.byteField),
					new ShortField(FieldKeys.shortField, databean.shortField),
					new IntegerField(FieldKeys.integerField, databean.integerField),
					new LongField(FieldKeys.longField, databean.longField),
					new FloatField(FieldKeys.floatField, databean.floatField),
					new DoubleField(FieldKeys.doubleField, databean.doubleField),
					new LongDateField(FieldKeys.longDateField, databean.longDateField),
					new LocalDateTimeField(FieldKeys.localDateTimeField, databean.localDateTimeField),
					new CharacterField(FieldKeys.characterField, databean.characterField),
					new StringField(FieldKeys.stringField, databean.stringField),
					new VarIntField(FieldKeys.varIntField, databean.varIntField),
					new IntegerEnumField<>(FieldKeys.intEnumField, databean.intEnumField),
					new VarIntEnumField<>(FieldKeys.varIntEnumField, databean.varIntEnumField),
					new StringEnumField<>(FieldKeys.stringEnumField, databean.stringEnumField),
					new ByteArrayField(FieldKeys.stringByteField, databean.stringByteField),
					new ByteArrayField(FieldKeys.data, databean.data),
					new UInt63ArrayField(FieldKeys.longArrayField, databean.longArrayField),
					new BooleanArrayField(FieldKeys.booleanArrayField, databean.booleanArrayField),
					new IntegerArrayField(FieldKeys.integerArrayField, databean.integerArrayField),
					new ByteArrayField(FieldKeys.byteArrayField, databean.byteArrayField),
					new DoubleArrayField(FieldKeys.doubleArrayField, databean.doubleArrayField),
					new DelimitedStringArrayField(FieldKeys.delimitedStringArrayField,
							databean.delimitedStringArrayField),
					new StringField(FieldKeys.testSchemaUpdateField, databean.testSchemaUpdateField),
					new UInt63Field(FieldKeys.incrementField, databean.incrementField));
		}

	}

	public ManyFieldBean(){// no-arg and public
		this.key = new ManyFieldBeanKey();// let the key generate a random value
	}

	public ManyFieldBean(Long id){
		this.key = new ManyFieldBeanKey(id);
	}

	@Override
	public Class<ManyFieldBeanKey> getKeyClass(){
		return ManyFieldBeanKey.class;
	}

	@Override
	public ManyFieldBeanKey getKey(){
		return key;
	}

	public static List<ManyFieldBean> filterForStringValue(Collection<ManyFieldBean> ins, String value){
		List<ManyFieldBean> outs = new LinkedList<>();
		for(ManyFieldBean in : IterableTool.nullSafe(ins)){
			if(Objects.equals(in.getStringField(), value)){
				outs.add(in);
			}
		}
		return outs;
	}

	public List<Long> appendToLongArrayField(long val){
		if(longArrayField == null){
			longArrayField = new LongArray();
		}
		longArrayField.add(val);
		return longArrayField;
	}

	public List<Boolean> appendToBooleanArrayField(Boolean val){
		if(booleanArrayField == null){
			booleanArrayField = new ArrayList<>();
		}
		booleanArrayField.add(val);
		return booleanArrayField;
	}

	public List<Double> appendToDoubleArrayField(Double val){
		if(doubleArrayField == null){
			doubleArrayField = new ArrayList<>();
		}
		doubleArrayField.add(val);
		return doubleArrayField;
	}

	public List<Integer> appendToIntegerArrayField(Integer val){
		if(integerArrayField == null){
			integerArrayField = new ArrayList<>();
		}
		integerArrayField.add(val);
		return integerArrayField;
	}

	public List<String> appendToDelimitedStringArrayField(String val){
		if(delimitedStringArrayField == null){
			delimitedStringArrayField = new ArrayList<>();
		}
		delimitedStringArrayField.add(val);
		return delimitedStringArrayField;
	}

	public byte[] getData(){
		return data;
	}

	public void setData(byte[] data){
		this.data = data;
	}

	public void setKey(ManyFieldBeanKey key){
		this.key = key;
	}

	public Long getId(){
		return key.getId();
	}

	public void setId(Long id){
		key.setId(id);
	}

	public Integer getIntegerField(){
		return integerField;
	}

	public void setIntegerField(Integer integerField){
		this.integerField = integerField;
	}

	public Short getShortField(){
		return shortField;
	}

	public void setShortField(Short shortField){
		this.shortField = shortField;
	}

	public Byte getByteField(){
		return byteField;
	}

	public void setByteField(Byte byteField){
		this.byteField = byteField;
	}

	public Long getLongField(){
		return longField;
	}

	public void setLongField(Long longField){
		this.longField = longField;
	}

	public Float getFloatField(){
		return floatField;
	}

	public void setFloatField(Float floatField){
		this.floatField = floatField;
	}

	public Double getDoubleField(){
		return doubleField;
	}

	public void setDoubleField(Double doubleField){
		this.doubleField = doubleField;
	}

	public Character getCharacterField(){
		return characterField;
	}

	public void setCharacterField(Character characterField){
		this.characterField = characterField;
	}

	public String getStringField(){
		return stringField;
	}

	public void setStringField(String stringField){
		this.stringField = stringField;
	}

	public byte[] getStringByteField(){
		return stringByteField;
	}

	public void setStringByteField(byte[] stringByteField){
		this.stringByteField = stringByteField;
	}

	public List<Long> getLongArrayField(){
		return longArrayField;
	}

	public void setLongArrayField(List<Long> longArrayField){
		this.longArrayField = longArrayField;
	}

	public List<Boolean> getBooleanArrayField(){
		return booleanArrayField;
	}

	public void setBooleanArrayField(List<Boolean> booleanArrayField){
		this.booleanArrayField = booleanArrayField;
	}

	public List<Double> getDoubleArrayField(){
		return doubleArrayField;
	}

	public void setDoubleArrayField(List<Double> doubleArrayField){
		this.doubleArrayField = doubleArrayField;
	}

	public List<Integer> getIntegerArrayField(){
		return integerArrayField;
	}

	public void setIntegerArrayField(List<Integer> integerArrayField){
		this.integerArrayField = integerArrayField;
	}

	public byte[] getByteArrayField(){
		return byteArrayField;
	}

	public void setByteArrayField(byte[] byteArrayField){
		this.byteArrayField = byteArrayField;
	}

	public Date getLongDateField(){
		return longDateField;
	}

	public void setLongDateField(Date longDateField){
		this.longDateField = longDateField;
	}

	public LocalDateTime getDateTimeField(){
		return localDateTimeField;
	}

	public void setDateTimeField(LocalDateTime dateTimeField){
		this.localDateTimeField = dateTimeField;
	}

	public Integer getVarIntField(){
		return varIntField;
	}

	public void setVarIntField(Integer varIntField){
		this.varIntField = varIntField;
	}

	public TestEnum getIntEnumField(){
		return intEnumField;
	}

	public void setIntEnumField(TestEnum intEnumField){
		this.intEnumField = intEnumField;
	}

	public TestEnum getVarIntEnumField(){
		return varIntEnumField;
	}

	public void setVarIntEnumField(TestEnum varIntEnumField){
		this.varIntEnumField = varIntEnumField;
	}

	public TestEnum getStringEnumField(){
		return stringEnumField;
	}

	public void setStringEnumField(TestEnum stringEnumField){
		this.stringEnumField = stringEnumField;
	}

	public Boolean getBooleanField(){
		return booleanField;
	}

	public void setBooleanField(Boolean booleanField){
		this.booleanField = booleanField;
	}

	public List<String> getDelimitedStringArrayField(){
		return delimitedStringArrayField;
	}

	public void setDelimitedStringArrayField(List<String> delimitedStringArrayField){
		this.delimitedStringArrayField = delimitedStringArrayField;
	}

	public String getTestSchemaUpdateField(){
		return testSchemaUpdateField;
	}

	public void setTestSchemaUpdateField(String testSchemaUpdateField){
		this.testSchemaUpdateField = testSchemaUpdateField;
	}

	public Long getIncrementField(){
		return incrementField;
	}

	public void setIncrementField(Long incrementField){
		this.incrementField = incrementField;
	}

}
