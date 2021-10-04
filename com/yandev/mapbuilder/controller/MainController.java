package com.yandev.mapbuilder.controller;

import com.yandev.mapbuilder.utils.BitmapHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
/*Use node.setCache(true) to switch on node caching.
Use node.setCacheHint(CacheHint.SPEED) to enable high speed node transforms.*/
public class MainController {

    @FXML
    private VBox mainLayout;

    @FXML
    private Menu menuFile;

    @FXML
    private MenuItem menuFileNew;

    @FXML
    private MenuItem menuFileOpen;

    @FXML
    private Menu menuFileOpenRecent;

    @FXML
    private MenuItem menuFileSave;

    @FXML
    private MenuItem menuFileSaveAs;

    @FXML
    private Menu menuEdit;

    @FXML
    private MenuItem menuEditUndo;

    @FXML
    private MenuItem menuEditRedo;

    @FXML
    private MenuItem menuEditCopy;

    @FXML
    private MenuItem menuEditPaste;

    @FXML
    private MenuItem menuEditDelete;

    @FXML
    private Text textViewCurrentCoordinates;

    @FXML
    private Tab buttonTiles;

    @FXML
    private ScrollPane scrollViewTile;

    @FXML
    private GridPane gridTileset;

    @FXML
    private Tab buttonCharacters;

    @FXML
    private ScrollPane scrollViewMap;

    @FXML
    private GridPane gridMap;

    private int rowsOfMap = 256;
    private int columnsOfMap = 256;
    private final int TILE_WINDOW_SIZE = 60;

    private final int TILE_SELECTOR_LENGTH = 4;
    private ImageView selectedTile;
    private int[][] mapValues;

    private Point selectedTileIndex = new Point(0, 0);


    private final String CLEAR_WEATHER = "clear_sky";


    @FXML
    void initialize() {

        WindowStartController.putListener((filePath, tileWidth, tileHeight, tileSize) -> {
            columnsOfMap = tileHeight;
            rowsOfMap = tileWidth;
            initTileMap();
            initTileSelector(filePath, tileSize);
            initFileButtons(tileSize);

        });


    }
    private void initFileButtons(int tileSize){
        menuFileSaveAs.setOnAction(actionEvent -> {
            try {
                JSONObject jsonMap = getJSONMap(tileSize);
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.showSaveDialog(jFileChooser.getParent());
                jFileChooser.setName("map_01.json");
                File file = jFileChooser.getSelectedFile();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(jsonMap.toString());
                    fileWriter.close();

            } catch (Exception e){
                e.printStackTrace();;
            }

        });
    }

