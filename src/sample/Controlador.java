package sample;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.*;

public class Controlador {

    static final List<Character> abc = new ArrayList<Character>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'Ñ', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'));

    private static Character current = abc.get(0);

    private HashMap<Character, CirculoLetra> circulos;

    @FXML
    private ProgressIndicator barraTiempoRestante;

    @FXML
    private Label labelTiempo;

    @FXML
    private Label labelRespuestaCorrecta;

    @FXML
    private StackPane root;

    private Thread thread;

    private int laps=0;
    @FXML
    void initialize() throws IOException {

        root.setStyle("-fx-background-color: darkslategray");
        int i=0;
        circulos = new HashMap<>();
        double r = Main.SCREEN.getHeight()/2*0.8;
        for (char character: abc) {
            CirculoLetra circle = new CirculoLetra(character, this);
            root.getChildren().add(circle);
            circulos.put(character,circle);
            double x = calcularCircunferenciaX(r,i);
            double y = calcularCircunferenciaY(r,i);
            circle.setTranslateX(x);
            circle.setTranslateY(-y);
            i++;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while (Integer.parseInt(labelTiempo.getText())>0 && laps<2){
                    Thread.sleep(1000);
                    Platform.runLater(()-> {
                        labelTiempo.setText(((Integer.parseInt(labelTiempo.getText()) - 1)>-1?
                                (Integer.parseInt(labelTiempo.getText()) - 1):0)+ "");
                        barraTiempoRestante.setProgress((180D-Integer.parseInt(labelTiempo.getText()))/180);
                        }
                    );
                }
                return null;
            }
        };
        thread = new Thread(task);
        thread.start();

        circulos.get(current).setCurrentStatus(CirculoLetra.STATUS.ACTIVA);

        root.setOnKeyPressed(e->{
            if (e.getCode()== KeyCode.Z){
                selectNextCircle(CirculoLetra.STATUS.INCORRECTA);
            }
            if (e.getCode()== KeyCode.X){
                selectNextCircle(CirculoLetra.STATUS.CORRECTA);
            }
            if (e.getCode()== KeyCode.C){
                selectNextCircle(CirculoLetra.STATUS.PENDIENTE);
            }
        });
    }



    private double calcularCircunferenciaX(double r, int i){
        double x=0;
        x = r*Math.sin(2*Math.PI/(abc.size())*(i));
        return x;
    }
    private double calcularCircunferenciaY(double r, int i){
        double y=0;
        y = r*Math.cos(2*Math.PI/(abc.size())*(i));
        return y;
    }

    public CirculoLetra getCircle(char character){
        return circulos.get(character);
    }

    void updatePuntaje(){
        int correctas = 0;
        for(Map.Entry<Character, CirculoLetra> entry : circulos.entrySet()) {
            CirculoLetra circulo = entry.getValue();
            if(circulo.getStatus()== CirculoLetra.STATUS.CORRECTA){
                correctas++;
            }
        }
        labelRespuestaCorrecta.setText(correctas+"");
    }


    void selectNextCircle(CirculoLetra.STATUS newStatus){
        if(current!=null) {
            circulos.get(current).setCurrentStatus(newStatus);
            int index = abc.indexOf(current);
            index++;
            if (index == abc.size()) index = 0;
            current = abc.get(index);
            laps = 0;
            while (circulos.get(current).getStatus() != CirculoLetra.STATUS.ACTIVA && laps < 2) {
                CirculoLetra.STATUS estado = circulos.get(current).getStatus();
                if (estado != CirculoLetra.STATUS.CORRECTA && estado != CirculoLetra.STATUS.INCORRECTA) {
                    circulos.get(current).setCurrentStatus(CirculoLetra.STATUS.ACTIVA);
                } else {
                    index++;
                    if (index == abc.size()) {
                        index = 0;
                        laps++;
                    }
                    current = abc.get(index);
                }
            }
            if (laps >= 2) {
                current = null;
            }
            updatePuntaje();
        }
    }
    @FXML
    void correct(ActionEvent event) {
        selectNextCircle(CirculoLetra.STATUS.CORRECTA);
    }

    @FXML
    void incorrect(ActionEvent event) {
        selectNextCircle(CirculoLetra.STATUS.INCORRECTA);
    }

    @FXML
    void pause(ActionEvent event) {
        selectNextCircle(CirculoLetra.STATUS.INICIAL);
    }

    @FXML
    void skip(ActionEvent event) {
        selectNextCircle(CirculoLetra.STATUS.PENDIENTE);
    }
}

