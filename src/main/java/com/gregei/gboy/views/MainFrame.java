package com.gregei.gboy.views;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class MainFrame {

  private final Scene scene;
  private final GraphicsContext graphics;
  private final Canvas canvas;
  private final MenuItem openItem;
  private final FileChooser fileChooser;

  public MainFrame() {
    BorderPane root = new BorderPane();

    scene = new Scene(root, Color.RED);

    canvas = new Canvas(160 * 2, 144 * 2);

    fileChooser = new FileChooser();
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("File");
    openItem = new MenuItem("Open");

    fileMenu.getItems().add(openItem);
    menuBar.getMenus().add(fileMenu);

    root.setTop(menuBar);
    root.setCenter(canvas);

    graphics = canvas.getGraphicsContext2D();
  }

  public Scene getScene() {
    return scene;
  }

  public GraphicsContext getGraphics() {
    return graphics;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public FileChooser getFileChooser() {
    return fileChooser;
  }

  public MenuItem getOpenItem() {
    return openItem;
  }
}
