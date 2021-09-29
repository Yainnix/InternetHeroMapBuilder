import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class WindowStartController {
    private  static Stage stage;
    private  static OnWindowStartCompletedListener windowStartCompletedListener;
    private int tileSize = 16;
    private String filePath = "";
    public static void initController(Stage stage){
        WindowStartController.stage = stage;
    }

    public static void putListener(OnWindowStartCompletedListener l){
        windowStartCompletedListener = l;
    }

    @FXML
    private TextField editTextHeight;

    @FXML
    private TextField editTextWidth;

    @FXML
    private Button buttonConfirm;

    @FXML
    private TextField editTextTilePath;

    @FXML
    private  Button buttonSetPath;

    @FXML
    private ComboBox<String> comboBoxTileSize;

    private void initComboBox(){
        ObservableList<String> tilesVariants = FXCollections.observableArrayList("16x16", "32x32", "64x64");
        comboBoxTileSize.setItems(tilesVariants);
        comboBoxTileSize.setValue("16x16");
        comboBoxTileSize.setOnAction(event ->{
            tileSize = Integer.parseInt(comboBoxTileSize.getValue().substring(0, 2));
            System.out.println(tileSize);
        });
    }

    @FXML
    void initialize(){
        editTextTilePath.setEditable(false);
        initComboBox();

        buttonSetPath.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile == null){
                // No directory
            }else{
                filePath = selectedFile.getAbsolutePath();
                editTextTilePath.setText(filePath);
            }
        });

        buttonConfirm.setOnMouseClicked(mouseEvent ->{
            int height = Integer.parseInt(editTextHeight.getText());
            int width =  Integer.parseInt(editTextWidth.getText());
            if(!filePath.isEmpty() && height != 0 && width != 0){
                windowStartCompletedListener.windowCompleted(filePath, width, height, tileSize);
                stage.close();
            }
        });
    }
}
