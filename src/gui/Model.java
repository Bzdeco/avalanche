package gui;

import javafx.scene.paint.Color;
import layers.GridLayer;
import layers.VectorLayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Model {
    private LinkedHashMap<String, Layer> layers = new LinkedHashMap<String, Layer>();

    public Model() {
        Layer terrain = new GridLayer("Teren", Color.GREEN);
        terrain.setVisible(true);
        registerLayer("terrain", terrain);

        Layer risk = new GridLayer("Ryzyko lawinowe", Color.RED);
        risk.setVisible(true);
        registerLayer("avalancheRisk", risk);

        registerLayer("groundTemperature", new GridLayer("Temperatura gruntu", Color.BLUE));
        registerLayer("snowDepth", new GridLayer("Grubość pokrywy śnieżnej", Color.BLUE));
        registerLayer("windSpeed", new VectorLayer("Prędkość wiatru", Color.BLUE));
        registerLayer("snowfall", new GridLayer("Opady", Color.BLUE));
    }

    public void registerLayer(String layerId, Layer layer) {
        layers.put(layerId, layer);
    }
    public void toggleLayer(String layerId, Boolean state) {
        ((Layer)layers.get(layerId)).setVisible(state);
    }
    public Set<Map.Entry<String, Layer>> getLayers() { return layers.entrySet(); }
}
