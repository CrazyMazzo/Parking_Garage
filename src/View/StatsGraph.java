package View;

import Models.StatsPie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;

import java.util.ArrayList;

public class StatsGraph {

    private ObservableList _graphData;
    private PieChart _chart;
    private StatsPie _statsPie;

    public StatsGraph(StatsPie pie) {
        _statsPie = pie;
        _chart = new PieChart();
        _chart.setTitle("Statistics");

        _graphData = FXCollections.observableArrayList();
    }

    public void setData() {
        _graphData.clear();

        _graphData = FXCollections.observableArrayList(
            new PieChart.Data(_statsPie.getAdHoc().getLabel(), _statsPie.getAdHoc().getPercentage()),
            new PieChart.Data(_statsPie.getPassHolders().getLabel(), _statsPie.getPassHolders().getPercentage()),
            new PieChart.Data(_statsPie.getReservations().getLabel(), _statsPie.getReservations().getPercentage()),
            new PieChart.Data(_statsPie.getUnoccupied().getLabel(), _statsPie.getUnoccupied().getPercentage())
        );
        _chart.setData(_graphData);

        System.out.println(_statsPie.getAdHoc().getPercentage());
        System.out.println(_statsPie.getPassHolders().getPercentage());
        System.out.println(_statsPie.getReservations().getPercentage());
        System.out.println(_statsPie.getUnoccupied().getPercentage());
    }

    public void generate() {
        // !!
    }

    public PieChart getChart() {
        return _chart;
    }
}