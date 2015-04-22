package ann.gui;

import ann.problems.ProblemSimulator;
import ann.problems.flatland.Agent;
import ann.problems.flatland.FlatlandSimulator;
import ann.problems.tracker.BlockObject;
import ann.problems.tracker.FallingBlock;
import ann.problems.tracker.TrackerBlock;
import ann.problems.tracker.TrackerSimulator;
import ea.core.Settings;
import ea.core.State;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import utils.Constants;
import utils.GUIController;

import java.util.Arrays;


public class Controller implements GUIController {

    private static final String WHITE = "-fx-background-color: WHITE;\n";
    private static final String BLUE = "-fx-background-color: BLUE;\n";
    private static final String GREEN = "-fx-background-color: GREEN;\n";
    private static final String RED = "-fx-background-color: RED;\n";
    private static final String BORDER = "-fx-border-color: black;\n"
                                        + "-fx-border-width: 1;\n"
                                        + "-fx-border-style: solid;\n";


    @FXML
    private LineChart<Double, Double> graph;
    LineChart.Series<Double, Double> bestSeries;
    LineChart.Series<Double, Double> averageSeries;
    LineChart.Series<Double, Double> sdSeries;
    @FXML
    private GridPane gridContainer;
    @FXML
    private ToggleGroup parentSelectToggler;
    @FXML
    private RadioButton sigmaScaleToggle;
    @FXML
    private RadioButton tournamentToggle;
    @FXML
    private RadioButton boltzmannToggle;
    @FXML
    private RadioButton fitnessPropToggle;
    @FXML
    private RadioButton fullRepToggle;
    @FXML
    private RadioButton overProdToggle;
    @FXML
    private RadioButton mixedToggle;
    @FXML
    private ComboBox<String> problemCombo;
    @FXML
    private Slider loopDelaySlider;
    @FXML
    private Label sliderLabel;
    @FXML
    private TextField childPoolSize;
    @FXML
    private TextField adultPoolSize;
    @FXML
    private TextField genotypeSize;
    @FXML
    private TextField seriesCount;
    @FXML
    private TextField maxGens;
    @FXML
    private TextField crashPenalty;
    @FXML
    private CheckBox dynamic;
    @FXML
    private TextField crossoverRate;
    @FXML
    private TextField mutationRate;
    @FXML
    private TextField poisonPenalty;
    @FXML
    private Button runDynamicButton;
    @FXML
    private Button runLastButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;

    private boolean flatlandView = false;
    ImageView[][] imageViews;
    ProblemSimulator sim;
    int[] lastPos = new int[]{-1,-1};


    @FXML
    protected void initialize() {
        ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();

        bestSeries = new LineChart.Series<>();
        bestSeries.setName("Best fitness");
        lineChartData.add(bestSeries);

        averageSeries = new LineChart.Series<>();
        averageSeries.setName("Average fitness");
        lineChartData.add(averageSeries);

        sdSeries = new LineChart.Series<>();
        sdSeries.setName("Standard Deviation");
        lineChartData.add(sdSeries);

        graph.setData(lineChartData);
        graph.createSymbolsProperty();

        loopDelaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                sliderLabel.setText("Loopdelay=" + newValue.intValue());
                Settings.LOOP_DELAY = newValue.intValue();
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
    }

