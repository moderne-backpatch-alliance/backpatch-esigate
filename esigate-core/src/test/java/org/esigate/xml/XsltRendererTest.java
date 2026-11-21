package org.esigate.xml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.output.StringBuilderWriter;
import org.esigate.HttpErrorPage;
import org.esigate.xml.XsltRenderer;
import org.junit.Test;

public class XsltRendererTest {

	/**
	 * Tests xpath expression evaluation
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	@Test
	public void testXslt() throws IOException, HttpErrorPage {
		String src = "<html><body>The body<br></body></html>";
		String template = "<?xml version=\"1.0\"?>";
		template += "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
		template += "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/> indent=\"no\"";
		template += "<xsl:template match=\"//html:body\">";
		template += "<xsl:copy-of select=\".\"/>";
		template += "</xsl:template>";
		template += "</xsl:stylesheet>";
		StringWriter out = new StringWriter();
		XsltRenderer tested = new XsltRenderer(template);
		tested.render(null, src, out);
		assertEquals(
				"<body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br/></body>",
				out.toString());
	}

	@Test(expected = ProcessingFailedException.class)
	public void testSecureRendering() throws IOException {
		String src = "<html><body>The body<br></body></html>";
		renderExtensionStyleSheet(src);
	}

	private String renderExtensionStyleSheet(String src) throws IOException {
		String template = "<?xml version=\"1.0\"?>";
		template += "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" "
				+ "xmlns:html=\"http://www.w3.org/1999/xhtml\" "
				+ "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
		template += "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>";
		template += "<xsl:template match=\"/\"  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:rt=\"http://xml.apache.org/xalan/java/java.lang.Runtime\">";

		template += "<xsl:variable name=\"rtObj\" select=\"rt:getRuntime()\"/>";
		template += "<xsl:variable name=\"process\" select=\"rt:totalMemory($rtObj)\"/>";
		template += "Process: <xsl:value-of select=\"$process\"/>\n";

		template += "</xsl:template>";

		template += "</xsl:stylesheet>";
		StringBuilderWriter out = new StringBuilderWriter();
		XsltRenderer tested = new XsltRenderer(template);
		tested.render(null, src, out);
		return out.toString();
	}
}
