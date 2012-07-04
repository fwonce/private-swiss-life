package fwonce.xia.misc;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fwonce.xia.constant.ResourceType;

/**
 * 已下好的网页
 * 
 * @author Floyd Wan
 */
public class TaskQueue {

	private Map<String, Set<ResourceType>> taskResources = Maps
			.newHashMap();

	public static TaskQueue inst = new TaskQueue();

	private TaskQueue() {
	}

	private static Queue<String> finished = Lists.newLinkedList();

	public void put(String name, ResourceType rt) {
		if (!taskResources.containsKey(name)) {
			taskResources.put(name, Sets.<ResourceType>newHashSet());
		}
		Set<ResourceType> already = taskResources.get(name);
		already.add(rt);
		// when all resources are ready
		if (already.containsAll(ResourceType.ALL_OF)) {
			finished.add(name);
		}
	}

	public String get() {
		return finished.poll();
	}

}
