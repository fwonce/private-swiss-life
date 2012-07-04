package fwonce.xia.exe;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.junit.Test;


import fwonce.xia.constant.ResourceType;
import fwonce.xia.core.HtmlParsing;
import fwonce.xia.core.Id3Writing;
import fwonce.xia.misc.InfoHolder;

public class Id3ReaderTest {
	
	@Test
	public void test_parseSomething() throws ParserException {
		String taskName = "2094009";
		Parser parser = new Parser(taskName + ResourceType.MAIN_HTM.getSuf());
		parser.setEncoding("UTF-8");
		NodeList nodeList = HtmlParsing._parseManyThings(parser, new LinkRegexFilter("/search/find\\?artist"), taskName, "test");
		System.out.println(nodeList.size());
	}

	@Test
	public void test_parseTitle() throws ParserException {
		String taskName = "1472016";
		Parser parser = new Parser(taskName + ResourceType.MAIN_HTM.getSuf());
		parser.setEncoding("UTF-8");
		System.out.println(HtmlParsing.parseTitle(parser, taskName));
	}

	@Test
	public void test_parseAlbum() throws ParserException {
		String taskName = "2094009";
		Parser parser = new Parser(taskName + ResourceType.MAIN_HTM.getSuf());
		parser.setEncoding("UTF-8");
		System.out.println(HtmlParsing.parseAlbum(parser, taskName));
	}

	@Test
	public void test_parseArtist() throws ParserException {
		String taskName = "2094009";
		Parser parser = new Parser(taskName + ResourceType.MAIN_HTM.getSuf());
		parser.setEncoding("UTF-8");
		System.out.println(HtmlParsing.parseArtist(parser, taskName));
	}

	@Test
	public void test_parseYear() throws ParserException {
		String taskName = "2094009";
		Parser parser = new Parser(taskName + ResourceType.ALBUM_HTM.getSuf());
		parser.setEncoding("UTF-8");
		System.out.println(HtmlParsing.parseYear(parser, taskName));
	}
	
	@Test
	public void test_InfoHolder() throws ParserException {
		InfoHolder infoHolder = Id3Writing.getInfoHolder("2094009");
		System.out.println(infoHolder);
	}
}
