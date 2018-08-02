package org.nioserver.http;

import org.nioserver.demo.MessageReader;
import org.nioserver.demo.MessageReaderFactory;

public class HttpMessageReaderFactory implements MessageReaderFactory {

	public HttpMessageReaderFactory() {
	}

	@Override
	public MessageReader createMessageReader() {
		return new HttpMessageReader();
	}
}
