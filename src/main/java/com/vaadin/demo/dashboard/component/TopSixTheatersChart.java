package com.vaadin.demo.dashboard.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.demo.dashboard.DashboardUI;
import com.vaadin.demo.dashboard.domain.Movie;

public class TopSixTheatersChart extends Chart {

    private static Color[] colors = new Color[] { new SolidColor("#FA9E00"),
            new SolidColor("#8CB206"), new SolidColor("#519BC2"),
            new SolidColor("#FACF00"), new SolidColor("#B0DC07"),
            new SolidColor("#76BCE0") };

    public TopSixTheatersChart() {
        // TODO this don't actually visualize top six theaters, but just makes a
        // pie chart
        super(ChartType.PIE);

        setCaption("Popular Movies");
        getConfiguration().setTitle("");
        getConfiguration().getChart().setType(ChartType.PIE);
        setWidth("100%");
        setHeight("90%");

        DataSeries series = new DataSeries();

        List<Movie> movies = new ArrayList<Movie>(DashboardUI.getDataProvider()
                .getMovies());
        for (int i = 0; i < 6; i++) {
            Movie movie = movies.get(i);
            DataSeriesItem item = new DataSeriesItem(movie.getTitle(),
                    movie.getScore());
            series.add(item);
            item.setColor(colors[5 - i]);
        }
        getConfiguration().setSeries(series);

        PlotOptionsPie opts = new PlotOptionsPie();
        opts.setBorderWidth(0);
        opts.setShadow(false);
        getConfiguration().setPlotOptions(opts);
    }

}
