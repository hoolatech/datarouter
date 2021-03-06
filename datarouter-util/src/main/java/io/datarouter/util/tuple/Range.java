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
package io.datarouter.util.tuple;

import java.util.ArrayList;
import java.util.Objects;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.datarouter.util.ComparableTool;
import io.datarouter.util.iterable.IterableTool;
import io.datarouter.util.lang.ObjectTool;

/* * null compares first
 * * startInclusive defaults to true
 * * endExclusive defaults to false
 */
public class Range<T extends Comparable<? super T>> implements Comparable<Range<T>>{

	/*------------------------- fields --------------------------------------*/

	private T start;
	private boolean startInclusive;
	private T end;
	private boolean endInclusive;

	/*------------------------- constructors --------------------------------*/

	public Range(T start){
		this(start, true, null, false);
	}

	public Range(T start, boolean startInclusive){
		this(start, startInclusive, null, false);
	}

	public Range(T start, T end){
		this(start, true, end, false);
	}

	public Range(T start, boolean startInclusive, T end, boolean endInclusive){
		this.start = start;
		this.startInclusive = startInclusive;
		this.end = end;
		this.endInclusive = endInclusive;
	}


	/*------------------------- static constructors -------------------------*/

	public static <T extends Comparable<? super T>> Range<T> nullSafe(Range<T> in){
		if(in != null){
			return in;
		}
		return everything();
	}

	public static <T extends Comparable<? super T>> Range<T> everything(){
		return new Range<>(null, true);
	}

	/*------------------------- methods -------------------------------------*/

	public Range<T> assertValid(){
		if(ObjectTool.anyNull(start, end)){
			return this;
		}
		if(start.compareTo(end) > 0){
			throw new IllegalStateException("start is after end for " + this);
		}
		return this;
	}

	public boolean isEmptyStart(){
		return start == null;
	}

	public boolean hasStart(){
		return start != null;
	}

	public boolean isEmptyEnd(){
		return end == null;
	}

	public boolean hasEnd(){
		return end != null;
	}

	public boolean equalsStartEnd(){
		return Objects.equals(start, end);
	}

	public boolean matchesStart(T item){
		if(!hasStart()){
			return true;
		}
		int diff = item.compareTo(start);
		return startInclusive ? diff >= 0 : diff > 0;
	}

	public boolean matchesEnd(T item){
		if(!hasEnd()){
			return true;
		}
		int diff = item.compareTo(end);
		return endInclusive ? diff <= 0 : diff < 0;
	}

	public boolean contains(T item){
		return matchesStart(item) && matchesEnd(item);
	}

	public ArrayList<T> filter(Iterable<T> ins){
		ArrayList<T> outs = new ArrayList<>();
		for(T in : IterableTool.nullSafe(ins)){
			if(contains(in)){
				outs.add(in);
			}
		}
		return outs;
	}

	public boolean isEmpty(){
		return equalsStartEnd() && start != null && !(startInclusive && endInclusive);
	}

	public boolean notEmpty(){
		return !isEmpty();
	}

	@Override
	public Range<T> clone(){
		return new Range<>(start, startInclusive, end, endInclusive);
	}

	/*------------------------- standard ------------------------------------*/

