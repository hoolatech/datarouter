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
package io.datarouter.web.monitoring;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.datarouter.storage.config.DatarouterProperties;
import io.datarouter.util.DateTool;
import io.datarouter.util.SystemTool;
import io.datarouter.util.bytes.ByteUnitTool;
import io.datarouter.util.collection.CollectorTool;
import io.datarouter.util.duration.Duration;
import io.datarouter.web.app.WebappName;
import io.datarouter.web.config.DatarouterWebFiles;
import io.datarouter.web.dispatcher.NonEagerInitHandler;
import io.datarouter.web.handler.BaseHandler;
import io.datarouter.web.handler.encoder.JsonEncoder;
import io.datarouter.web.handler.mav.Mav;

/**
 * This class needs to access a folder containing a library which doesn't exist during unit tests
 */
public class MemoryMonitoringHandler extends BaseHandler implements NonEagerInitHandler{

	private static final String PATTERN = "d MMM H:mm";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

	@Inject
	private DatarouterProperties datarouterProperties;
	@Inject
	private WebappName webappName;
	@Inject
	private GitProperties gitProperties;
	@Inject
	private BuildProperties buildProperties;
	@Inject
	private LoadedLibraries loadedLibraries;
	@Inject
	private DatarouterWebFiles files;

	@Handler(defaultHandler = true)
	protected Mav view(){
		Mav mav = new Mav(files.jsp.admin.datarouter.memoryStats.memoryJsp);
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		long startTime = runtimeMxBean.getStartTime();
		long uptime = runtimeMxBean.getUptime();
		Map<String,GitPropertiesJspDto> gitDetailedLibraries = loadedLibraries.gitDetailedLibraries.entrySet().stream()
				.collect(CollectorTool.toMap(Entry::getKey, entry -> new GitPropertiesJspDto(entry.getValue())));
		mav.put("startTime", DateTool.format(PATTERN, startTime));
		mav.put("upTime", new Duration(uptime, TimeUnit.MILLISECONDS).toString(TimeUnit.MINUTES));
		mav.put("serverName", datarouterProperties.getServerName());
		mav.put("serverVersion", servletContext.getServerInfo());
		mav.put("javaVersion", SystemTool.getJavaVersion());
		mav.put("jvmVersion", runtimeMxBean.getVmName() + " (build " + runtimeMxBean.getVmVersion() + ")");
		mav.put("appName", webappName);
		mav.put("gitDescribeShort", gitProperties.getDescribeShort().orElse(GitProperties.UNKNOWN_STRING));
		mav.put("gitBranch", gitProperties.getBranch().orElse(GitProperties.UNKNOWN_STRING));
		mav.put("gitCommit", gitProperties.getIdAbbrev().orElse(GitProperties.UNKNOWN_STRING));
		mav.put("gitCommitUserName", gitProperties.getCommitUserName().orElse(GitProperties.UNKNOWN_STRING));
		mav.put("gitCommitTime", FORMATTER.format(gitProperties.getCommitTime().orElse(GitProperties.UNKNOWN_DATE)));
		mav.put("buildTime", FORMATTER.format(gitProperties.getBuildTime().orElse(GitProperties.UNKNOWN_DATE)));
		mav.put("gitTags", gitProperties.getTags().orElse(GitProperties.UNKNOWN_STRING));
		mav.put("buildId", buildProperties.getBuildId());
		mav.put("detailedLibraries", gitDetailedLibraries);
		mav.put("buildDetailedLibraries", loadedLibraries.buildDetailedLibraries);
		mav.put("otherLibraries", loadedLibraries.otherLibraries);

		Runtime runtime = Runtime.getRuntime();
		MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		mav.put("heap", new MemoryUsageForDisplay(heap));
		MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
		mav.put("nonHeap", new MemoryUsageForDisplay(nonHeapMemoryUsage));

		List<MemoryPoolMXBean> memoryPoolMxBeans = ManagementFactory.getMemoryPoolMXBeans();
		List<MemoryPoolForDisplay> heaps = new LinkedList<>();
		List<MemoryPoolForDisplay> nonHeaps = new LinkedList<>();
		for(MemoryPoolMXBean memoryPoolMxBean : memoryPoolMxBeans){
			switch(memoryPoolMxBean.getType()){
			case HEAP:
				heaps.add(new MemoryPoolForDisplay(memoryPoolMxBean));
				break;
			case NON_HEAP:
				nonHeaps.add(new MemoryPoolForDisplay(memoryPoolMxBean));
				break;
			default:
				break;
			}
		}
		mav.put("heaps", heaps);
		mav.put("nonHeaps", nonHeaps);

		ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
		int procNumber = runtime.availableProcessors();
		int threadCount = threadMxBean.getThreadCount();
		int daemonCount = threadMxBean.getDaemonThreadCount();

		mav.put("procNumber", procNumber);
		mav.put("threadCount", threadCount);
		mav.put("daemon", daemonCount);
		mav.put("nonDaemon", threadCount - daemonCount);
		mav.put("peak", threadMxBean.getPeakThreadCount());
		mav.put("started", threadMxBean.getTotalStartedThreadCount());

		List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
		List<GarbageCollectorForDisplay> es = new LinkedList<>();
		for(GarbageCollectorMXBean garbageCollectorMxBean : garbageCollectorMxBeans){
			es.add(new GarbageCollectorForDisplay(garbageCollectorMxBean));
		}
		mav.put("gcs", es);

		return mav;
	}

