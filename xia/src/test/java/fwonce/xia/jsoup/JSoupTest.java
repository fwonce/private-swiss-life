package fwonce.xia.jsoup;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fwonce.xia.constant.Constants;

public class JSoupTest {

	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.parse(new File("1000347.album.htm"), Constants.xiamiEncoding);
		Elements newsHeadlines = doc.select("#album_info tbody > tr:eq(3) > td + td");
		System.out.println(newsHeadlines);
	}
}
