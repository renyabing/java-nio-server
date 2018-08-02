package org.nioserver.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.nioserver.demo.MessageReader;
import org.nioserver.demo.Message;
import org.nioserver.demo.MessageBuffer;
import org.nioserver.demo.Socket;

public class HttpMessageReader implements MessageReader {

	private MessageBuffer messageBuffer = null;

	private List<Message> completeMessages = new ArrayList<Message>();
	private Message nextMessage = null;

	public HttpMessageReader() {
	}

	@Override
	public void init(MessageBuffer readMessageBuffer) {
		this.messageBuffer = readMessageBuffer;
		this.nextMessage = messageBuffer.getMessage();
		this.nextMessage.metaData = new HttpHeaders();
	}

	@Override
	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
		socket.read(byteBuffer);
		byteBuffer.flip();

		if (byteBuffer.remaining() == 0) {
			byteBuffer.clear();
			return;
		}

		this.nextMessage.writeToMessage(byteBuffer);

		int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray, this.nextMessage.offset,
				this.nextMessage.offset + this.nextMessage.length, (HttpHeaders) this.nextMessage.metaData);
		if (endIndex != -1) {
			Message message = this.messageBuffer.getMessage();
			message.metaData = new HttpHeaders();

			message.writePartialMessageToMessage(nextMessage, endIndex);

			completeMessages.add(nextMessage);
			nextMessage = message;
		}
		byteBuffer.clear();
	}

	@Override
	public List<Message> getMessages() {
		return this.completeMessages;
	}

}
