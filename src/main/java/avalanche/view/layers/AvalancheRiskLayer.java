package avalanche.view.layers;

import avalanche.view.layers.magicalindexes.RiskProps;

public class AvalancheRiskLayer extends LayerView
{
    public AvalancheRiskLayer(final String name)
    {
        super(name);
    }

    @Override
    public int getMagicalIndex()
    {
        return RiskProps.RISK;
    }

    @Override
    public int convertToColor(final float value)
    {
        return ColorRamp.create()
                .step(0, 0, 255, 0, 255)
                .step(2, 255, 255, 0, 255)
                .step(4, 255, 0, 0, 255)
                .step(5, 127, 0, 63, 255)
                .convert(value);
    }
}
