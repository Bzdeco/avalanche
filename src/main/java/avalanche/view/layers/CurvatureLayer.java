package avalanche.view.layers;

import avalanche.view.layers.magicalindexes.TerrainProps;

public class CurvatureLayer extends LayerUI
{
    public CurvatureLayer(final String name)
    {
        super(name);
    }

    @Override
    public int getMagicalIndex()
    {
        return TerrainProps.PROFCURV;
    }

    @Override
    public int convertToColor(final float value)
    {
        return ColorRamp.create()
                .step(-1, 0, 0, 255, 255)
                .step(-0.01f, 0, 255, 255, 255)
                .step(0, 0, 255, 0, 255)
                .step(0.01f, 255, 255, 0, 255)
                .step(1, 255, 0, 0, 255)
                .convert(value);
    }
}
