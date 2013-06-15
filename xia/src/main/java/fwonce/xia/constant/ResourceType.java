package fwonce.xia.constant;

import java.util.EnumSet;

/**
 * 写一个mp3的标签，依赖的资源类型
 * 
 * @author Floyd Wan
 */
public enum ResourceType {

	MAIN_HTM(".htm", Constants.songUrlPrefix),
	ALBUM_HTM(".album.htm", Constants.albumUrlPrefix), ;

	private String suf;

	private String pre;

	ResourceType(String suf, String pre) {
		this.suf = suf;
		this.pre = pre;
	}

	public String getSuf() {
		return suf;
	}

	public String getPre() {
		return pre;
	}

	public static final EnumSet<ResourceType> ALL_OF = EnumSet
			.allOf(ResourceType.class);

}
