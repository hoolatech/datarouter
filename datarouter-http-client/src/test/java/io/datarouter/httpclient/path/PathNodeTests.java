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
package io.datarouter.httpclient.path;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PathNodeTests{

	public static class TestPaths extends PathNode{
		public final BPaths aa = branch(BPaths::new, "aa");

		public static class BPaths extends PathNode{
			public final CPaths bb = branch(CPaths::new, "bb");
		}

		public static class CPaths extends PathNode{
			public final PathNode cc = leaf("cc");
		}
	}

	@Test
	public void testToSlashedString(){
		TestPaths paths = new TestPaths();
		Assert.assertEquals(paths.aa.toSlashedString(), "/aa");
		Assert.assertEquals(paths.aa.bb.toSlashedString(), "/aa/bb");
		Assert.assertEquals(paths.aa.bb.cc.toSlashedString(), "/aa/bb/cc");
	}

	@Test
	public void testNodesAfter(){
		TestPaths paths = new TestPaths();
		List<PathNode> nodesAfter = PathNode.nodesAfter(paths.aa, paths.aa.bb.cc);
		Assert.assertEquals(PathNode.toSlashedString(nodesAfter), "/bb/cc");
	}

	@Test
	public void testToSlashedStringAfter(){
		TestPaths paths = new TestPaths();
		PathNode cc = paths.aa.bb.cc;
		Assert.assertEquals(cc.toSlashedStringAfter(null), "/aa/bb/cc");
		Assert.assertEquals(cc.toSlashedStringAfter(paths.aa), "/bb/cc");
		Assert.assertEquals(cc.toSlashedStringAfter(paths.aa.bb), "/cc");
	}

	@Test
	public void testEquals(){
		TestPaths paths1 = new TestPaths();
		TestPaths paths2 = new TestPaths();
		Assert.assertNotSame(paths1.aa, paths2.aa);
		Assert.assertEquals(paths1.aa, paths2.aa);
	}

}
