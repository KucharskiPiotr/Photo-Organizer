package sample;

import io.indico.api.utils.IndicoException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static sample.Main.apiKey;

/**
 * Class controls what is going on window and calls appropriate functions
 */
public class Controller
{
    @FXML
    private Button quit_button;

    @FXML
    private Button open_button;

    @FXML
    private Button organise_button;

    @FXML
    private ListView<String> ls;

    @FXML
    private ImageView imageView;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label label;

    private Stage stage;

    // Main model - photo organiser
    private PhotoOrganiser po;

    // Path to directory that is being organized
    private String dirPath;

    public Controller()
    {
        try
        {
            po = new PhotoOrganiser(Main.apiKey);
        } catch (IndicoException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to initialize Indico.");
            alert.showAndWait();
        }
    }

    @FXML
    private void initialize()
    {
        ls.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void openImageFromList()
    {
        System.out.println("openImageFromList");
        File file = new File(dirPath + "/" + ls.getSelectionModel().getSelectedItem());
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        makePieChart();
        System.out.println("openImageFromList: done");
    }

    public void openFileBrowser()
    {
        System.out.println("openFileBrowser");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        dirPath = "";


        if(selectedDirectory != null)
        {
            label.setText("Processing images...");
            dirPath = selectedDirectory.getAbsolutePath();
            System.out.println(dirPath);
            dirToList();
            try
            {
                po.generateImageRecognitionResults(dirPath);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (IndicoException e)
            {
                e.printStackTrace();
            }
            label.setText("Organised files will be saved in \"organised\" subfolder.");
        }

    }

    public void startOrganise()
    {
        System.out.println("startOrganise");

        try
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Operation in progress");
            alert.setHeaderText(null);
            alert.setContentText("Operation in progress...");
            alert.show();

            po.writeOrganisedToDir(dirPath, dirPath + "/organised");

            alert.close();
            alert.setTitle("Operation completed");
            alert.setHeaderText(null);
            alert.setContentText("Operation completed successfully!");
            alert.showAndWait();


        }catch (DirectoryCreationFailedException e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create new directory with organized photos. Check your permissions or disk space.");
            alert.showAndWait();
        }

        catch (IOException e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error while operating on files.");
            alert.showAndWait();
        }

    }

    public void quitProgram()
    {
        System.exit(0);
    }

    private void dirToList()
    {
        ObservableList<String> items = FXCollections.observableArrayList(po.listDir(dirPath));
        ls.setItems(items);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void makePieChart()
    {
        // Get know what index file is
        System.out.println(ls.getSelectionModel().getSelectedIndex());

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for(Map.Entry<String, Double> r : po.get_results().get(ls.getSelectionModel().getSelectedIndex()).entrySet())
        {
            pieChartData.add(new PieChart.Data(r.getKey(), r.getValue()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Image possibilities");
    }

    public void showProgramInfo()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Photo organizer \n version 1.0 \n Made by Piotr Kucharski");
        alert.showAndWait();
    }
}
