package org.nioserver.test;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SelectorTest {

	@Test
	public void test() throws IOException {
		Selector selector = Selector.open();
		SocketChannel socketChannel = SocketChannel.open();

		socketChannel.bind(new InetSocketAddress("127.0.0.1", 10000));
		socketChannel.configureBlocking(false);

		SelectionKey key1 = socketChannel.register(selector, SelectionKey.OP_WRITE);
		SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
		try {
			if (key1.isAcceptable()) {
				// ......
			}
		} catch (Exception e) {
			key1.cancel();
		}
		try {
			if (key2.isAcceptable()) {
				// ......
			}
		} catch (Exception e) {
			key2.cancel();
		}
	}

}
