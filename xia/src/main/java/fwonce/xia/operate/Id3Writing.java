package fwonce.xia.operate;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.io.TextEncoding;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fwonce.xia.constant.Constants;
import fwonce.xia.constant.ResourceType;
import fwonce.xia.misc.TaskQueue;

/**
 * 分析html并写id3信息
 * 
 * @author Floyd Wan
 */
public class Id3Writing implements Runnable {

	static {
		TextEncoding.setDefaultTextEncoding(TextEncoding.UNICODE);
	}

	private static final String EXCL_CMT = "gen by fwonce.xia";
	private static final long timeoutMills = 10000;
	private TaskQueue taskQueue = TaskQueue.inst;
	private Integer taskCount;

	public Id3Writing(Integer taskCount) {
		this.taskCount = taskCount;
	}

	@Override
	public void run() {
		StopWatch sw = new StopWatch();
		sw.start();
		for (int i = 0; i < taskCount; i++) {
			String taskName = null;
			while (null == taskName) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (sw.getTime() > timeoutMills) {
					sw.stop();
					System.err.println("timeout waiting for resource ready "
							+ sw.toString());
					return;
				}
				taskName = taskQueue.get();
			}
			sw.reset();
			sw.start();
			try {
				MediaFile mp3File = new MP3File(new File(
						Constants.getMp3FileName(taskName)));
				if (isUpdatedByXia(mp3File)) {
					System.out.println(taskName + " already tagged.");
				} else {
					ID3V2_3_0Tag tagInfo = getID3V2_3_0Tag(taskName);
					mp3File.setID3Tag(tagInfo);
					mp3File.sync();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isUpdatedByXia(MediaFile mp3File) throws ID3Exception {
//		return null != mp3File.getID3V2Tag()
//				&& EXCL_CMT.equals(mp3File.getID3V2Tag().getComment());
		return false;
	}

	public static ID3V2_3_0Tag getID3V2_3_0Tag(String taskName) throws NumberFormatException, ID3Exception, IOException {
		Document mainDoc = Jsoup.parse(new File(taskName + ResourceType.MAIN_HTM.getSuf()), Constants.xiamiEncoding);
		Document albumDoc = Jsoup.parse(new File(taskName + ResourceType.ALBUM_HTM.getSuf()), Constants.xiamiEncoding);
		ID3V2_3_0Tag tagInfo = new ID3V2_3_0Tag();

		tagInfo.setTitle(mainDoc.select("#title").text().trim());
		tagInfo.setAlbum(mainDoc.select("#albums_info a[href~=album]").text().trim());
		tagInfo.setArtist(mainDoc.select("#albums_info a[href~=artist]").text().trim());
		tagInfo.setYear(Integer.parseInt(StringUtils.substringBefore(
				albumDoc.select("#album_info tbody > tr:eq(3) > td + td").text(), "年")));
		tagInfo.setComment(EXCL_CMT);
		return tagInfo;
	}
}
