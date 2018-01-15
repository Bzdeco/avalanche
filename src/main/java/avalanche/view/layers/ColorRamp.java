package avalanche.view.layers;

import java.awt.*;

public class ColorRamp
{
    private static final Color lowColor = new Color(0, 64, 7);
    private static final float mid = 0.45f;
    private static final Color midColor = new Color(200, 237, 63);
    private static final float high = 0.9f;
    private static final Color highColor = new Color(154, 2, 13);
    private static final Color peaksColor = new Color(255, 255, 255);

    public static Color getColorForValue(float value)
    {
        if (value >= 0f && value <= mid) {
            float innerValue = (value - 0) / 0.45f;
            return new Color(
                    clamp(lowColor.getRed() * innerValue + midColor.getRed() * (1 - innerValue)),
                    clamp(lowColor.getGreen() * innerValue + midColor.getGreen() * (1 - innerValue)),
                    clamp(lowColor.getBlue() * innerValue + midColor.getBlue() * (1 - innerValue))
            );
        }
        else if (value > mid && value <= high) {
            float innerValue = (value - mid) / 0.45f;
            return new Color(
                    clamp(midColor.getRed() * innerValue + highColor.getRed() * (1 - innerValue)),
                    clamp(midColor.getGreen() * innerValue + highColor.getGreen() * (1 - innerValue)),
                    clamp(midColor.getBlue() * innerValue + highColor.getBlue() * (1 - innerValue))
            );
        }
        else if (value > high && value <= 1f) {
            float innerValue = (value - high) / 0.1f;
            return new Color(
                    clamp(highColor.getRed() * innerValue + peaksColor.getRed() * (1 - innerValue)),
                    clamp(highColor.getGreen() * innerValue + peaksColor.getGreen() * (1 - innerValue)),
                    clamp(highColor.getBlue() * innerValue + peaksColor.getBlue() * (1 - innerValue))
            );
        }
        else
            return new Color(0, 0, 0);
    }

    private static int clamp(float value)
    {
        return (int) Math.max(0, Math.min(255, Math.floor(value)));
    }
}
