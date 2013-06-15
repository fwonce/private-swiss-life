package fwonce.xia.constant;

import java.util.EnumSet;

/**
 * дһ��mp3�ı�ǩ����������Դ����
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
