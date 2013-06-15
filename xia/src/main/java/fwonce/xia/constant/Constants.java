package fwonce.xia.constant;

/**
 * 常量
 * 
 * @author Floyd Wan
 */
public final class Constants {

	/**
	 * 以slash结尾
	 */
	public static final String absPath = "/Volumes/hd/fwonce/temp/xia/";

	public static final String songUrlPrefix = "http://www.xiami.com/song/";

	public static final String albumUrlPrefix = "http://www.xiami.com/album/";
	
	public static final String xiamiEncoding = "UTF-8";

	public static String getMp3FileName(String name) {
		return absPath + "84504_" +name + "mp3.mp3";
	}
}
