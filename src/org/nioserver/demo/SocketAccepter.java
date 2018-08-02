package org.nioserver.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

public class SocketAccepter implements Runnable {

	private int tcpPort = 0;
	private ServerSocketChannel serverSocket = null;

	private Queue<Socket> socketQueue = null;

	public SocketAccepter(int tcpPort, Queue<Socket> socketQueue) {
		this.tcpPort = tcpPort;
		this.socketQueue = socketQueue;
	}

	public void run() {
		try {
			this.serverSocket = ServerSocketChannel.open();
			this.serverSocket.bind(new InetSocketAddress(tcpPort));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (true) {
			try {
				SocketChannel socketChannel = this.serverSocket.accept();
				System.out.println("Socket accepted: " + socketChannel);
				this.socketQueue.add(new Socket(socketChannel));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
