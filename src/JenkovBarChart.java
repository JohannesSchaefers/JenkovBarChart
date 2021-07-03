import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPort;
import jssc.SerialPortException;

public class JenkovBarChart extends Application {
    final static String austria = "Austria";

    @Override public void start(Stage stage) {

        SerialPort serialPort = new SerialPort("/dev/tty.usbmodem80181401");

        stage.setTitle("Bar Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final BarChart<Number,String> bc =
                new BarChart<Number,String>(xAxis,yAxis);
        bc.setTitle("Spannung");
        xAxis.setLabel("Value");

        xAxis.setTickLabelRotation(0);
        yAxis.setLabel("Country");
        
        xAxis.setAutoRanging( false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(6);
        xAxis.setTickUnit(0.1);


        XYChart.Series series1 = new XYChart.Series();
        series1.setName("2003");

        series1.getData().add(new XYChart.Data(1.34, austria));

        Scene scene  = new Scene(bc,1300,200);
        bc.getData().addAll(series1);
        stage.setScene(scene);
        stage.show();

        try {

            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
                } catch (SerialPortException e) {
            e.printStackTrace();
        }

        final String[] aString = {""};

        Timeline tl = new Timeline();
        tl.getKeyFrames().add(
                new KeyFrame(Duration.millis( 500),
                        actionEvent -> {

                            try {
                                if (!serialPort.isOpened())
                                {
                                serialPort.openPort();
                                serialPort.setParams(SerialPort.BAUDRATE_9600,
                                        SerialPort.DATABITS_8,
                                        SerialPort.STOPBITS_1,
                                        SerialPort.PARITY_NONE);
                                }

                                aString[0] = serialPort.readString();

                                aString[0] = aString[0].substring( 0,4);

                            } catch (SerialPortException e) {
                                e.printStackTrace();
                            }

                            for ( XYChart.Series<Number, String> series : bc.getData()) {
                                for ( XYChart.Data<Number, String> data : series.getData()) {

                                    data.setXValue( Double.parseDouble( aString[0]));

                                    System.out.println(aString[0] + " Volt");
                                }
                            }
                        }
                ));

        tl.setCycleCount(540);
        tl.setAutoReverse(true);
        tl.play();

        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}