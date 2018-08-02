package org.nioserver.example;

import java.io.IOException;

import org.nioserver.demo.*;
import org.nioserver.http.HttpMessageReaderFactory;

public class Main {

	public static void main(String[] args) throws IOException {

		String httpResponse = "HTTP/1.1 200 OK\r\nContent-Length: 38\r\nContent-Type: text/html\r\n\r\n<html><body>Hello World!</body></html>";

		byte[] httpResponseBytes = httpResponse.getBytes("UTF-8");

		MessageProcessor messageProcessor = (request, writeProxy) -> {
			System.out.println("Message Received from socket: " + request.socketId);

			Message response = writeProxy.getMessage();
			response.socketId = request.socketId;
			response.writeToMessage(httpResponseBytes);

			writeProxy.enqueue(response);
		};

		Server server = new Server(9999, new HttpMessageReaderFactory(), messageProcessor);

		server.start();

	}

}
