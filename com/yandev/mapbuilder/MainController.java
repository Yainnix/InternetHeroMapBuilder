
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {
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

    @FXML
    void initialize() {
        WindowStartController.putListener((filePath, tileWidth, tileHeight, tileSize) -> {
            columnsOfMap = tileHeight;
            rowsOfMap = tileWidth;
            initTileMap();
            initTileSelector(filePath, tileSize);

        });
    }
    private void initTileSelector(String filePath, int tileSize){
        try {
            // Crop tile from selected path to Buffered ArrayList
            BufferedImage tileset = ImageIO.read(new File(filePath));
            BitmapHandler bitmapHandler = new BitmapHandler();
            List<BufferedImage> cropTiles = bitmapHandler.cropToTiles(tileSize, tileset, TILE_WINDOW_SIZE);

            int tileColumns = cropTiles.size()/TILE_SELECTOR_LENGTH;

            gridTileset.setPrefSize(TILE_SELECTOR_LENGTH*TILE_WINDOW_SIZE, tileColumns*TILE_WINDOW_SIZE);
            for(int i = 0; i < TILE_SELECTOR_LENGTH; i++){
                for(int j = 0; j < tileColumns; j++){
                     Pane cell = new Pane();
                     cell.setPrefSize(TILE_WINDOW_SIZE, TILE_WINDOW_SIZE);
                     cell.setStyle("-fx-background-color: black, white; -fx-background-insets: 0, 0 0 1 1;");

                     Image image =  SwingFXUtils.toFXImage(cropTiles.get(i+j), null);
                     ImageView imageView = new ImageView(image);
                     cell.getChildren().add(imageView);

                     gridTileset.add(cell, i, j);
                }
            }

            gridTileset.setOnMouseClicked(mouseEvent -> {
               selectedTile =  (ImageView) mouseEvent.getTarget();
                System.out.println("Imageview selected " + selectedTile);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void initTileMap(){
        gridMap.setPrefWidth(rowsOfMap* TILE_WINDOW_SIZE);
        gridMap.setPrefHeight(columnsOfMap* TILE_WINDOW_SIZE);

        // Create gridmap with values
        for(int x = 0; x < columnsOfMap; x++){
            for(int y = 0; y < rowsOfMap; y++){
                Pane cell = new Pane();
                cell.setPrefSize(TILE_WINDOW_SIZE, TILE_WINDOW_SIZE);
                cell.setStyle("-fx-background-color: black, white; -fx-background-insets: 0, 0 0 1 1;");
                gridMap.add(cell, x, y);
            }
        }
        gridMap.setOnMouseClicked(mouseEvent -> {
            Object clickedObject =  mouseEvent.getTarget();
            if(clickedObject instanceof Pane && mouseEvent.getButton().name().equals("PRIMARY") && selectedTile != null){
                ImageView imageView = new ImageView();
                imageView.setImage(selectedTile.getImage());
                Pane clickedPane = (Pane) clickedObject;
                clickedPane.getChildren().add(imageView);
            } else if(clickedObject instanceof ImageView && mouseEvent.getButton().name().equals("SECONDARY")) {
                ImageView clickedView = (ImageView) clickedObject;
                ImageView emptyImageView = new ImageView();
                clickedView.setImage(emptyImageView.getImage());
                }
            int column = GridPane.getColumnIndex((Node) clickedObject);
            int row = GridPane.getRowIndex((Node) clickedObject);
            textViewCurrentCoordinates.setText(String.format("x%d:y%d", row, column));
        });

        gridMap.setOnScroll(scrollEvent -> {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0){
                zoomFactor = 2.0 - zoomFactor;
            }
            gridMap.setScaleX(gridMap.getScaleX() * zoomFactor);
            gridMap.setScaleY(gridMap.getScaleY() * zoomFactor);
        });

    }

}