	@Handler(encoder = JsonEncoder.class)
	private GarbabeCollectingResult garbageCollector(){
		String serverName = params.required("serverName");
		if(!serverName.equals(datarouterProperties.getServerName())){
			return new GarbabeCollectingResult(false, null, null);
		}
		List<MemoryPoolMXBean> memoryPoolMxBeans = ManagementFactory.getMemoryPoolMXBeans();
		Map<String,Long> map = new HashMap<>();
		for(MemoryPoolMXBean memoryPoolMxBean : memoryPoolMxBeans){
			map.put(memoryPoolMxBean.getName(), memoryPoolMxBean.getUsage().getUsed());
		}
		long start = System.currentTimeMillis();
		System.gc();
		long duration = System.currentTimeMillis() - start;
		List<GcEffect> effects = new LinkedList<>();
		for(MemoryPoolMXBean memoryPoolMxBean : ManagementFactory.getMemoryPoolMXBeans()){
			GcEffect gcEffect = new GcEffect(memoryPoolMxBean.getName(), map.get(memoryPoolMxBean.getName()),
					memoryPoolMxBean.getUsage().getUsed());
			effects.add(gcEffect);
		}
		return new GarbabeCollectingResult(true, duration, effects);
	}

	@SuppressWarnings("unused")
	private static class GarbabeCollectingResult{

		private final boolean success;
		private final Long duration;
		private final List<GcEffect> effects;

		private GarbabeCollectingResult(boolean success, Long duration, List<GcEffect> effects){
			this.success = success;
			this.duration = duration;
			this.effects = effects;
		}

	}

	public static class GarbageCollectorForDisplay{

		private String name;
		private long collectionCount;
		private Duration collectionTime;
		private String[] memoryPoolNames;

		public GarbageCollectorForDisplay(GarbageCollectorMXBean garbageCollectorMxBean){
			name = garbageCollectorMxBean.getName();
			collectionCount = garbageCollectorMxBean.getCollectionCount();
			collectionTime = new Duration(garbageCollectorMxBean.getCollectionTime(), TimeUnit.MILLISECONDS);
			memoryPoolNames = garbageCollectorMxBean.getMemoryPoolNames();
		}

		public String getName(){
			return name;
		}

		public long getCollectionCount(){
			return collectionCount;
		}

		public Duration getCollectionTime(){
			return collectionTime;
		}

		public String[] getMemoryPoolNames(){
			return memoryPoolNames;
		}

	}

	public static class MemoryUsageForDisplay{

		private String max;
		private String unallocated;
		private String allocated;
		private String used;
		private long usedBytes;
		private String free;

		public MemoryUsageForDisplay(MemoryUsage memoryUsage){
			long allocatedBytes = memoryUsage.getCommitted();
			this.usedBytes = memoryUsage.getUsed();
			long maxBytes = memoryUsage.getMax();
			long freeBytes = allocatedBytes - usedBytes;
			long unallocatedBytes = maxBytes - allocatedBytes;
			this.max = ByteUnitTool.byteCountToDisplaySize(maxBytes);
			this.unallocated = ByteUnitTool.byteCountToDisplaySize(unallocatedBytes);
			this.allocated = ByteUnitTool.byteCountToDisplaySize(allocatedBytes);
			this.used = ByteUnitTool.byteCountToDisplaySize(usedBytes);
			this.free = ByteUnitTool.byteCountToDisplaySize(freeBytes);
		}

		public String getMax(){
			return max;
		}

		public String getUnallocated(){
			return unallocated;
		}

		public String getAllocated(){
			return allocated;
		}

		public String getUsed(){
			return used;
		}

		public long getUsedBytes(){
			return usedBytes;
		}

		public String getFree(){
			return free;
		}

	}

	public static class MemoryPoolForDisplay{

		private String name;
		private String escapedName;
		private MemoryUsageForDisplay usage;

		public MemoryPoolForDisplay(MemoryPoolMXBean memoryPoolMxBean){
			this.name = memoryPoolMxBean.getName();
			this.escapedName = name.replace(" ", "-");
			this.usage = new MemoryUsageForDisplay(memoryPoolMxBean.getUsage());
		}

		public String getName(){
			return name;
		}

		public String getEscapedName(){
			return escapedName;
		}

		public MemoryUsageForDisplay getUsage(){
			return usage;
		}

	}

	public static class GcEffect{

		private String name;
		private String saved;
		private Double pct;

		public GcEffect(String name, long before, long after){
			this.name = name;
			long diff = before - after;
			if(diff >= 0){
				this.saved = ByteUnitTool.byteCountToDisplaySize(diff);
			}else{
				this.saved = "-" + ByteUnitTool.byteCountToDisplaySize(-diff);
			}
			if(before > 0){
				this.pct = diff / (double)before;
			}
		}

		public String getName(){
			return name;
		}

		public String getSaved(){
			return saved;
		}

		public double getPct(){
			return pct;
		}

	}
}
