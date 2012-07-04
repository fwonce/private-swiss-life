package fwonce.xia.core;

import java.io.File;

import org.apache.commons.lang3.time.StopWatch;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.io.TextEncoding;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;


import fwonce.xia.constant.ResourceType;
import fwonce.xia.constant.Uris;
import fwonce.xia.misc.InfoHolder;
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
						Uris.getMp3FileName(taskName)));
				if (isUpdatedByXia(mp3File)) {
					System.out.println(taskName + " already tagged.");
				} else {
					InfoHolder infoHolder = getInfoHolder(taskName);
					ID3V2_3_0Tag tagInfo = new ID3V2_3_0Tag();
					tagInfo.setTitle(infoHolder.title);
					tagInfo.setAlbum(infoHolder.album);
					tagInfo.setArtist(infoHolder.artist);
					tagInfo.setYear(Integer.parseInt(infoHolder.year));
					tagInfo.setComment(EXCL_CMT);
					
					mp3File.setID3Tag(tagInfo);
					mp3File.sync();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean isUpdatedByXia(MediaFile mp3File) throws ID3Exception {
		return null != mp3File.getID3V2Tag()
				&& EXCL_CMT.equals(mp3File.getID3V2Tag().getComment());
	}

	public static InfoHolder getInfoHolder(String taskName)
			throws ParserException {
		Parser parser = new Parser(taskName + ResourceType.MAIN_HTM.getSuf());
		parser.setEncoding("UTF-8");
		Parser parser2 = new Parser(taskName + ResourceType.ALBUM_HTM.getSuf());
		parser2.setEncoding("UTF-8");
		InfoHolder infoHolder = new InfoHolder();

		infoHolder.title = HtmlParsing.parseTitle(parser, taskName);
		parser.reset();
		infoHolder.album = HtmlParsing.parseAlbum(parser, taskName);
		parser.reset();
		infoHolder.artist = HtmlParsing.parseArtist(parser, taskName);
		
		infoHolder.year = HtmlParsing.parseYear(parser2, taskName);

		return infoHolder;
	}

}
