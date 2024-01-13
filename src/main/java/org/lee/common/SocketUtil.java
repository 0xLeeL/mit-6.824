package org.lee.common;

import org.lee.lab1.Task;

import java.io.*;
import java.net.Socket;

public class SocketUtil {

    public static void objectSend(Serializable serializable, OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(serializable);
        objectOutputStream.flush();
    }

    public static void objectSend(Serializable serializable, int port) throws IOException {
        try (Socket socket = new Socket("localhost", port)) {
            SocketUtil.objectSend(serializable, socket.getOutputStream());
        }
    }

    public static Object readObject(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return objectInputStream.readObject();
    }
}