    private JSONObject getJSONMap(int tileSize) {
        JSONObject preparedJson = new JSONObject();
        JSONObject jsonMap = new JSONObject();

        JSONArray mapData = new JSONArray();
        for (int i = 0; i < mapValues.length; i++) {
            JSONArray jsonArray = new JSONArray();
            for (int j = 0; j < mapValues[i].length; j++) {
                jsonArray.put(mapValues[i][j]);
            }
            mapData.put(jsonArray);
        }
        preparedJson.put("map_data", mapData);


        jsonMap.put("width", columnsOfMap);
        jsonMap.put("height", rowsOfMap);
        jsonMap.put("tile_size", tileSize);
        jsonMap.put("weather", CLEAR_WEATHER);

        preparedJson.put("map", jsonMap);

        return preparedJson;
    }
    private void initTileSelector(String filePath, int tileSize) {
        try {
            // Crop tile from selected path to Buffered ArrayList
            BufferedImage tileset = ImageIO.read(new File(filePath));
            BitmapHandler bitmapHandler = new BitmapHandler();
            List<BufferedImage> cropTiles = bitmapHandler.cropToTiles(tileSize, tileset, TILE_WINDOW_SIZE);

            int tileColumns = cropTiles.size() / TILE_SELECTOR_LENGTH;

            gridTileset.setPrefSize(TILE_SELECTOR_LENGTH * TILE_WINDOW_SIZE, tileColumns * TILE_WINDOW_SIZE);
            for (int i = 0; i < TILE_SELECTOR_LENGTH; i++) {
                for (int j = 0; j < tileColumns; j++) {
                    Pane cell = new Pane();
                    cell.setPrefSize(TILE_WINDOW_SIZE, TILE_WINDOW_SIZE);
                    cell.setStyle("-fx-background-color: black, white; -fx-background-insets: 0, 0 0 1 1;");

                    Image image = SwingFXUtils.toFXImage(cropTiles.get(i + j), null);
                    ImageView imageView = new ImageView(image);
                    cell.getChildren().add(imageView);
                    cell.setCache(true);
                    cell.setCacheHint(CacheHint.SPEED);

                    gridTileset.add(cell, i, j);
                }
            }


            gridTileset.setOnMouseClicked(mouseEvent -> {
                Node node = (Node)  mouseEvent.getTarget();
                selectedTile = (ImageView) node;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void initTileMap() {

        gridMap.setPrefWidth(rowsOfMap * TILE_WINDOW_SIZE);
        gridMap.setPrefHeight(columnsOfMap * TILE_WINDOW_SIZE);

        // Create gridmap with values
        for (int x = 0; x < columnsOfMap; x++) {
            for (int y = 0; y < rowsOfMap; y++) {
                Pane cell = new Pane();
                cell.setPrefSize(TILE_WINDOW_SIZE, TILE_WINDOW_SIZE);
                cell.setStyle("-fx-background-color: black, white; -fx-background-insets: 0, 0 0 1 1;");
                gridMap.add(cell, x, y);
            }
        }
        mapValues = new int[columnsOfMap][rowsOfMap];
        Arrays.stream(mapValues).forEach(a -> Arrays.fill(a, -1));

        /*gridMap.setOnMouseDragged(s -> {
            if (s.isPrimaryButtonDown()) {
                // If delta already exist
                if (mouseMoveDelta.x != 0 && mouseMoveDelta.y != 0 ) {
                    mouseMoveDelta.x = (int) (s.getX() - mouseMoveDelta.x);
                    mouseMoveDelta.y = (int) (s.getY() - mouseMoveDelta.y);
                    gridMap.setTranslateX(s.getX() - mouseMoveDelta.x);
                    gridMap.setTranslateY(s.getY() - mouseMoveDelta.y);
                }
                mouseMoveDelta.x = (int) s.getX();
                mouseMoveDelta.y = (int) s.getY();
            }
        });*/
        gridMap.setOnMouseClicked(mouseEvent -> {
            try {

                int columnOfTile = -1;
                int rowOfTile  = -1;
                Object clickedObject = mouseEvent.getTarget();
                if (clickedObject instanceof Pane && mouseEvent.getButton().name().equals("PRIMARY") && selectedTile != null) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(selectedTile.getImage());
                    columnOfTile =  GridPane.getColumnIndex(selectedTile.getParent());
                    rowOfTile = GridPane.getRowIndex(selectedTile.getParent());

                    Pane clickedPane = (Pane) clickedObject;
                    clickedPane.getChildren().add(imageView);


                } else if (clickedObject instanceof ImageView && mouseEvent.getButton().name().equals("SECONDARY")) {
                    ImageView clickedView = (ImageView) clickedObject;
                    ImageView emptyImageView = new ImageView();
                    clickedView.setImage(emptyImageView.getImage());
                }
                int column = GridPane.getColumnIndex((Node) clickedObject);
                int row = GridPane.getRowIndex((Node) clickedObject);
                textViewCurrentCoordinates.setText(String.format("x%d:y%d", row, column));



                String concatNumber = (columnOfTile + "" + rowOfTile);
                mapValues[column][row] = Integer.parseInt(concatNumber);
                System.out.printf("col:%d row:%d number:%s%n%n", column, row, concatNumber);

            } catch (Exception ignored) {
            }

        });

        gridMap.setOnScroll(scrollEvent -> {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            gridMap.setScaleX(gridMap.getScaleX() * zoomFactor);
            gridMap.setScaleY(gridMap.getScaleY() * zoomFactor);
        });

    }
}
