package com.nestedworld.nestedworld.api.socket.runnable;

import java.io.BufferedReader;
import java.io.IOException;

public class PacketReceiver implements Runnable {

    private BufferedReader in;

    PacketReceiver(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        Boolean run = true;
        while (run) {
            try {
                String message = in.readLine();
                System.out.println("reception: " + message);
            } catch (IOException e) {
                run = false;
                e.printStackTrace();
            }
        }
    }
}
