package fwonce.xia.misc;

/**
 * ÔªÊý¾Ý
 * 
 * @author Floyd Wan
 */
public class InfoHolder {

	public String artist;
	public String album;
	public String title;
	public String year;

	/**
	 * for debugging
	 */
	@Override
	public String toString() {
		return artist + " [" + album + "] - " +  title + " //" + year;
	}
}
