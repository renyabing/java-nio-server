package org.nioserver.demo;

public interface MessageProcessor {

	public void process(Message message, WriteProxy writeProxy);

}
