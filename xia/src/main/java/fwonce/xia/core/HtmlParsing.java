/**
 * 
 */
package fwonce.xia.core;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import com.google.common.base.Preconditions;

/**
 * @author Floyd Wan
 */
public class HtmlParsing {

	public static String parseTitle(Parser parser, String taskName)
			throws ParserException {
		NodeFilter filter = new HasAttributeFilter("id", "title");
		Node node = _parseSureThing(parser, filter, taskName, "title");
		return _cleanup(node.toPlainTextString());
	}

	public static String parseArtist(Parser parser, String taskName)
			throws ParserException {
		NodeFilter filter = new AndFilter(
						new LinkRegexFilter("(/artist/\\d+)|(/search/find\\?artist=)"),
						new HasParentFilter(new TagNameFilter("td")));
		NodeList nodeList = _parseManyThings(parser, filter, taskName, "artist");
		StringBuilder sb = new StringBuilder();
		SimpleNodeIterator iterator = nodeList.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			sb.append(_cleanup(node.toPlainTextString()));
			if (iterator.hasMoreNodes())
				sb.append(", ");
		}
		return sb.toString();
	}

	public static String parseAlbum(Parser parser, String taskName)
			throws ParserException {
		NodeFilter filter = new HasAttributeFilter("id", "albumCover");
		LinkTag node = (LinkTag) _parseSureThing(parser, filter, taskName,
				"album");
		return _cleanup(node.getAttribute("title"));
	}

	public static String parseYear(Parser parser, String taskName)
			throws ParserException {
		NodeFilter filter = new AndFilter(new RegexFilter("\\d+年\\d+月\\d+日"),
				new HasParentFilter(new TagNameFilter("td")));
		Node node = _parseSureThing(parser, filter, taskName, "year");
		return _cleanup(StringUtils.substringBefore(node.toPlainTextString(),
				"年"));
	}

	public static Node _parseSureThing(Parser parser, NodeFilter filter,
			String taskName, String stub) throws ParserException {
		NodeList nodeList = parser.parse(filter);
		Preconditions.checkArgument(nodeList.size() == 1, String.format(
				"failed parsing %s for %s, got %d", stub, taskName,
				nodeList.size()));
		return nodeList.elementAt(0);
	}

	public static NodeList _parseManyThings(Parser parser, NodeFilter filter,
			String taskName, String stub) throws ParserException {
		NodeList nodeList = parser.parse(filter);
		Preconditions.checkArgument(nodeList.size() >= 1, String.format(
				"failed parsing %s for %s, got %d", stub, taskName,
				nodeList.size()));
		return nodeList;
	}

	private static String _cleanup(String s) {
		return s.trim().replaceAll(" +", " ");
	}

}
