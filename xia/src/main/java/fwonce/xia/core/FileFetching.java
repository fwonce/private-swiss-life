package fwonce.xia.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;

import fwonce.xia.constant.ResourceType;
import fwonce.xia.misc.TaskQueue;

/**
 * 下载html文件
 * 
 * @author Floyd Wan
 */
public class FileFetching implements Runnable {

	private static final String MP3 = ".mp3";
	private static TaskQueue taskPool = TaskQueue.inst;
	private static int BUFFER_SIZE = 1024;
	private String taskName;
	private boolean ready = false;

	public FileFetching(File file) {
		if (file == null || !file.isFile() || !file.canWrite()) {
			System.err.println("skip on file: " + file);
			return;
		}
		renameToMp3(file);
		ready = true;
		this.taskName = file.getName().split("\\.")[0];
	}

	/**
	 * 如果不是MP3后缀则加上
	 */
	private static void renameToMp3(File file) {
		if (!StringUtils.endsWith(file.getName(), MP3)) {
			file.renameTo(new File(file.getAbsoluteFile() + MP3));
		}
	}

	@Override
	public void run() {
		if (!ready) {
			return;
		}
		try {
			fetchFile(taskName, taskName, ResourceType.MAIN_HTM);

			Parser parser = new Parser(taskName
					+ ResourceType.MAIN_HTM.getSuf());
			parser.setEncoding("UTF-8");
			LinkTag node = (LinkTag) HtmlParsing._parseSureThing(parser,
					new HasAttributeFilter("id", "albumCover"), taskName,
					"albumId");
			String albumId = StringUtils.substringAfterLast(
					node.getAttribute("href"), "/");
			
			fetchFile(albumId, taskName, ResourceType.ALBUM_HTM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void fetchFile(String urlPart, String taskName, ResourceType rt) throws Exception {
		String target = taskName + rt.getSuf();
		File output = new File(target);
		if (output.exists()) {
			// System.out.println("Found and Skipped " + output.getName());
			taskPool.put(taskName, rt);
			return;
		}
		OutputStream os = new BufferedOutputStream(new FileOutputStream(
				output));

		byte[] bytes = new byte[BUFFER_SIZE];
		int num;
		URL url = new URL(rt.getPre() + urlPart);
		InputStream is = url.openStream();
		while ((num = is.read(bytes)) > 0) {
			os.write(bytes, 0, num);
		}
		System.out.println("Downloaded " + output.getName());
		is.close();
		os.close();
		
		taskPool.put(taskName, rt);
	}
}
