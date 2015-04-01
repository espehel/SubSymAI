package ea.gui;

import ea.problems.SuprisingSequence.SuprisingSequenceLoop;
import ea.core.EvolutionaryLoop;
import ea.core.Settings;
import ea.core.State;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ea.problems.lolzprefix.LOLZPrefixLoop;
import ea.problems.onemax.OneMaxLoop;



public class Controller {


    @FXML
    private LineChart<Double, Double> graph;
    LineChart.Series<Double, Double> bestSeries;
    LineChart.Series<Double, Double> averageSeries;
    LineChart.Series<Double, Double> sdSeries;
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

                sliderLabel.setText("Loopdelay="+newValue.intValue());
                Settings.LOOP_DELAY = newValue.intValue();
            }
        });

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
        EvolutionaryLoop eaLoop;

        switch (problem){
            case "One-Max" :
                eaLoop = new OneMaxLoop();
                break;
            case "LOLZ-prefix":
                eaLoop = new LOLZPrefixLoop();
                break;
            case "Surprising Sequence":
                eaLoop = new SuprisingSequenceLoop();
                break;
            default:return;
        }
        eaLoop.initialize(this);
        Task task = new Task<Void>(){

            @Override
            protected Void call()  {
                Settings.RUNNING = true;
                try{
                    eaLoop.start();

                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();

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
            System.out.println("graph: " + bestFitness);
        });
    }
    public void reset(){
        bestSeries.getData().clear();
        averageSeries.getData().clear();
        sdSeries.getData().clear();
        State.reset();
    }


}
