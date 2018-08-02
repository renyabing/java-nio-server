package org.nioserver.http;

import java.io.UnsupportedEncodingException;

public class HttpUtil {
	private static final byte[] GET = "GET".getBytes();
	private static final byte[] POST = "POST".getBytes();
	private static final byte[] PUT = "PUT".getBytes();
	private static final byte[] HEAD = "HEAD".getBytes();
	private static final byte[] DELETE = "DELETE".getBytes();

	// private static final byte[] HOST = "Host".getBytes();
	private static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();

	public static int parseHttpRequest(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders) {

		// parse HTTP request line
		int endOfFirstLine = findNextLineBreak(src, startIndex, endIndex);
		if (endOfFirstLine == -1)
			return -1;

		// parse HTTP headers
		int prevEndOfHeader = endOfFirstLine + 1;
		int endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);

		// prevEndOfHeader + 1 = end of previous header + 2 (+2 = CR + LF)
		while (endOfHeader != -1 && endOfHeader != prevEndOfHeader + 1) {
			if (matches(src, prevEndOfHeader, CONTENT_LENGTH)) {
				try {
					findContentLength(src, prevEndOfHeader, endIndex, httpHeaders);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			prevEndOfHeader = endOfHeader + 1;
			endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
		}

		if (endOfHeader == -1)
			return -1;

		// check that byte array contains full HTTP message.
		int bodyStartIndex = endOfHeader + 1;
		int bodyEndIndex = bodyStartIndex + httpHeaders.contentLength;

		if (bodyEndIndex <= endIndex) {
			// byte array contains a full HTTP request
			httpHeaders.bodyStartIndex = bodyStartIndex;
			httpHeaders.bodyEndIndex = bodyEndIndex;
			return bodyEndIndex;
		}

		return -1;
	}

	private static void findContentLength(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders)
			throws UnsupportedEncodingException {
		int indexOfColon = findNext(src, startIndex, endIndex, (byte) ':');

		// skip spaces after colon
		int index = indexOfColon + 1;
		while (src[index] == ' ')
			index++;

		int valueStartIndex = index;
		int valueEndIndex = index;
		boolean endOfValueFound = false;

		while (index < endIndex && !endOfValueFound) {
			if (src[index] < '0' || '9' < src[index]) {
				endOfValueFound = true;
				valueEndIndex = index;
			}
			index++;
		}

		httpHeaders.contentLength = Integer
				.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, "UTF-8"));

	}

	public static int findNext(byte[] src, int startIndex, int endIndex, byte value) {
		for (int index = startIndex; index < endIndex; index++) {
			if (src[index] == value)
				return index;
		}
		return -1;
	}

	public static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
		for (int index = startIndex; index < endIndex; index++) {
			if (src[index] == '\n') {
				if (src[index - 1] == '\r') {
					return index;
				}
			}
		}
		return -1;
	}

	public static void resolveHttpMethod(byte[] src, int startIndex, HttpHeaders httpHeaders) {
		if (matches(src, startIndex, GET)) {
			httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_GET;
			return;
		}
		if (matches(src, startIndex, POST)) {
			httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_POST;
			return;
		}
		if (matches(src, startIndex, PUT)) {
			httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_PUT;
			return;
		}
		if (matches(src, startIndex, HEAD)) {
			httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_HEAD;
			return;
		}
		if (matches(src, startIndex, DELETE)) {
			httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_DELETE;
			return;
		}
	}

	public static boolean matches(byte[] src, int offset, byte[] value) {
		for (int i = offset, n = 0; n < value.length; i++, n++) {
			if (src[i] != value[n])
				return false;
		}
		return true;
	}
}
