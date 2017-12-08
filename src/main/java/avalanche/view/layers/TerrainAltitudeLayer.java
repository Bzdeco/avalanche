package avalanche.view.layers;

import avalanche.view.layers.magicalindexes.TerrainProps;

public class TerrainAltitudeLayer extends LayerView
{
    public TerrainAltitudeLayer(final String name)
    {
        super(name);
    }

    @Override
    public int getMagicalIndex()
    {
        return TerrainProps.ALTITUDE;
    }

    @Override
    public int convertToColor(final float value)
    {
        return ColorRamp.create()
                .step(4000, 255, 255, 255, 255)
                .step(2800, 110, 110, 110, 255)
                .step(1700, 158, 0, 0, 255)
                .step(1200, 161, 67, 0, 255)
                .step(500, 232, 215, 125, 255)
                .step(50, 16, 122, 47, 255)
                .step(0, 0, 97, 71, 255)
                .convert(value);
    }
}
