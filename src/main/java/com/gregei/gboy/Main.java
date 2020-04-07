package com.gregei.gboy;

import com.gregei.gboy.core.Gameboy;
import com.gregei.gboy.views.MainFrame;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Gameboy gameboy = new Gameboy();
    MainFrame mainFrame = new MainFrame();
    EmulatorController emulator = new EmulatorController();

    emulator.setGameboy(gameboy);
    emulator.setMainFrame(mainFrame);
    emulator.initialize(primaryStage);

    primaryStage.setTitle(emulator.getTitle());
    primaryStage.setScene(mainFrame.getScene());
    primaryStage.show();
  }
}