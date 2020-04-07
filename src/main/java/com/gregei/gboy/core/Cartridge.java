package com.gregei.gboy.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cartridge {

  public MBC mbc;
  boolean hasBattery = false;
  private int[] rom;
  private String fileName = "";

  public Cartridge(String path) {
    try {
      byte[] signed = Files.readAllBytes(Paths.get(path));
      rom = new int[signed.length];

      for (int i = 0; i < rom.length; ++i) {
        rom[i] = signed[i] & 0xFF;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    int idx = path.replaceAll("\\\\", "/").lastIndexOf("/");
    String a = idx >= 0 ? path.substring(idx + 1) : path;
    String fileName = a.substring(0, a.lastIndexOf('.'));
    System.out.println(fileName);
    this.fileName = fileName;
  }

  // TODO verify cartridge
  public boolean verify() {

    switch (rom[0x0147]) {
      case 0:
        mbc = new None();
        break;

      case 1:
        mbc = new MBC1();
        break;

      case 3:
        hasBattery = true;
        mbc = new MBC1();
        break;

      case 0x13:
        hasBattery = true;
        mbc = new MBC3();
        break;

      case 0x1B:
        hasBattery = true;
        mbc = new MBC5();
        break;

      default:
        System.out.println("Unknown mbc " + Integer.toHexString(rom[0x0147]));
        System.exit(0);
    }

    return true;
  }

  public int read8(int address) {
    return mbc.read(address);
  }

  public void write8(int address, int data) {
    mbc.write(address, data);
  }

  public String getRomName() {
    return fileName;
  }

  public int[] getRam() {
    return mbc.getRam();
  }

  public boolean batteryLoaded() {
    return hasBattery;
  }

  public interface Clock {

    Clock SYSTEM_CLOCK = new Clock() {
      @Override
      public long currentTimeMillis() {
        return System.currentTimeMillis();
      }
    };

    long currentTimeMillis();
  }

  public interface MBC {

    int[] getRam();

    void writeDirect(int bank, int address, int data);

    int read(int address);

    void write(int address, int data);
  }

  public static class RealTimeClock {

    private final Clock clock;

    private long offsetSec;

    private long clockStart;

    private boolean halt;

    private long latchStart;

    private int haltSeconds;

    private int haltMinutes;

    private int haltHours;

    private int haltDays;

    public RealTimeClock(Clock clock) {
      this.clock = clock;
      this.clockStart = clock.currentTimeMillis();
    }

    public void latch() {
      latchStart = clock.currentTimeMillis();
    }

    public void unlatch() {
      latchStart = 0;
    }

    public int getSeconds() {
      return (int) (clockTimeInSec() % 60);
    }

    public void setSeconds(int seconds) {
      if (!halt) {
        return;
      }
      haltSeconds = seconds;
    }

    public int getMinutes() {
      return (int) ((clockTimeInSec() % (60 * 60)) / 60);
    }

    public void setMinutes(int minutes) {
      if (!halt) {
        return;
      }
      haltMinutes = minutes;
    }

    public int getHours() {
      return (int) ((clockTimeInSec() % (60 * 60 * 24)) / (60 * 60));
    }

    public void setHours(int hours) {
      if (!halt) {
        return;
      }
      haltHours = hours;
    }

    public int getDayCounter() {
      return (int) (clockTimeInSec() % (60 * 60 * 24 * 512) / (60 * 60 * 24));
    }

    public void setDayCounter(int dayCounter) {
      if (!halt) {
        return;
      }
      haltDays = dayCounter;
    }

    public boolean isHalt() {
      return halt;
    }

    public void setHalt(boolean halt) {
      if (halt && !this.halt) {
        latch();
        haltSeconds = getSeconds();
        haltMinutes = getMinutes();
        haltHours = getHours();
        haltDays = getDayCounter();
        unlatch();
      } else if (!halt && this.halt) {
        offsetSec = haltSeconds + haltMinutes * 60 + haltHours * 60 * 60 + haltDays * 60 * 60 * 24;
        clockStart = clock.currentTimeMillis();
      }
      this.halt = halt;
    }

    public boolean isCounterOverflow() {
      return clockTimeInSec() >= 60 * 60 * 24 * 512;
    }

    public void clearCounterOverflow() {
      while (isCounterOverflow()) {
        offsetSec -= 60 * 60 * 24 * 512;
      }
    }

    private long clockTimeInSec() {
      long now;
      if (latchStart == 0) {
        now = clock.currentTimeMillis();
      } else {
        now = latchStart;
      }
      return (now - clockStart) / 1000 + offsetSec;
    }

    public void deserialize(long[] clockData) {
      long seconds = clockData[0];
      long minutes = clockData[1];
      long hours = clockData[2];
      long days = clockData[3];
      long daysHigh = clockData[4];
      long timestamp = clockData[10];

      this.clockStart = timestamp * 1000;
      this.offsetSec = seconds + minutes * 60 + hours * 60 * 60 + days * 24 * 60 * 60
          + daysHigh * 256 * 24 * 60 * 60;
    }

    public long[] serialize() {
      long[] clockData = new long[11];
      latch();
      clockData[0] = clockData[5] = getSeconds();
      clockData[1] = clockData[6] = getMinutes();
      clockData[2] = clockData[7] = getHours();
      clockData[3] = clockData[8] = getDayCounter() % 256;
      clockData[4] = clockData[9] = getDayCounter() / 256;
      clockData[10] = latchStart / 1000;
      unlatch();
      return clockData;
    }
  }

  class None implements MBC {

    @Override
    public int[] getRam() {
      return new int[0];
    }

    @Override
    public void writeDirect(int bank, int address, int data) {

    }

    public int read(int address) {
      if (address <= 0x7FFF) {
        return rom[address];
      } else {
        //  System.out.println("Can't read address " + Integer.toHexString(address));
      }
      return 0;
    }

    public void write(int address, int data) {
      //System.out.println("Can't write");
    }
  }

  class MBC1 implements MBC {

    private final int ramSize = rom[0x149];
    private final int[] ramBanks = new int[0x8000];
    boolean ramEnabled = false;
    boolean ramMode = false;
    private int romBank = 1;

    public MBC1() {
      if (hasBattery) {
        System.out.println("Loading ram MCB1");
        try {
          String savPath = "";
          if (Files.exists(Paths.get(savPath = (fileName + ".sav")))) {
            System.out.println("Path " + savPath);
            byte[] signedRam = Files.readAllBytes(Paths.get(savPath));
            for (int i = 0; i < signedRam.length; ++i) {
              ramBanks[i] = signedRam[i] & 0xFF;
            }
          } else {
            System.out.println("Save File Doesn't Exist!");
          }
        } catch (IOException e) {
          System.out.println("MBC3 ram error");
        }
      }
    }

    @Override
    public int[] getRam() {
      return ramBanks;
    }

    @Override
    public void writeDirect(int bank, int address, int data) {

    }

    @Override
    public int read(int address) {
      if (address <= 0x3FFF) {
        return rom[address];
      } else if (address >= 0x4000 && address <= 0x7FFF) {
        int bank = romBank;

        if (ramMode) {
          bank = bank & 0x1F;
        }

        int base = address - 0x4000;
        base += (0x4000 * bank);
        base &= 0xffffffff;
        return rom[base];
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        if (ramSize == 0 || !ramEnabled) {
          return 0;
        }

        int bank = (romBank >>> 5) & 0x3;

        int base = address - 0xA000;

        if (ramMode) {
          base += (0x2000 * bank);
        }

        base &= 0xffffffff;
        return ramBanks[base];
      } else {
        System.out.println("Can't read MBC1 address " + Integer.toHexString(address));
        return 0;
      }
    }

    @Override
    public void write(int address, int data) {
      if (address <= 0x1FFF) {
        ramEnabled = (data & 0xA) == 0xA;
      } else if (address >= 0x2000 && address <= 0x3FFF) {
        romBank = (romBank & 0x60);

        if (data == 0) {
          romBank |= 1;
        }

        romBank |= (data & 0x1F);
      } else if (address >= 0x4000 && address <= 0x5FFF) {
        data &= 0x3;
        romBank = romBank & 0x1F;
        romBank = romBank | (data << 5);
      } else if (address >= 0x6000 && address <= 0x7FFF) {
        ramMode = (data & 1) != 0;
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        if (ramSize == 0 || !ramEnabled) {
          return;
        }

        int bank = (romBank >>> 5) & 0x3;

        if (!ramMode) {
          bank = 0;
        }

        int base = address - 0xA000;
        base += (0x2000 * bank);

        ramBanks[base] = data;
      } else {
        System.out.println("Can't write cartridge address " + Integer.toHexString(address));
      }
    }
  }

  class MBC3 implements MBC {

    public final int[] ramBanks = new int[0x8000];
    int rtcBank = 0;
    boolean ramEnabled = false;
    private int romBank = 1;
    private int ramBank = 0;
    private boolean ramWrite = true;

    public MBC3() {
      if (hasBattery) {
        System.out.println("Loading ram MCB3");
        try {
          String savPath = "";
          if (Files.exists(Paths.get(savPath = (fileName + ".sav")))) {
            System.out.println("Path " + savPath);
            byte[] signedRam = Files.readAllBytes(Paths.get(savPath));

            for (int i = 0; i < signedRam.length; ++i) {
              ramBanks[i] = signedRam[i] & 0xFF;
            }

          }
        } catch (IOException e) {
          System.out.println("MBC3 ram error");
        }
      }
    }

    @Override
    public int[] getRam() {
      return ramBanks;
    }

    @Override
    public void writeDirect(int bank, int address, int data) {
      int base = address - 0xA000;
      base += (0x2000 * bank);
      ramBanks[base] = data;
    }

    @Override
    public int read(int address) {
      if (address <= 0x3FFF) {
        return rom[address];
      } else if (address >= 0x4000 && address <= 0x7FFF) {
        int bank = romBank;

        int base = address - 0x4000;
        base += (0x4000 * bank);

        return rom[base];
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        if (!ramEnabled) {
          return 0;
        }

        if (ramWrite) {
          int base = address - 0xA000;
          base += (0x2000 * ramBank);
          return ramBanks[base];
        } else {
          //  TODO RTC
          return 0;
        }
      } else {
        System.out.println("Can't read MBC3 address " + Integer.toHexString(address));
        return 0;
      }
    }

    @Override
    public void write(int address, int data) {
      if (address <= 0x1FFF) {
        ramEnabled = data == 0xA;
      } else if (address >= 0x2000 && address <= 0x3FFF) {
        if (data == 0) {
          romBank = 1;
          return;
        }

        romBank = data;
      } else if (address >= 0x4000 && address <= 0x5FFF) {
        if (data <= 0x3) {
          ramBank = data;
          ramWrite = true;
        } else {
          rtcBank = data;
          ramWrite = false;
        }
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        if (!ramEnabled) {
          return;
        }

        if (ramWrite) {
          int base = address - 0xA000;
          base += (0x2000 * ramBank);
          ramBanks[base] = data;
        } else {
          //  TODO RTC
        }
      } else if (address >= 0x6000 && address <= 0x7FFF) {
        // TODO RTC
      } else {
        System.out.println("Can't write MBC3 address " + Integer.toHexString(address));
      }
    }
  }

  class MBC5 implements MBC {

    final int[] ramBanks = new int[0x100000];
    int romBank = 1;
    int ramBank = 0;
    private boolean ramEnabled = false;

    public MBC5() {
      if (hasBattery) {
        System.out.println("Loading ram");
        try {
          String savPath = "";
          if (Files.exists(Paths.get(savPath = (fileName + ".sav")))) {
            System.out.println("Path " + savPath);
            byte[] signedRam = Files.readAllBytes(Paths.get(savPath));
            for (int i = 0; i < signedRam.length; ++i) {
              ramBanks[i] = signedRam[i] & 0xFF;
            }
          }
        } catch (IOException e) {
          System.out.println("MBC3 ram error");
        }
      }
    }

    @Override
    public int[] getRam() {
      return ramBanks;
    }

    @Override
    public void writeDirect(int bank, int address, int data) {

    }

    @Override
    public int read(int address) {
      if (address <= 0x3FFF) {
        return rom[address];
      } else if (address >= 0x4000 && address <= 0x7FFF) {
        if (!ramEnabled) {
          return 0;
        }

        int base = address - 0x4000;
        base += (0x4000 * romBank);
        base &= 0xffffffff;
        return rom[base];
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        int base = address - 0xA000;
        base += (0x2000 * ramBank);
        base &= 0xffffffff;
        return ramBanks[base];
      } else {
        System.out.println("Can't read MBC3 address " + Integer.toHexString(address));
        return 0;
      }
    }

    @Override
    public void write(int address, int data) {
      if (address <= 0x1FFF) {
        ramEnabled = data == 0xA;
      } else if (address >= 0x2000 && address <= 0x2FFF) {
        romBank &= 0x100;
        romBank |= (data & 0xFF);
      } else if (address >= 0xA000 && address <= 0xBFFF) {
        if (!ramEnabled) {
          return;
        }

        int base = address - 0xA000;
        base += (0x2000 * ramBank);
        base &= 0xffffffff;
        ramBanks[base] = data;
      } else if (address >= 0x3000 && address <= 0x3FFF) {
        romBank = ((data & 1) << 8) | (romBank & 0x00FF);
      } else if (address >= 0x4000 && address <= 0x5FFF) {
        ramBank = data & 0xF;
      } else {
        System.out.println("Can't write address " + Integer.toHexString(address));
      }
    }
  }

}