	//auto-gen
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + (end == null ? 0 : end.hashCode());
		result = prime * result + (endInclusive ? 1231 : 1237);
		result = prime * result + (start == null ? 0 : start.hashCode());
		result = prime * result + (startInclusive ? 1231 : 1237);
		return result;
	}

	//auto-gen
	@Override
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		Range<?> other = (Range<?>)obj;
		if(end == null){
			if(other.end != null){
				return false;
			}
		}else if(!end.equals(other.end)){
			return false;
		}
		if(endInclusive != other.endInclusive){
			return false;
		}
		if(start == null){
			if(other.start != null){
				return false;
			}
		}else if(!start.equals(other.start)){
			return false;
		}
		if(startInclusive != other.startInclusive){
			return false;
		}
		return true;
	}

	/*
	 * currently only compares start values, but end values could make sense
	 *
	 * null comes before any value
	 */
	@Override
	public int compareTo(Range<T> that){
		return compareStarts(this, that);
	}

	public static <T extends Comparable<? super T>> int compareStarts(Range<T> itemA, Range<T> itemB){
		if(itemA == itemB){
			return 0;
		}
		int diff = ComparableTool.nullFirstCompareTo(itemA.start, itemB.start);
		if(diff != 0){
			return diff;
		}
		if(itemA.startInclusive){
			return itemB.startInclusive ? 0 : -1;
		}
		return itemB.startInclusive ? 1 : 0;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName() + ":");
		sb.append(startInclusive ? "[" : "(");
		sb.append(start + "," + end);
		sb.append(endInclusive ? "]" : ")");
		return sb.toString();
	}

	/*------------------------- get/set -------------------------------------*/

	public T getStart(){
		return start;
	}

	public void setStart(T start){
		this.start = start;
	}

	public boolean getStartInclusive(){
		return startInclusive;
	}

	public void setStartInclusive(boolean startInclusive){
		this.startInclusive = startInclusive;
	}

	public T getEnd(){
		return end;
	}

	public void setEnd(T end){
		this.end = end;
	}

	public boolean getEndInclusive(){
		return endInclusive;
	}

	public void setEndInclusive(boolean endInclusive){
		this.endInclusive = endInclusive;
	}

	/*------------------------- tests ---------------------------------------*/

	public static class RangeTests{
		@Test
		public void testContains(){
			Range<Integer> rangeA = new Range<>(3, true, 5, true);
			Assert.assertFalse(rangeA.contains(2));
			Assert.assertTrue(rangeA.contains(3));
			Assert.assertTrue(rangeA.contains(5));
			Assert.assertFalse(rangeA.contains(6));
			Range<Integer> rangeB = new Range<>(3, false, 5, false);
			Assert.assertFalse(rangeB.contains(3));
			Assert.assertTrue(rangeB.contains(4));
			Assert.assertFalse(rangeB.contains(5));
			Range<Integer> rangeC = new Range<>(7, true, 7, true);
			Assert.assertTrue(rangeC.contains(7));
			Range<Integer> rangeD = new Range<>(8, false, 8, false);
			Assert.assertFalse(rangeD.contains(8));
			Range<Integer> rangeE = new Range<>(9, true, 9, false);//exclusive should win (?)
			Assert.assertFalse(rangeE.contains(9));
		}
		@Test
		public void testCompareStarts(){
			Range<Integer> rangeA = new Range<>(null, true, null, true);
			Assert.assertEquals(0, compareStarts(rangeA, rangeA));
			Range<Integer> rangeB = new Range<>(null, false, null, true);
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeA, rangeB));
			Range<Integer> rangeC = new Range<>(null, true, 999, true);
			Assert.assertEquals(0, ComparableTool.compareAndAssertReflexive(rangeA, rangeC));
			Range<Integer> rangeD = new Range<>(3, true, 999, true);
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeA, rangeD));
			Range<Integer> rangeE = new Range<>(3, false, 999, true);
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeA, rangeD));
			Range<Integer> rangeF = new Range<>(4, true, 999, true);
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeD, rangeF));
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeE, rangeF));
			Range<Integer> rangeG = new Range<>(4, false, 999, true);
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeD, rangeG));
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeE, rangeG));
			Assert.assertEquals(-1, ComparableTool.compareAndAssertReflexive(rangeF, rangeG));
		}
		@Test
		public void testValidAssert(){
			new Range<>(null, null).assertValid();
			new Range<>(0, null).assertValid();
			new Range<>(null, 0).assertValid();
			new Range<>(0, 1).assertValid();
		}
		@Test(expectedExceptions = IllegalStateException.class)
		public void testInvalidAssert(){
			new Range<>(1, 0).assertValid();
		}
	}

}
