package com.gregei.gboy.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LinkCable implements Runnable, Cable {

  private static boolean cableRunning = false;
  private boolean connected = false;
  private int port;
  private boolean terminate = false;

  private Memory memory;
  private Z80 z80;

  private int cycles = 0;

  private Socket connection;

  private BufferedOutputStream streamWriter;
  private BufferedInputStream streamReader;

  public LinkCable(String ip, int port) {
    if (cableRunning) {
      System.out.println("Client Already Started");
      return;
    }

    cableRunning = true;

    this.port = port;
    System.out.println("Attempting To Connect To Server " + ip + " On Port " + port);

    try {
      connection = new Socket(ip, port);

      if (!connection.isConnected()) {
        System.out.println("Attempt To Connect To Server Failed");
        cableRunning = false;
        return;
      }

      connected = true;
      streamReader = new BufferedInputStream(connection.getInputStream());
      streamWriter = new BufferedOutputStream(connection.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Connection To Server Successful");
    Thread thread = new Thread(this);
    thread.start();
  }

  public LinkCable(int port) {
    if (cableRunning) {
      System.out.println("Server Already Running");
      return;
    }

    cableRunning = true;

    this.port = port;

    try {
      ServerSocket server = new ServerSocket(port);
      System.out.println("Server Waiting For Connection");
      connection = server.accept();
      streamReader = new BufferedInputStream(connection.getInputStream());
      streamWriter = new BufferedOutputStream(connection.getOutputStream());
      connected = true;
      System.out.println("Client Connected To Server");
    } catch (IOException e) {
      e.printStackTrace();
    }

    Thread thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run() {
    while (connected) {
      try {

        int data = 0, clock, initial, b1, b2, b3, b4, v = 0;
        while ((data != -1) && (!terminate)) {           /* This needs to terminate */

          initial = streamReader.read();

          b1 = streamReader.read();
          b2 = streamReader.read();
          b3 = streamReader.read();
          b4 = streamReader.read();

          clock = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);

          data = streamReader.read();

          try {
            java.lang.Thread.sleep(5);
          } catch (InterruptedException ignored) {

          }

          if (initial == 1) {
            streamWriter.write(0);

            writeInt(streamWriter, -1);

            streamWriter.write(memory.read8(0xFF01));
            streamWriter.flush();
            memory.write8(0xFF02, memory.read8(0xFF02) & 0x7F);

            memory.write8(0xFF01, data);
            z80.requestInterrupt(3);

          } else if (initial == 0) {
            memory.write8(0xFF02, memory.read8(0xFF02) & 0x7F);
            memory.write8(0xFF01, data);
            z80.requestInterrupt(3);
          }
        }
      } catch (IOException ignored) {

      }
    }
  }

  public void stop() {
    terminate = true;
    connected = false;
  }

  public void writeInt(BufferedOutputStream s, int i) {
    int b1, b2, b3, b4;

    b1 = i & 0x000000FF;
    b2 = (i & 0x0000FF00) >> 8;
    b3 = (i & 0x00FF0000) >> 16;
    b4 = (i & 0xFF000000) >> 24;

    try {
      s.write(b1);
      s.write(b2);
      s.write(b3);
      s.write(b4);
    } catch (IOException ignored) {

    }
  }

  public void send(int b) {
    try {
      streamWriter.write(1);
      writeInt(streamWriter, 0);
      streamWriter.write(b);
      streamWriter.flush();

      try {
        java.lang.Thread.sleep(10);
      } catch (InterruptedException ignored) {

      }

    } catch (IOException ignored) {

    }
  }

  @Override
  public void transfer(int data, int shift) {
    try {
      streamWriter.write(shift);
      streamWriter.write(data);
      streamWriter.flush();
    } catch (IOException e) {
      System.out.println("Transfer Error");
    }

  }

  public void updateCable(int cycles) {
    boolean countingCycles = false;
    if (countingCycles) {
      this.cycles += cycles;
    }
  }

  @Override
  public void setMemory(Memory memory) {
    this.memory = memory;
  }

  @Override
  public void setZ80(Z80 z80) {
    this.z80 = z80;
  }
}
