package org.ednovo.gooru.core.application.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtil implements ParameterProperties {
	private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

	public static String getFileExtenstion(String contentType) {

		if (contentType.equalsIgnoreCase("image/bmp")) {
			return BMP;
		} else if (contentType.equalsIgnoreCase("image/png")) {
			return PNG;
		} else if (contentType.equalsIgnoreCase("image/gif")) {
			return GIF;
		} else {
			return JPG;
		}

	}

	public static boolean downloadAndSaveFile(String urlString, String outputFilePath) {

		try {
			File outputFile = new File(outputFilePath);
			URL url = new URL(urlString);
			URLConnection yc = url.openConnection();
			InputStream inputStream = yc.getInputStream();

			String parentFolder = StringUtils.substringBeforeLast(outputFilePath, "/");
			File parentFolderFile = new File(parentFolder);
			if (!parentFolderFile.exists()) {
				parentFolderFile.mkdirs();
			}

			OutputStream out = new FileOutputStream(outputFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			inputStream.close();

			return true;
		} catch (Exception e) {
			logger.warn("DownloadImage failed:exception:", e);
			return false;
		}
	}

	public static boolean downloadAndSaveFile(String urlString, String outputFilePath, int width, int height) {
		BufferedImage image = null;
		try {
			URL url = new URL(urlString);
			image = ImageIO.read(url);
			String parentFolder = StringUtils.substringBeforeLast(outputFilePath, "/");
			File parentFolderFile = new File(parentFolder);
			if (!parentFolderFile.exists()) {
				parentFolderFile.mkdirs();
			}
			ResampleOp resampleOp = new ResampleOp(width, height);
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			image = resampleOp.filter(image, null);
			ImageIO.write(image, "png", new File(outputFilePath));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean downloadAndSaveFile(String urlString, String outputFilePath, double maxWidth, double maxHeight, boolean autoResize) {
		BufferedImage image = null;
		try {
			URL url = new URL(urlString);
			image = ImageIO.read(url);
			double width = image.getWidth();
			double height = image.getHeight();
			if (autoResize) {

				if (height > maxHeight || width > maxWidth) {
					double ratio = (double) Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());
					width = (image.getWidth() * ratio);
					height = (image.getHeight() * ratio);
				}

			}

			String parentFolder = StringUtils.substringBeforeLast(outputFilePath, "/");
			File parentFolderFile = new File(parentFolder);
			if (!parentFolderFile.exists()) {
				parentFolderFile.mkdirs();
			}
			ResampleOp resampleOp = new ResampleOp((int) width, (int) height);
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			image = resampleOp.filter(image, null);
			ImageIO.write(image, "png", new File(outputFilePath));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getThumbnailUrlByQuery(String query, String imageSize, String aspectRatio) {
		try {

			// get rid of spaces
			query = query.replaceAll("[\\s,\\._:-]+", "%20");
			String filters = "";
			if (imageSize != null) {
				filters += "Image.Filters=Size:" + imageSize;
			}
			if (aspectRatio != null) {
				if (!StringUtils.isEmpty(filters)) {
					filters += "&";
				}
				filters += "Image.Filters=Aspect:" + aspectRatio;
			}
			if (!StringUtils.isEmpty(filters)) {
				filters = "&" + filters;
			}

			// send request to bing.
			String address = "http://api.bing.net/xml.aspx?Appid=E33DF01A3363CBE8CC3C5F4E15F1284647476C8A&sources=image&adlt=strict&query=" + query + filters;
			URL url = new URL(address);
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();

			// xml anem space stuff:
			NamespaceContext ctx = new NamespaceContext() {
				public String getNamespaceURI(String prefix) {
					String uri;
					if (prefix.equals(E)) {
						uri = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/element";
					} else if (prefix.equals(M)) {
						uri = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/multimedia";
					} else {
						uri = null;
					}
					return uri;
				}

				public Iterator getPrefixes(String val) {
					return null;
				}

				public String getPrefix(String uri) {
					return null;
				}
			};

			// create xml doc from input:
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(in);// new
															// File("c:\\users\\a\\Desktop\\test.xml"));

			// create xpath for extract thumbnail url:
			String xpathStr = "/e:SearchResponse/m:Image/m:Results/m:ImageResult/m:Thumbnail/m:Url/text()";
			XPathFactory xpathFact = XPathFactory.newInstance();
			XPath xpath = xpathFact.newXPath();
			xpath.setNamespaceContext(ctx);
			// extract thumbnail url from xml doc reponse:
			String thmbnailUrl = xpath.evaluate(xpathStr, doc);

			xpathStr = "/e:SearchResponse/m:Image/m:Results/m:ImageResult/m:MediaUrl/text()";
			xpathFact = XPathFactory.newInstance();
			xpath = xpathFact.newXPath();
			xpath.setNamespaceContext(ctx);
			// extract thumbnail url from xml doc reponse:
			String imageUrl = xpath.evaluate(xpathStr, doc);

			xpathStr = "/e:SearchResponse/m:Image/m:Results/m:ImageResult/m:ContentType/text()";
			xpathFact = XPathFactory.newInstance();
			xpath = xpathFact.newXPath();
			xpath.setNamespaceContext(ctx);
			// extract thumbnail url from xml doc reponse:
			String imageType = xpath.evaluate(xpathStr, doc);

			String fileType = JPG;

			if (imageType != null) {
				fileType = getFileExtenstion(fileType);
			}

			return fileType + "|" + imageUrl + "|" + thmbnailUrl;

		} catch (Exception ex) {

			return null;
		}

	}

}
