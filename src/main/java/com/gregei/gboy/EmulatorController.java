package com.gregei.gboy;

import com.gregei.gboy.core.Cartridge;
import com.gregei.gboy.core.Gameboy;
import com.gregei.gboy.views.MainFrame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EmulatorController {

  private final Set<KeyCode> pressed;
  private boolean emulatorRunning = false;
  private Gameboy gameboy;
  private MainFrame mainFrame;

  public EmulatorController() {
    pressed = new HashSet<>();
  }

  public void initialize(final Stage primaryStage) {
    mainFrame.getFileChooser().setTitle("Open Rom");
    mainFrame.getFileChooser().getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("GB/GBC Roms", "*.gb", "*.gbc"));
    mainFrame.getOpenItem().setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        gameboy.setCpuRunning(false);
        File file = mainFrame.getFileChooser().showOpenDialog(primaryStage);

        if (file == null) {
          //gameboy.setCpuRunning(true);
          return;
        }

        gameboy.loadGame(new Cartridge(file.toString()));
      }
    });

    mainFrame.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent e) {
        if (pressed.contains(e.getCode())) {
          return;
        }

        pressed.add(e.getCode());

        switch (e.getCode()) {

          case DOWN:
            gameboy.updateJoypad(7, true);
            break;

          case UP:
            gameboy.updateJoypad(6, true);
            break;

          case LEFT:
            gameboy.updateJoypad(5, true);
            break;

          case RIGHT:
            gameboy.updateJoypad(4, true);
            break;

          case Z:
            // Start
            gameboy.updateJoypad(3, true);
            break;

          case X:
            // Select
            gameboy.updateJoypad(2, true);
            break;

          case A:
            // B
            gameboy.updateJoypad(1, true);
            break;

          case S:
            // A
            gameboy.updateJoypad(0, true);
            break;

          case L:
            gameboy.updateJoypad(3, true);
            gameboy.updateJoypad(2, true);
            gameboy.updateJoypad(1, true);
            gameboy.updateJoypad(0, true);
            break;
        }
      }
    });

    mainFrame.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent e) {
        pressed.remove(e.getCode());

        switch (e.getCode()) {
          case DOWN:
            gameboy.updateJoypad(7, false);
            break;

          case UP:
            gameboy.updateJoypad(6, false);
            break;

          case LEFT:
            gameboy.updateJoypad(5, false);
            break;

          case RIGHT:
            gameboy.updateJoypad(4, false);
            break;

          case Z:
            // Start
            gameboy.updateJoypad(3, false);
            break;

          case X:
            // Select
            gameboy.updateJoypad(2, false);
            break;

          case A:
            // B
            gameboy.updateJoypad(1, false);
            break;

          case S:
            // A
            gameboy.updateJoypad(0, false);
            break;

          case L:
            gameboy.updateJoypad(3, false);
            gameboy.updateJoypad(2, false);
            gameboy.updateJoypad(1, false);
            gameboy.updateJoypad(0, false);
            break;
        }
      }
    });

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        gameboy.savRam();
      }
    });

    // - Passed
    //Cartridge cartridge = new Cartridge("01.gb");
    //Cartridge cartridge = new Cartridge("03.gb");
    //Cartridge cartridge = new Cartridge("04.gb");
    //Cartridge cartridge = new Cartridge("05.gb");
    //Cartridge cartridge = new Cartridge("06.gb");
    //Cartridge cartridge = new Cartridge("07.gb");
    //Cartridge cartridge = new Cartridge("08.gb");
    //Cartridge cartridge = new Cartridge("09.gb");
    //Cartridge cartridge = new Cartridge("10.gb");
    //Cartridge cartridge = new Cartridge("11.gb");
    //Cartridge cartridge = new Cartridge("bgbtest.gb");
    //Cartridge cartridge = new Cartridge("instr_timing.gb");

   // Cartridge cartridge = new Cartridge("Super Mario Land (World).gb");

  //  gameboy.loadGame(cartridge);

    start();
  }

  private void render(int[] pixels) {
    GraphicsContext graphicsContext = mainFrame.getGraphics();

    graphicsContext
        .clearRect(0, 0, mainFrame.getScene().getWidth(), mainFrame.getScene().getWidth());

    BufferedImage image = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < 144; ++y) {
      for (int x = 0; x < 160; ++x) {
        int red = pixels[(((y * 160) + x) * 3)];
        int green = pixels[(((y * 160) + x) * 3) + 1];
        int blue = pixels[(((y * 160) + x) * 3) + 2];

        java.awt.Color color = new java.awt.Color(red, green, blue, 255);

        image.setRGB(x, y, color.getRGB());
      }
    }

    BufferedImage resize = new BufferedImage(160 * 2, 144 * 2, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = resize.createGraphics();

    g.drawImage(image, 0, 0, 160 * 2, 144 * 2, null);

    graphicsContext.drawImage(SwingFXUtils.toFXImage(resize, null), 0, 0);
  }

  public void start() {
    if (emulatorRunning) {
      System.out.println("Emulator already running");
      return;
    }

    emulatorRunning = true;

    new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (!emulatorRunning) {
          System.out.println("Exiting");
          stop();
        }

        if (gameboy.getCpuRunning()) {
          gameboy.runFrame();
        }

        render(gameboy.getBuffer());
      }
    }.start();
  }

  public void stop() {
    gameboy.reset();
    emulatorRunning = false;
    gameboy.setCpuRunning(false);
  }

  public String getTitle() {
    return "Gameboy";
  }

  public void pause() {
    gameboy.setCpuRunning(!gameboy.getCpuRunning());
  }

  public void setGameboy(Gameboy gameboy) {
    this.gameboy = gameboy;
  }

  public void setMainFrame(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
  }
}