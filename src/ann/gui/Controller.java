package ann.gui;

import ann.problems.ProblemSimulator;
import ann.problems.flatland.Agent;
import ann.problems.flatland.FlatlandSimulator;
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


public class Controller implements GUIController {


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
    private TextField zValue;
    @FXML
    private TextField maxGens;
    @FXML
    private TextField epsilon;
    @FXML
    private CheckBox localSequence;
    @FXML
    private TextField crossoverRate;
    @FXML
    private TextField mutationRate;
    @FXML
    private TextField kValue;

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
                imageView.setImage(new Image("resources/Pac-Man-South.png"));
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

        switch (problem){
            case "Flatland" :
                sim = new FlatlandSimulator();
                break;
            default:return;
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
        if(flatlandView) {
            gridContainer.setVisible(false);
            graph.setVisible(true);
        }
        else{
            if(sim == null)
                return;
            gridContainer.setVisible(true);
            graph.setVisible(false);
            flatlandView = !flatlandView;

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

    private void saveSettings() {
        Settings.GENOTYPE_SIZE = Integer.parseInt(genotypeSize.getText());
        Settings.CHILD_POOL_SIZE = Integer.parseInt(childPoolSize.getText());
        Settings.ADULT_POOL_SIZE = Integer.parseInt(adultPoolSize.getText());
        Settings.Z_VALUE = Integer.parseInt(zValue.getText());
        Settings.K_VALUE = Integer.parseInt(kValue.getText());
        Settings.EPSILON = Double.parseDouble(epsilon.getText());
        long maxG = Long.parseLong(maxGens.getText());
        Settings.MAX_GENERATIONS = maxG < 0 ? Long.MAX_VALUE : maxG;
        Settings.LOCAL_SEQUENCE = localSequence.isSelected();
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

    public void reset(){
        bestSeries.getData().clear();
        averageSeries.getData().clear();
        sdSeries.getData().clear();
        State.reset();
    }


}
