package com.gregei.gboy.core;

import com.gregei.gboy.core.sound.Sound;
import java.io.FileOutputStream;
import java.io.IOException;

public class Gameboy {

  private final Z80 cpu;
  private final Memory memory;
  private final LCD lcd;
  private Cable cable;
  private int currentCycles = 0;


  public Gameboy() {
    cpu = new Z80();
    memory = new Memory();
    lcd = new LCD();
    Sound sound = new Sound();

    memory.setZ80(cpu);
    lcd.setZ80(cpu);

    cpu.setMemory(memory);
    lcd.setMemory(memory);
    sound.setMemory(memory);

    cpu.setLCD(lcd);
  }

  public void runFrame() {

    while (currentCycles < 70224) {
      int elapsed = cpu.runCycle();

      currentCycles += elapsed;
      cpu.checkIme();

      for (int i = 0; i < elapsed; ++i) {
        lcd.tick();
        cpu.tickTimer();
      }

      cpu.checkInterrupts();
    }

    currentCycles -= 70224;
  }

  public void updateJoypad(int bit, boolean pressed) {

    boolean before = ((memory.joypad >> bit) & 1) != 0;
    boolean after;

    // Pressed Key
    if (pressed) {
      memory.joypad &= ~(1 << bit);
      after = false;
    }
    // Released Key
    else {
      memory.joypad |= (1 << bit);
      after = true;
    }

    if (before && !after) {
      cpu.requestInterrupt(4);
    }
  }

  public int[] getBuffer() {
    return lcd.buffer;
  }

  public void loadGame(Cartridge cartridge) {
    reset();

    if (!cartridge.verify()) {
      return;
    }

    memory.setCartridge(cartridge);
    setCpuRunning(true);
  }

  public void savRam() {
    if (!memory.getCartridge().batteryLoaded()) {
      return;
    }

    System.out.println("Saving Ram");
    try {
      FileOutputStream file = new FileOutputStream(memory.getCartridge().getRomName() + ".sav");
      int[] data = memory.getCartridge().getRam();
      for (int datum : data) {
        file.write(datum);
      }
      file.close();
    } catch (IOException e) {
      System.out.println("Ram sav Error - " + e.toString());
    }
  }

  public void applyGameShark(String text) {

    if (text.length() == 8) {
      String bankStr = text.substring(0, 2);
      int bank = Integer.decode("0x" + bankStr);
      String dataStr = text.substring(2, 4);
      int data = Integer.decode("0x" + dataStr);
      String upStr = text.substring(6, 8);
      String dwStr = text.substring(4, 6);

      int address = Integer.decode("0x" + upStr + dwStr);

      memory.getCartridge().mbc.writeDirect(bank, address, data);
    }
  }

  public void startServer() {
    System.out.println("Starting Server");
    cable = new LinkCable(9090);
    cable.setZ80(cpu);
    cable.setMemory(memory);
    memory.setLinkCable(cable);
  }

  public void startClient() {
    System.out.println("Starting Client");
    cable = new LinkCable("192.168.0.13", 9090);
    cable.setMemory(memory);
    cable.setZ80(cpu);
    memory.setLinkCable(cable);
  }

  public boolean getCpuRunning() {
    return cpu.cpuRunning;
  }

  public void setCpuRunning(boolean value) {
    cpu.cpuRunning = value;
  }

  public void reset() {
    cpu.reset();
    memory.reset();
    lcd.reset();
  }
}