package com.gregei.gboy.core;

import java.awt.Color;
import java.util.Arrays;

/**
 * This class emulates the Gameboy lcd.
 */
public class LCD {

  public final int[] buffer = new int[160 * 144 * 3];
  // 160 * 144 * Red | Green | Blue
  private final int[] gfx = new int[160 * 144 * 3];
  // Difference colors of the lcd
  private final Color[] color = new Color[]{Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY,
      Color.BLACK};
  public int total = 0;
  private int lcdCycles = 0;
  private Memory memory;
  private Z80 cpu;

  public LCD() {

  }

  public void tick() {
    int scanline = memory.read8(0xFF44);

    if (scanline >= 144) {
      changeLCDMode(LcdMode.VBLANK);
    } else {
      if (lcdCycles <= LcdMode.HBLANK.cycles) {
        changeLCDMode(LcdMode.HBLANK);
      } else if (lcdCycles <= LcdMode.OAM.cycles) {
        changeLCDMode(LcdMode.OAM);
      } else if (lcdCycles <= LcdMode.DATA.cycles) {
        changeLCDMode(LcdMode.DATA);
      }
    }

    if (lcdCycles > 456) {
      if (scanline < 144) {
        drawScanline();
      } else if (scanline == 144) {
        System.arraycopy(gfx, 0, buffer, 0, gfx.length);
        cpu.requestInterrupt(0);
      } else if (scanline > 153) {
        total = 0;
        memory.directWrite8(0xFF44, 0);
      }

      lcdCycles = 0;
      incScanline();

    }

    lcdCycles++;

    total += lcdCycles;
  }

  private void drawScanline() {
    if (isBgDisplayEnabled()) {
      drawBackground();
    }

    if (isObjDisplayEnabled()) {
      drawSprites();
    }
  }

  private void drawBackground() {
    // Each background is 256 pixels or 32 x 32 tiles (8x8 pixels)
    int backgroundMapStart;
    int backgroundTileDataStart = bg_WinTileDataStart();
    boolean signed = backgroundTileDataStart == 0x8800;
    boolean windowEnabled = false;

    int x = 0, y = 0;
    int tileX = 0;
    int tileY = 0;
    int tile;
    int tileNumber = 0;
    int tileAddress = 0;

    int scrollX = memory.read8(0xFF43);
    int scrollY = memory.read8(0xFF42);

    int windowX = memory.read8(0xFF4B) - 7;
    int windowY = memory.read8(0xFF4A);

    int ly = memory.read8(0xFF44);

    if (isWindowEnabled()) {
      if (windowY <= ly) {
        windowEnabled = true;
      }
    } else {
      windowEnabled = false;
    }

    if (windowEnabled) {
      y = (ly - windowY) % 256;
    } else {
      y = (ly + scrollY) % 256;
    }

    for (int pixel = 0; pixel < 160; ++pixel) {
      x = (pixel + scrollX) % 256;

      if (windowEnabled) {
        if (pixel >= windowX) {
          x = pixel - windowX;
        }
      }

      tileX = (x / 8);
      tileY = (y / 8);
      tile = (tileY * 32) + tileX;

      if (windowEnabled) {
        backgroundMapStart = windowTileMapStart();
      } else {
        backgroundMapStart = bgTileMapStart();
      }

      tileNumber = signed ? ((byte) memory.read8(backgroundMapStart + tile)) + 128
          : memory.read8(backgroundMapStart + tile);

      tileAddress = backgroundTileDataStart + (tileNumber * 0x10);

      tileAddress = tileAddress + ((y % 8) * 2);

      int data1 = memory.read8(tileAddress);
      int data2 = memory.read8(tileAddress + 1);

      int colorBit = 7 - (x % 8);
      // combine data 2 and data 1 to get the colour id for this pixel
      int colorNumber = ((data2 & (1 << colorBit)) == 0) ? 0 : 0x2;
      colorNumber |= ((data1 & (1 << colorBit)) == 0) ? 0 : 1;

      int line = memory.read8(0xFF44);

      gfx[(((line * 160) + pixel) * 3)] = color[colorNumber].getRed();
      gfx[(((line * 160) + pixel) * 3) + 1] = color[colorNumber].getGreen();
      gfx[(((line * 160) + pixel) * 3) + 2] = color[colorNumber].getBlue();
    }
  }

  private void drawSprites() {
    boolean use8x16 = false;

    if (((memory.read8(0xFF40) >> 2) & 1) != 0) {
      use8x16 = true;
    }

    for (int sprite = 0; sprite < 40; sprite++) {
      int index = sprite * 4;
      int yPos = memory.read8(0xFE00 + index) - 16;
      int xPos = memory.read8(0xFE00 + index + 1) - 8;
      int tileLocation = memory.read8(0xFE00 + index + 2);
      int attribites = memory.read8(0xFE00 + index + 3);

      boolean yFlip = ((attribites >> 6) & 1) != 0;
      boolean xFlip = ((attribites >> 5) & 1) != 0;

      int scanline = memory.read8(0xFF44);

      int ysize = 8;
      if (use8x16) {
        ysize = 16;
      }

      if ((scanline >= yPos) && (scanline < (yPos + ysize))) {
        int line = scanline - yPos;

        if (yFlip) {
          line -= ysize;
          line *= 1;
        }

        line *= 2;
        char dataAddress = (char) ((0x8000 + (tileLocation * 16)) + line);
        int data1 = memory.read8(dataAddress);
        int data2 = memory.read8(dataAddress + 1);

        for (int tilePixel = 7; tilePixel >= 0; --tilePixel) {
          int colourbit = tilePixel;
          if (xFlip) {
            colourbit -= 7;
            colourbit *= -1;
          }

          // the rest is the same as for tiles
          int colourNum = ((data2 >> colourbit) & 1) != 0 ? 1 : 0;
          colourNum <<= 1;
          colourNum |= ((data1 >> colourbit) & 1) != 0 ? 1 : 0;

          if (colourNum != 0) {

            int xPix = -tilePixel;
            xPix += 7;

            int pixel = xPos + xPix;

            gfx[(((memory.read8(0xFF44) * 160) + pixel) * 3)] = color[colourNum].getRed();
            gfx[(((memory.read8(0xFF44) * 160) + pixel) * 3) + 1] = color[colourNum].getGreen();
            gfx[(((memory.read8(0xFF44) * 160) + pixel) * 3) + 2] = color[colourNum].getBlue();
          }

        }
      }
    }
  }

