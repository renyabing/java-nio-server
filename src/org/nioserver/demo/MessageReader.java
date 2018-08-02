package org.nioserver.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface MessageReader {

    public void init(MessageBuffer readMessageBuffer);

    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

    public List<Message> getMessages();



}