    @FXML
    public void onParentSelectionAction(){
        Settings.PARENT_SELECTION_FITNESSPROPORTIONATE = fitnessPropToggle.isSelected();
        Settings.PARENT_SELECTION_BOLTZMANN = boltzmannToggle.isSelected();
        Settings.PARENT_SELECTION_SIGMASCALING = sigmaScaleToggle.isSelected();
        Settings.PARENT_SELECTION_TOURNAMENT = tournamentToggle.isSelected();
    }
    @FXML
    public void onAdultSelectionAction(){
        Settings.ADULT_SELECTION_FULLREPLACEMENT = fullRepToggle.isSelected();
        Settings.ADULT_SELECTION_GENERATIONMIXING = mixedToggle.isSelected();
        Settings.ADULT_SELECTION_OVERPRODUCTION = overProdToggle.isSelected();

    }
    @FXML
    public void startEALoop(){
        reset();
        saveSettings();
        String problem = problemCombo.getSelectionModel().getSelectedItem();
        System.out.println(problem);
        switch (problem){
            case "Flatland" :
                sim = new FlatlandSimulator();
                break;
            case "Tracker" :
                sim = new TrackerSimulator();
                break;
            default:throw new IllegalStateException();
        }
        sim.initialize(this);
        System.out.println("initialized");
        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                Settings.RUNNING = true;
                try{
                    sim.start();

                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();

    }
    @FXML
    public void toggleVisual(){
        //graph view
        if(flatlandView) {
            gridContainer.setVisible(false);
            graph.setVisible(true);
            runDynamicButton.setVisible(false);
            runLastButton.setVisible(false);
            startButton.setVisible(true);
            stopButton.setVisible(true);

        }
        //grid view
        else{
            /*if(sim == null)
                return;*/
            generateGrid();
            clearGrid();
            gridContainer.setVisible(true);
            graph.setVisible(false);
            runDynamicButton.setVisible(true);
            runLastButton.setVisible(true);
            startButton.setVisible(false);
            stopButton.setVisible(false);

        }

        flatlandView = !flatlandView;
    }

    private void generateGrid() {

        int height = 0;
        int width = 0;
        String image = "";
        String problem = problemCombo.getSelectionModel().getSelectedItem();

        if(problem.equals("Flatland")){
            height = 10;
            width = 10;
            image = "resources/Empty.png";
        }
        else if(problem.equals("Tracker")){
            height = 15;
            width = 30;
            image = "resources/Empty10x10.png";
        }




        imageViews = new ImageView[width][height];
        gridContainer.getChildren().clear();

        for (int i = 0; i < width; i++) {
            for (int j = 0;j < height; j++) {
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(image));
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

    public void updateGrid(int[][] boardData) {
        Platform.runLater(()->{

            for (int i = 0; i < boardData.length; i++) {
                for (int j = 0; j < boardData[0].length; j++) {
                    Image image;
                    if(boardData[i][j] == Constants.FLATLAND_CELLTYPE_EMPTY)
                         image = new Image("resources/Empty.png");
                    else if(boardData[i][j] == Constants.FLATLAND_CELLTYPE_POISON)
                        image = new Image("resources/Poison.png");
                    else
                        image = new Image("resources/Strawberry.png");

                    imageViews[i][j].setImage(image);
                }
        }
    });

}

    @Override
    public void updateGrid(TrackerBlock tracker, FallingBlock fallingBlock) {

        Platform.runLater(()-> {
            clearGrid();
            String trackerColor = tracker.polled ? GREEN : RED;
            fillGrid(tracker,BORDER+trackerColor);
            fillGrid(fallingBlock,BORDER+BLUE);
        });
    }

    private void fillGrid(BlockObject blockObject, String color){
        for (int i = blockObject.x; i < blockObject.x+blockObject.size; i++) {
            if(i < TrackerSimulator.width)
                imageViews[i][blockObject.y].getParent().setStyle(color);
            else
                imageViews[i - TrackerSimulator.width][blockObject.y].getParent().setStyle(color);
        }
    }
    private void fillGrid(int[] blockObject, String color){
        System.out.println(Arrays.toString(blockObject));
        for (int i = blockObject[0]; i < blockObject[2]; i++) {
            if(i < TrackerSimulator.width)
                imageViews[i][blockObject[1]].getParent().setStyle(color);
            else
                imageViews[i - TrackerSimulator.width][blockObject[1]].getParent().setStyle(color);
        }
    }

    private void clearGrid(){
        for (int i = 0; i < imageViews.length; i++) {
            for (int j = 0; j < imageViews[i].length; j++) {
                imageViews[i][j].getParent().setStyle(BORDER+WHITE);
            }
        }
    }

    private void saveSettings() {
        Settings.GENOTYPE_SIZE = Integer.parseInt(genotypeSize.getText());
        Settings.CHILD_POOL_SIZE = Integer.parseInt(childPoolSize.getText());
        Settings.ADULT_POOL_SIZE = Integer.parseInt(adultPoolSize.getText());
        ann.core.Settings.SERIES_COUNT = Integer.parseInt(seriesCount.getText());
        ann.core.Settings.POISON_PENALTY = Integer.parseInt(poisonPenalty.getText());
        ann.core.Settings.CRASH_PENALTY = Double.parseDouble(crashPenalty.getText());
        long maxG = Long.parseLong(maxGens.getText());
        Settings.MAX_GENERATIONS = maxG < 0 ? Long.MAX_VALUE : maxG;
        ann.core.Settings.DYNAMIC = dynamic.isSelected();
        Settings.MUTATION_RATE = Double.parseDouble(mutationRate.getText());
        Settings.CROSSOVER_RATE = Double.parseDouble(crossoverRate.getText());

    }

    @FXML
    public void stopEALoop(){
        Settings.RUNNING = false;
    }

    public void updateGraph(double bestFitness){
        Platform.runLater(()->{
            bestSeries.getData().add(new XYChart.Data<>((double)State.generationNumber, bestFitness));
            averageSeries.getData().add(new XYChart.Data<>((double)State.generationNumber, State.averageFitness));
            sdSeries.getData().add(new XYChart.Data<>((double) State.generationNumber, State.standardDeviation));
            //System.out.println("graph: " + bestFitness);
        });
    }

    @Override
    public void updateGrid(Agent agent) {
        Platform.runLater(()->{
            if(lastPos[0] != -1){
            imageViews[lastPos[0]][lastPos[1]].setImage(new Image("resources/Empty.png"));
        }

        imageViews[agent.x][agent.y].setImage(new Image("resources/Pac-Man-" + agent.front.toString()+".png"));
        lastPos[0] = agent.x;
        lastPos[1] = agent.y;
        });
    }
    @FXML
    public void runDynamic(){
        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                Settings.RUNNING = true;
                try{
                    sim.generateNewContent();
                    sim.runBestAgent();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    @FXML
    public void runLast(){
        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                Settings.RUNNING = true;
                try{
                    sim.runBestAgent();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void reset(){
        bestSeries.getData().clear();
        averageSeries.getData().clear();
        sdSeries.getData().clear();
        State.reset();
    }


}