  private void incScanline() {
    int scanline = memory.read8(0xFF44);
    scanline++;
    memory.directWrite8(0xFF44, scanline);
    checkLYC();
  }

  private boolean isLcdEnabled() {
    return ((memory.read8(0xFF40) >> 7) & 1) != 0;
  }

  private boolean isWindowEnabled() {
    return ((memory.read8(0xFF40) >> 5) & 1) != 0;
  }

  private int windowTileMapStart() {
    return (((memory.read8(0xFF40) >> 6) & 1) != 0) ? 0x9C00 : 0x9800;
  }

  private int bg_WinTileDataStart() {
    return (((memory.read8(0xFF40) >> 4) & 1) != 0) ? 0x8000 : 0x8800;
  }

  private int bgTileMapStart() {
    return (((memory.read8(0xFF40) >> 3) & 1) != 0) ? 0x9C00 : 0x9800;
  }

  private boolean isObjDisplayEnabled() {
    return (((memory.read8(0xFF40) >> 1) & 1) != 0);
  }

  private int obgSize() {
    return (((memory.read8(0xFF40) >> 2) & 1) != 0) ? 16 : 8;
  }

  private boolean isBgDisplayEnabled() {
    return (((memory.read8(0xFF40)) & 1) != 0);
  }

  private void changeLCDMode(LcdMode newMode) {
    if (newMode == getLCDMode()) {
      return;
    }

    int stat = memory.read8(0xFF41);

    switch (newMode) {
      case OAM:
        stat = stat & ~(1);
        stat = stat | (1 << 1);
        if (((stat >> 5) & 1) != 0) {
          cpu.requestInterrupt(1);
        }
        break;

      case DATA:
        stat = stat | (1);
        stat = stat | (1 << 1);
        break;

      case HBLANK:
        stat = stat & ~(1);
        stat = stat & ~(1 << 1);
        if (((stat >> 3) & 1) != 0) {
          cpu.requestInterrupt(1);
        }
        break;

      case VBLANK:
        stat = stat & ~(1 << 1);
        stat = stat | (1);
        if (((stat >> 4) & 1) != 0) {
          cpu.requestInterrupt(1);
        }
        break;
    }

    memory.directWrite8(0xFF41, stat);
  }

  private void checkLYC() {
    int stat = memory.read8(0xFF41);
    int scanline = memory.read8(0xFF44);

    if (scanline == memory.read8(0xFF45)) {
      stat = stat | (1 << 2);

      if (((stat >> 6) & 1) != 0) {
        cpu.requestInterrupt(1);
      }
    } else {
      stat = stat & ~(1 << 2);
    }

    memory.directWrite8(0xFF41, stat);
  }

  private LcdMode getLCDMode() {
    int stat = memory.read8(0xFF41) & 0x3;

    switch (stat) {
      case 0:
        return LcdMode.HBLANK;
      case 1:
        return LcdMode.VBLANK;
      case 2:
        return LcdMode.OAM;
      case 3:
        return LcdMode.DATA;
      default:
        System.out.println("Uknown LCD Mode");
        return null;
    }
  }

  public void reset() {
    Arrays.fill(gfx, 0);
    lcdCycles = 0;
  }

  public void setMemory(Memory memory) {
    this.memory = memory;
  }

  public void setZ80(Z80 z80) {
    this.cpu = z80;
  }

  // The difference modes of the lcd
  enum LcdMode {
    /* Mode 0 - Takes 48.6uS(microseconds) or 201 Clock Cycles
     * 201 / 4194304hz(Gameboy Clock Speed) = 48.6uS
     */
    HBLANK(20560.3137254902 / 70224), // H-Blank 204

    /*
     * Mode 1 - Takes 1.08ms(milliseconds) or 456 Clock Cycles
     * 456 / 4194304hz(Gameboy Clock Speed) = 1.08ms | 1.08ms = 1080uS or 4560 Clock Cycles
     */
    VBLANK(919.8035087719298 / 70224),

    /*
     * Mode 2 - Takes 19uS(microseconds) or 80 Clock Cycles
     * 80 / 4194304hz(Gameboy Clock Speed) = 19uS
     */
    OAM(52428.8 / 70224),

    /*
     * Mode 3 - Takes 41uS(microseconds) or 172 Clock Cycles
     * 172 / 4194304hz(Gameboy Clock Speed) = 41uS
     */
    DATA(24385.48837209302 / 70224); // Transfer Data to LCD Driver 172

    public final int cycles;

    LcdMode(double cycles) {
      this.cycles = (int) cycles;
    }
  }
}