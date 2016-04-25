package com.nestedworld.nestedworld.api.socket.runnable;

import android.support.annotation.NonNull;

import java.io.PrintWriter;

public class PacketSender implements Runnable {
    private final PrintWriter out;
    private final String packet;

    public PacketSender(@NonNull final PrintWriter out, @NonNull final String packet) {
        this.out = out;
        this.packet = packet;
    }

    @Override
    public void run() {
        out.println(packet);
        out.flush();
    }
}
