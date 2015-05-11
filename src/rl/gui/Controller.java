package rl.gui;

import javafx.event.ActionEvent;
import utils.Settings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import rl.problems.flatland.FlatlandSimulator;
import utils.AbstractAgent;
import utils.Constants;
import java.io.File;


@SuppressWarnings("ALL")
public class Controller {



    @FXML
    private Button startButton;
    @FXML
    private Button testButton;
    @FXML
    private Button stepButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button stopButton;
    @FXML
    private GridPane gridContainer;
    @FXML
    private Slider loopDelaySlider;
    @FXML
    private Label sliderLabel;
    @FXML
    private TextField repetitions;
    @FXML
    private TextField exploreRate;
    @FXML
    private TextField discountRate;
    @FXML
    private TextField learningRate;


    private boolean flatlandView = false;
    ImageView[][] imageViews;
    FlatlandSimulator sim;
    public int[] lastPos = new int[]{-1,-1};
    private File scenario;

    @FXML
    protected void initialize() {

        loopDelaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                sliderLabel.setText("Delay=" + newValue.intValue());
                Settings.ea.LOOP_DELAY = newValue.intValue();
            }
        });

        imageViews = new ImageView[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                ImageView imageView = new ImageView();
                imageView.setImage(new Image("resources/Empty.png"));
                StackPane pane = new StackPane();
                pane.setPrefSize(32,32);
                pane.getChildren().add(imageView);
                pane.setMargin(imageView, new Insets(5));
                pane.setStyle("-fx-border-color: black;\n"
                        + "-fx-border-width: 1;\n"
                        + "-fx-border-style: solid;\n");
                StackPane.setAlignment(imageView, Pos.CENTER);

                imageViews[i][j] = imageView;
                gridContainer.add(pane,i,j);
            }
        }

        sim = new FlatlandSimulator();
        startButton.setDisable(true);
        testButton.setDisable(true);
        stepButton.setDisable(true);
        resetButton.setDisable(true);
    }


    @FXML
    public void trainAgent(){
        reset();
        saveSettings();

        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                try{
                    sim.train();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();

    }
    @FXML
    public void testAgent(){
        reset();
        saveSettings();

        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                try{
                    Settings.ea.RUNNING = true;
                    sim.test();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();

    }

    private Image getImage(int i){
        Image image;

        if (i == 0)
            image = new Image("resources/Empty.png");
        else if (i == -1)
            image = new Image("resources/Poison.png");
        else if (i == -2)
            image = new Image("resources/Pac-Man-East.png");
        else
            image = new Image("resources/Strawberry.png");
        return image;
    }

    public void generateGrid(int[][] content) {

        imageViews = new ImageView[content.length][content[0].length];
        gridContainer.getChildren().clear();

        for (int i = 0; i < imageViews.length; i++) {
            for (int j = 0;j < imageViews[0].length; j++) {
                ImageView imageView = new ImageView();
                imageView.setImage(getImage(content[i][j]));
                StackPane pane = new StackPane();
                //pane.setPrefSize(size,size);
                pane.getChildren().add(imageView);
                pane.setMargin(imageView, new Insets(5));
                pane.setStyle("-fx-border-color: black;\n"
                        + "-fx-border-width: 1;\n"
                        + "-fx-border-style: solid;\n");
                StackPane.setAlignment(imageView, Pos.CENTER);

                imageViews[i][j] = imageView;
                gridContainer.add(pane,i,j);
            }
        }
    }
    public void setHomeCell(int x, int y){
        imageViews[x][y].getParent().setStyle("-fx-border-color: black;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid;\n"
                + "-fx-background-color: lightskyblue;\n");

    }

    public void updateGrid(int[][] boardData) {
        Platform.runLater(() -> {

            for (int i = 0; i < boardData.length; i++) {
                for (int j = 0; j < boardData[0].length; j++) {
                    imageViews[i][j].setImage(getImage(boardData[i][j]));
                }
            }
        });
    }
    public void updateGrid(String[][] boardData) {
        Platform.runLater(() -> {

            for (int i = 0; i < boardData.length; i++) {
                for (int j = 0; j < boardData[0].length; j++) {
                    if(boardData[i][j] == "Filled")
                        continue;
                    else if(boardData[i][j] == "")
                        imageViews[i][j].setImage(new Image("resources/Empty.png"));
                    else
                        imageViews[i][j].setImage(new Image("resources/Arrow-"+boardData[i][j]+".png"));
                }
            }
        });
    }


    private void saveSettings() {

        Settings.rl.REPETITIONS = Long.parseLong(repetitions.getText());
        Settings.rl.EXPLORE_RATE = Double.parseDouble(exploreRate.getText());
        Settings.rl.LEARNING_RATE = Double.parseDouble(learningRate.getText());
        Settings.rl.DISCOUNT_RATE = Double.parseDouble(discountRate.getText());
        if(Settings.rl.EXPLORE_RATE == -1d) {
            Settings.rl.SIMULATED_ANNEALING = true;
            Settings.rl.EXPLORE_RATE = 1d;
        }

    }

    @FXML
    public void stopTest(){
        Settings.ea.RUNNING = false;
    }

    public void updateGrid(AbstractAgent agent, String[][] scenarioPolicy) {
        Platform.runLater(()->{
            if(lastPos[0] != -1){
            imageViews[lastPos[0]][lastPos[1]].setImage(getImage(scenarioPolicy[lastPos[0]][lastPos[1]]));
        }

        imageViews[agent.x][agent.y].setImage(new Image("resources/Pac-Man-" + agent.front.toString()+".png"));
        lastPos[0] = agent.x;
        lastPos[1] = agent.y;
        });
    }
    private Image getImage(String content){
        if(content=="")
            return new Image("resources/Empty.png");
        else
            return new Image("resources/Arrow-"+content+".png");

    }

    public void reset(){

    }

    public void pickScenario() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("/Users/espen/Dropbox/IntelliJ/SubSymAI/src/resources"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        scenario = fileChooser.showOpenDialog(gridContainer.getScene().getWindow());
        System.out.println(scenario);

        sim.initialize(this,scenario);
        System.out.println("initialized");
        startButton.setDisable(false);
        testButton.setDisable(false);
        stepButton.setDisable(false);
        resetButton.setDisable(false);
    }


    public void resetScenario() {
        sim.reset();
    }

    public void stepAgent() {
        sim.step();
    }
}
