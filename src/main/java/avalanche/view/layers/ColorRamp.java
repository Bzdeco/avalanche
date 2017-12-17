package avalanche.view.layers;

import java.util.Map;
import java.util.TreeMap;

public class ColorRamp {
    private TreeMap<Float, Integer> ramp = new TreeMap<>();

    public static ColorRamp create() {
        return new ColorRamp();
    }

    private static int ilerp(int low, int high, float t) {
        float l = (float) low, h = (float) high;
        return Math.round(l + (h - l) * t);
    }

    public static int lerp(int low, int high, float t) {
        int a = ilerp(low >> 24, high >> 24, t);
        int r = ilerp(low >> 16 & 0xFF, high >> 16 & 0xFF, t);
        int g = ilerp(low >> 8 & 0xFF, high >> 8 & 0xFF, t);
        int b = ilerp(low & 0xFF, high & 0xFF, t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public ColorRamp step(float step, int r, int g, int b, int a) {
        final int argb = a << 24 | r << 16 | g << 8 | b;
        ramp.put(step, argb);
        return this;
    }

    public int convert(float value) {
            Map.Entry<Float, Integer> lower = ramp.headMap(value, true).lastEntry();
            Map.Entry<Float, Integer> upper = ramp.tailMap(value, true).firstEntry();

            if (lower == null) lower = ramp.firstEntry();
            if (upper == null) upper = ramp.lastEntry();

            float lower_key = lower.getKey();
            float diff = upper.getKey() - lower_key;
            float t = (diff == 0) ? 0 : (value - lower_key) / diff;

            return ColorRamp.lerp(lower.getValue(), upper.getValue(), t);
    }
}
