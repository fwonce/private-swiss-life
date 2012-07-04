package fwonce.xia.constant;

/**
 * ³£Á¿
 * 
 * @author Floyd Wan
 */
public final class Uris {

	public static final String absPath = "e:/music_new/xiami/";

	static final String songUrlPrefix = "http://www.xiami.com/song/";

	static final String albumUrlPrefix = "http://www.xiami.com/album/";

	public static String getMp3FileName(String name) {
		return absPath + name + ".mp3";
	}
}
