package fwonce.xia.misc;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import fwonce.xia.constant.Constants;

/**
 * ±È¿˙MP3
 * 
 * @author Floyd Wan
 */
public class DirectoryNavigator implements Iterable<File> {

	private File dir;
	private List<File> fileList = Lists.newArrayList();
	private Integer fileCount;

	public DirectoryNavigator() {
		try {
			dir = new File(Constants.absPath);
			if (dir == null || !dir.isDirectory()) {
				throw new IllegalArgumentException(Constants.absPath + " is not a directory");
			}
			File[] files = dir.listFiles();
			for (File file : files) {
				if (!file.getName().startsWith(".")) {
					this.fileList.add(file);
				}
			}
			this.fileCount = this.fileList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getFileCount() {
		return fileCount;
	}

	@Override
	public Iterator<File> iterator() {
		return fileList.iterator();
	}
}
