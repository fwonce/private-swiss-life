package fwonce.xia.operate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fwonce.xia.constant.Constants;
import fwonce.xia.constant.ResourceType;
import fwonce.xia.misc.TaskQueue;

/**
 * 下载html文件，并缓存
 * 
 * @author Floyd Wan
 */
public class DocumentFetching implements Runnable {

	private static final String MP3 = ".mp3";
	private static TaskQueue taskPool = TaskQueue.inst;
	private static int BUFFER_SIZE = 1024;
	private String taskName;
	private boolean ready = false;

	public DocumentFetching(File file) {
		if (file == null || !file.isFile() || !file.canWrite()) {
			System.err.println("skip on file: " + file);
			return;
		}
		renameToMp3(file);
		this.taskName = extractTaskTargetId(file);
		this.ready = true;
	}

	private static String extractTaskTargetId(File file) {
		// for Android
		// return file.getName().split("\\.")[0];
		// for iPhone
		return file.getName().substring(file.getName().indexOf("_") + 1, file.getName().indexOf("mp3"));
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

			Document doc = Jsoup.parse(new File(taskName + ResourceType.MAIN_HTM.getSuf()), Constants.xiamiEncoding);
			String albumId = StringUtils.substringAfterLast(doc.select("#albumCover").attr("href"), "/");
			
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
		OutputStream os = new BufferedOutputStream(new FileOutputStream(output));

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
