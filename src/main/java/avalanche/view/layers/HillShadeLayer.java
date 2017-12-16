package avalanche.view.layers;

import avalanche.view.layers.magicalindexes.RiskProps;

public class HillShadeLayer extends LayerUI
{
    public HillShadeLayer(final String name)
    {
        super(name);
    }

    @Override
    public int getMagicalIndex()
    {
        return RiskProps.HILLSHADE;
    }

    @Override
    public int convertToColor(final float value)
    {
        return ColorRamp.create()
                .step(1, 255, 255, 255, 255)
                .step(0, 0, 0, 0, 0)
                .convert(value);
    }
}
