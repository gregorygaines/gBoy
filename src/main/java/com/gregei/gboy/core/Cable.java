package com.gregei.gboy.core;

public interface Cable {

  void transfer(int data, int shift);

  void updateCable(int cycles);

  void setMemory(Memory memory);

  void setZ80(Z80 z80);

  void send(int b);

  void stop();
}
