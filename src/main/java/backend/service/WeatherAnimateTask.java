package backend.service;

import backend.AvalancheModel;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class WeatherAnimateTask extends Task {
    private static final Logger logger = LogManager.getLogger();

    private final int start;
    private final ObservableList<ObservableList<String>> list;
    private final AvalancheModel avalancheModel;

    public WeatherAnimateTask(int start, ObservableList<ObservableList<String>> list, AvalancheModel avalancheModel) {
        this.start = start;
        this.list = list;
        this.avalancheModel = avalancheModel;
    }

    @Override
    protected Object call() throws Exception {
        int cnt = start;
        while (cnt < list.size()) {
            avalancheModel.setCurrentWeather(list.get(cnt++));
            logger.info("Current weather set to: \n" + avalancheModel.getCurrentWeather().toString());
            TimeUnit.SECONDS.sleep(1);
        }
        return null;
    }
}