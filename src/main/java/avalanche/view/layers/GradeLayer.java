package avalanche.view.layers;

import avalanche.view.layers.magicalindexes.TerrainProps;

public class GradeLayer extends LayerUI
{
    public GradeLayer(final String name)
    {
        super(name);
    }

    @Override
    public int getMagicalIndex()
    {
        return TerrainProps.GRADE;
    }

    @Override
    public int convertToColor(final float value)
    {
        return ColorRamp.create()
                .step(-(float) Math.PI, 0, 0, 255, 255)
                .step((float) Math.PI, 255, 0, 0, 255)
                .convert(value);
    }
}
