import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Chart extends Application {
    public void start(Stage s) {
        NumberAxis x = new NumberAxis(); NumberAxis y = new NumberAxis();
        LineChart<Number,Number> chart = new LineChart<>(x,y);
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(0,27.95));
        series.getData().add(new XYChart.Data<>(1,39.30));
        series.getData().add(new XYChart.Data<>(2,16.10));
        chart.getData().add(series);
        s.setScene(new Scene(chart,400,300));
        s.show();
    }
    public static void main(String[] args){ launch(args); }
}