class CirculoLetra extends Group {
    enum STATUS {PENDIENTE, CORRECTA, INCORRECTA, INICIAL, ACTIVA}

    private Property<STATUS> currentStatus = new SimpleObjectProperty<>();
    private Circle circle;
    private byte odd = 0;

    CirculoLetra(char character, Controlador controlador) {
        String caracter = String.valueOf(character);
        StackPane root = new StackPane();
        Label text = new Label(caracter);
        circle = new Circle((35D*Main.SCREEN.getHeight())/900);
        Circle circleBorder = new Circle(36D*Main.SCREEN.getHeight()/900);
        circleBorder.setFill(Color.WHITE);
        text.setStyle("-fx-text-fill: whitesmoke;" +
                "-fx-font-weight: bolder;" +
                "-fx-font-size: "+(32D*Main.SCREEN.getHeight()/900)+"px;" +
                "-fx-font-family: Arial");
        setCurrentStatus(STATUS.INICIAL);

        root.getChildren().addAll(circleBorder,circle,text);
        getChildren().add(root);

        circle.setOnMouseClicked(e->{
            switch(currentStatus.getValue()){
                case PENDIENTE: setCurrentStatus(STATUS.INICIAL);break;
                case INICIAL: setCurrentStatus(STATUS.ACTIVA);break;
                case ACTIVA: setCurrentStatus(STATUS.CORRECTA);break;
                case CORRECTA: setCurrentStatus(STATUS.INCORRECTA);break;
                default: setCurrentStatus(STATUS.PENDIENTE);
            }
        });
    }

    public STATUS getStatus(){
        return currentStatus.getValue();

    }

    public void setCurrentStatus(STATUS status) {
        currentStatus.setValue(status);
        fillCircle();
    }

    Thread thread;
    private void fillCircle(){
        if (currentStatus.getValue()==STATUS.ACTIVA){
            if(odd%2==0) {
                Stop[] stops = new Stop[]{new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.BLUE)};
                LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                circle.setFill(lg);
                odd++;
            } else {
                Stop[] stops = new Stop[] { new Stop(0, Color.BLUE), new Stop(1, Color.DARKBLUE)};
                LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                circle.setFill(lg);
                odd=0;
            }
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);
                    fillCircle();
                    return null;
                }
            };
            thread = new Thread(task);
            thread.start();
        }
        else if (currentStatus.getValue()==STATUS.INICIAL){
            Stop[] stops = new Stop[] { new Stop(0, Color.BLUE), new Stop(1, Color.DARKBLUE)};
            LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            circle.setFill(lg);
        }
        else if (currentStatus.getValue()==STATUS.PENDIENTE){
            Stop[] stops = new Stop[] { new Stop(0, new Color(1,139D/255,0,1)), new Stop(1, new Color(130D/255,67D/255,0,1))};
            LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            circle.setFill(lg);
        }
        else if (currentStatus.getValue()==STATUS.CORRECTA){
            Stop[] stops = new Stop[] { new Stop(0, new Color(0,255D/255,0,1)), new Stop(1, new Color(0,130D/255,0,1))};
            LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            circle.setFill(lg);
        }
        else{
            Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(1, Color.DARKRED)};
            LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            circle.setFill(lg);
        }
    }

}