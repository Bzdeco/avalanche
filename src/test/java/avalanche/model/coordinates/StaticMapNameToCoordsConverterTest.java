package avalanche.model.coordinates;

import org.junit.Test;

public class StaticMapNameToCoordsConverterTest
{
    @Test
    public void performanceTestIGuess() throws Exception
    {
        final long start = System.currentTimeMillis();
        final StaticMapNameToCoordsConverter staticMapNameToCoordsConverter = new StaticMapNameToCoordsConverter();
        staticMapNameToCoordsConverter.convert("nope"); //making sure JVM has to initialize map
        final long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
