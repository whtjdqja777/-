package com.example.myapplication;

import android.util.Log;
import java.net.*;
import java.io.*;

public class NetworkThreads {
    private String ip;
    private int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean isConnected = false;

    // 싱글톤 인스턴스
    private static NetworkThreads instance;

    // 싱글톤 인스턴스를 얻는 메서드
    public static synchronized NetworkThreads getInstance() {
        if (instance == null) {
            instance = new NetworkThreads();
        }
        return instance;
    }

    // 생성자를 private으로 설정하여 외부에서 인스턴스를 직접 생성하지 못하게 함
    private NetworkThreads() {
        this("203.234.62.226", 10010);
    }

    private NetworkThreads(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Log.d("app", "NetworkThread is initialized");
    }

    public void connectServer() {
        new Thread(new ConnectRunnable()).start();
    }

    public void disconnectServer() {
        new Thread(new DisconnectRunnable()).start();
    }

    public void sendMessage(String msg) {
        new Thread(new SendMessageRunnable(msg)).start();
    }

    public void recvMessages(MessageCallback callback) {
        new Thread(new ReceiveMessagesRunnable(callback)).start();
    }

    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                Log.d("app", "Server connected");
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                isConnected = true;  // 연결 완료 상태를 표시
                Log.d("app", "Reader and writer created");
            } catch (IOException e) {
                Log.e("app", "Error connecting to server", e);
            }
        }
    }

    private class DisconnectRunnable implements Runnable {
        @Override
        public void run() {
            closeResources();
            isConnected = false; // 연결 해제 상태를 표시
            Log.d("disconnect","disconnected: "+ isConnected);
        }
    }

    private class SendMessageRunnable implements Runnable {
        private String msg;

        public SendMessageRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            if (writer != null) {
                writer.println(msg);
                Log.d("app", "Sent: " + msg);
            } else {
                Log.e("app", "Writer is null, message not sent");
            }
        }
    }

    private class ReceiveMessagesRunnable implements Runnable {
        private MessageCallback callback;

        public ReceiveMessagesRunnable(MessageCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                while (isConnected) {  // 연결 상태를 확인
                    Log.d("app", "isConnected: " + isConnected);
                    Log.d("app", "Waiting for message");
                    String recvMsg = reader.readLine();
                    if (recvMsg != null && !recvMsg.isEmpty()) {
                        Log.d("app", "Received: " + recvMsg);
                        callback.onMessageReceived(recvMsg);
                    }
                }
            } catch (IOException | InterruptedException e) {
                Log.e("app", "Error receiving message", e);
            }
        }
    }

    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
            Log.d("app", "Resources closed");
        } catch (IOException e) {
            Log.e("app", "Error closing resources", e);
        }
    }

    public interface MessageCallback {
        void onMessageReceived(String message) throws InterruptedException;
    }
}
