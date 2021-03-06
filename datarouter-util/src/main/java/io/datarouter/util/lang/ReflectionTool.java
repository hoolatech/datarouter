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
package io.datarouter.util.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.datarouter.util.collection.CollectionTool;
import io.datarouter.util.collection.ListTool;
import io.datarouter.util.string.StringTool;

public class ReflectionTool{
	private static final Logger logger = LoggerFactory.getLogger(ReflectionTool.class);

	public static <T> Supplier<T> supplier(Class<T> type){
		return () -> ReflectionTool.create(type);
	}

	private static Class<?> getClass(String className){
		try{
			return Class.forName(className);
		}catch(ClassNotFoundException e){
			throw new RuntimeException(className, e);
		}
	}

	public static <T> Class<? extends T> getAsSubClass(String className, Class<T> superClass){
		return getClass(className).asSubclass(superClass);
	}

	public static <T> T createWithParameters(Class<T> type, Collection<?> requiredParameters){
		for(Constructor<?> constructor : type.getDeclaredConstructors()){
			if(!canParamsCallParamTypes(requiredParameters, Arrays.asList(constructor.getParameterTypes()))){
				continue;
			}
			try{
				constructor.setAccessible(true);
				return type.cast(constructor.newInstance(requiredParameters.toArray()));
			}catch(SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e){
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Could not find constructor " + type.getCanonicalName() + requiredParameters);
	}

	public static Object create(String fullyQualifiedClassName){
		return create(getClass(fullyQualifiedClassName));
	}

	private static <T> T create(Class<T> cls, String exceptionMessage){
		try{
			if(cls.isEnum()){
				T enumValue = cls.getEnumConstants()[0];
				if(enumValue == null){
					throw new IllegalArgumentException("no values in enum class:" + cls.getName());
				}
				return enumValue;
			}
			// use getDeclaredConstructor to access non-public constructors
			Constructor<T> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			T databeanInstance = constructor.newInstance();
			return databeanInstance;
		}catch(Exception e){
			if(exceptionMessage == null){
				throw new RuntimeException(e);
			}
			throw new RuntimeException(exceptionMessage, e);
		}
	}

	public static <T> T create(Class<T> cls){
		return create(cls, null);
	}

	public static Object get(String fieldName, Object object){
		return get(getCachedDeclaredFieldIncludingAncestors(object.getClass(), fieldName), object);
	}

	public static Object get(Field field, Object object){
		if(field != null && object != null){
			if(!field.isAccessible()){
				field.setAccessible(true);
			}
			try{
				return field.get(object);
			}catch(IllegalArgumentException | IllegalAccessException e){
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static void set(Field field, Object object, Object value){
		try{
			field.set(object, value);
		}catch(IllegalArgumentException | IllegalAccessException e){
			throw new RuntimeException(e);
		}
	}

	public static Set<Class<?>> getAllSuperClassesAndInterfaces(Class<?> cls){
		Set<Class<?>> supersAndInterfaces = new LinkedHashSet<>();
		for(Class<?> interfaceClass : cls.getInterfaces()){
			supersAndInterfaces.add(interfaceClass);
			supersAndInterfaces.addAll(getAllSuperClassesAndInterfaces(interfaceClass));
		}
		Class<?> superclass = cls.getSuperclass();
		if(superclass != null){
			supersAndInterfaces.add(superclass);
			supersAndInterfaces.addAll(getAllSuperClassesAndInterfaces(superclass));
		}
		return supersAndInterfaces;
	}

	/*------------------------- get fields ----------------------------------*/

	public static Field getDeclaredFieldFromAncestors(Class<?> clazz, String fieldName){
		Set<Class<?>> supersAndInterfaces = getAllSuperClassesAndInterfaces(clazz);
		supersAndInterfaces.add(clazz);
		for(Class<?> cls : supersAndInterfaces){
			Field superField;
			try{
				superField = cls.getDeclaredField(fieldName);
			}catch(NoSuchFieldException nsfe){
				continue;
			}
			superField.setAccessible(true);
			return superField;
		}
		return null;
	}

	private static class FieldInClass{
		private Class<?> cls;
		private String fieldName;

		public FieldInClass(Class<?> cls, String fieldName){
			this.cls = cls;
			this.fieldName = fieldName;
		}

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + (cls == null ? 0 : cls.hashCode());
			result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj){
			if(obj instanceof FieldInClass){
				FieldInClass other = (FieldInClass)obj;
				return other.fieldName.equals(fieldName) && other.cls.equals(cls);
			}
			return false;
		}
	}

	private static final Map<FieldInClass,Field> cachedDeclaredFields = new ConcurrentHashMap<>();

	public static Field getCachedDeclaredFieldIncludingAncestors(Class<?> cls, String fieldName){
		FieldInClass fieldInClass = new FieldInClass(cls, fieldName);
		Field field = cachedDeclaredFields.get(fieldInClass);
		if(field == null){
			field = getDeclaredFieldFromAncestors(cls, fieldName);
			if(field == null){
				throw new RuntimeException(fieldName + " doesn't exist in " + cls);
			}
			cachedDeclaredFields.put(fieldInClass, field);
		}
		return field;
	}

	public static Field getNestedField(Object object, List<String> fieldNames){
		try{
			String fieldName = CollectionTool.getFirst(fieldNames);
			Field field = getDeclaredFieldFromAncestors(object.getClass(), fieldName);
			field.setAccessible(true);
			if(CollectionTool.size(fieldNames) == 1){
				return field;
			}
			if(field.get(object) == null){// initialize the field
				field.set(object, create(field.getType()));
			}
			return getNestedField(field.get(object), fieldNames.subList(1, fieldNames.size()));
		}catch(Exception e){
			String message = "could not set field: " + object.getClass().getName() + "." + StringTool.concatenate(
					fieldNames, ".");
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * This will return a list of all the fields declared in the super types of YourClass. It will NOT include
	 * the declared fields in YourClass.
	 * @param clazz class from which the field list will be extracted.
	 * @return a list of the inherited declared Fields not including any declared field from YourClass.
	 */
	public static List<Field> getDeclaredFieldsFromAncestors(Class<?> clazz){
		List<Field> fields = new ArrayList<>();
		for(Class<?> cls : getAllSuperClassesAndInterfaces(clazz)){
			for(Field field : cls.getDeclaredFields()){
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * This will return a list of all the fields declared in the given class and in its super types.
	 * @param clazz class from which the field list will be extracted.
	 * @return a list of the inherited declared Fields not including any declared field from YourClass.
	 */
	public static List<Field> getDeclaredFieldsIncludingAncestors(Class<?> clazz){
		return ListTool.concatenate(getDeclaredFields(clazz), getDeclaredFieldsFromAncestors(clazz));
	}

	/**
	 * This is a wrapper for Class.getDeclaredFields().
	 * @param cls class from which the field list will be extracted.
	 * @return a list of the declared Fields not including any inherited field.
	 */
	public static List<Field> getDeclaredFields(Class<?> cls){
		return ListTool.create(cls.getDeclaredFields());
	}

	/*------------------------- get Method ----------------------------------*/

	public static Method getDeclaredMethodIncludingAncestors(Class<?> clazz, String methodName,
			Class<?>... parameterTypes){
		try{
			Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
			if(method != null){
				method.setAccessible(true);
				return method;
			}
		}catch(NoSuchMethodException nsfe){
			// continue
		}
		for(Class<?> cls : getAllSuperClassesAndInterfaces(clazz)){
			try{
				Method superMethod = cls.getDeclaredMethod(methodName, parameterTypes);
				if(superMethod != null){
					return superMethod;
				}
			}catch(NoSuchMethodException nsfe){
				// continue
			}
		}
		return null;
	}

	public static <T> Collection<Method> getDeclaredMethodsWithName(Class<T> cls, String methodName){
		List<Method> methods = new ArrayList<>();
		Set<Class<?>> supersAndInterfaces = getAllSuperClassesAndInterfaces(cls);
		supersAndInterfaces.add(cls);
		for(Class<?> clazz : supersAndInterfaces){
			for(Method method : clazz.getDeclaredMethods()){
				if(method.getName().equals(methodName)){
					method.setAccessible(true);
					methods.add(method);
				}
			}
		}
		return methods;
	}

	public static boolean canParamsCallParamTypes(Collection<?> params, Collection<? extends Class<?>> paramTypes){
		if(CollectionTool.differentSize(params, paramTypes)){
			return false;
		}
		Iterator<?> iterA = params.iterator();
		Iterator<? extends Class<?>> iterB = paramTypes.iterator();
		while(iterA.hasNext()){
			Class<?> typeA = iterA.next().getClass();
			Class<?> typeB = iterB.next();
			if(ClassTool.isEquivalentBoxedType(typeA, typeB)){
				continue;
			}
			if(!typeB.isAssignableFrom(typeA)){
				return false;
			}
		}
		return true;
	}

	/*------------------------- get Value -----------------------------------*/
	public static Object getObjectValueUsingGetterMethod(Object instance, Method method){
		if(instance == null || method == null){
			return null;
		}
		try{
			method.setAccessible(true);
			return method.invoke(instance);
		}catch(IllegalAccessException | InvocationTargetException e){
			logger.error("", e);
		}catch(IllegalArgumentException e){
			logger.error("the method " + method + " or " + instance + " are not a suitable argument ", e);
		}
		return null;
	}

	public static Object invoke(Object obj, String methodName, Object... args){
		Class<?>[] parameterTypes = Arrays.stream(args)
				.map(Object::getClass)
				.toArray(Class[]::new);
		Method method;
		try{
			method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
		}catch(NoSuchMethodException | SecurityException e){
			throw new RuntimeException(e);
		}
		method.setAccessible(true);
		try{
			return method.invoke(obj, args);
		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			throw new RuntimeException(e);
		}
	}

	/*------------------------- Tests ---------------------------------------*/

	public static class ReflectionToolTests{

		public static class DummyDto{
			public final Object field0;
			public final int field1;
			public final Double field2;

			public DummyDto(Object field0, int field1, Double field2){
				this.field0 = field0;
				this.field1 = field1;
				this.field2 = field2;
			}
		}

		public static class ExtensionDto extends DummyDto{
			public final long field3;

			public ExtensionDto(Object field0, int field1, Double field2, long field3){
				super(field0, field1, field2);
				this.field3 = field3;
			}
		}

		@Test
		public void testCanParamsCallParamTypes(){
			Assert.assertTrue(canParamsCallParamTypes(Arrays.asList(4), Arrays.asList(int.class)));
			Assert.assertTrue(canParamsCallParamTypes(Arrays.asList(4), Arrays.asList(Integer.class)));
			Assert.assertFalse(canParamsCallParamTypes(Arrays.asList("a"), Arrays.asList(int.class)));
		}

		@Test
		public void testCreateWithParameters(){
			Object[] params0 = new Object[]{new Object(), 3, 5.5d};
			Assert.assertNotNull(createWithParameters(DummyDto.class, Arrays.asList(params0)));

			Object[] params1 = new Object[]{"stringy", 3, 5.5d};
			Assert.assertNotNull(createWithParameters(DummyDto.class, Arrays.asList(params1)));
		}

		@Test(expectedExceptions = {Exception.class})
		public void testCreateWithParametersInvalid(){
			Object[] params0 = new Object[]{new Object(), "square peg", 5.5d};
			DummyDto dummyDto = createWithParameters(DummyDto.class, Arrays.asList(params0));
			Assert.assertNotNull(dummyDto);
		}

		@Test
		public void testGetDeclaredFieldsFromAncestors(){
			Assert.assertEquals(getDeclaredFieldsFromAncestors(ExtensionDto.class).size(), 3);
		}

		@Test
		public void testGetDeclaredFieldsIncludingAncestors(){
			Assert.assertEquals(getDeclaredFieldsIncludingAncestors(ExtensionDto.class).size(), 4);
		}

		@Test
		public void testGetDeclaredFields(){
			Assert.assertEquals(getDeclaredFields(ExtensionDto.class).size(), 1);
		}

	}
}
