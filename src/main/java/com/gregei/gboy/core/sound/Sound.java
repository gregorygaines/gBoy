package com.gregei.gboy.core.sound;


import com.gregei.gboy.core.Memory;

public class Sound {

  public Sound() {
    SquareWave squareWave1 = new SquareWave();
    SquareWave squareWave2 = new SquareWave();
    WaveTable waveTable = new WaveTable();
    NoiseGenerator noiseGenerator = new NoiseGenerator();
  }

  public void updateSound(int cycles) {

  }

  public void setMemory(Memory memory) {
  }

}
