package las2etin.las.vertex;

import las2etin.las.vertex.ThinningVertexFilter;
import las2etin.las.vertex.VertexFilter;
import org.junit.Test;
import tinfour.common.Vertex;

import java.util.Random;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ThinningVertexFilterTest
{
    @Test
    public void shouldAcceptVertexWithGeneratedValueLowerThanThreshold() throws Exception
    {
        // given
        Vertex vertex = new Vertex(1.0, 1.0, 1.0);
        float thinningFactor = 0.7f;
        float generatedRandomFloat = 0.654f;
        Random generatorStub = mock(Random.class, withSettings().stubOnly());
        when(generatorStub.nextFloat()).thenReturn(generatedRandomFloat);
        VertexFilter filter = new ThinningVertexFilter(thinningFactor, generatorStub);

        // when
        boolean isAccepted = filter.accept(vertex);

        // then
        assertThat(isAccepted).isTrue();
    }

    @Test
    public void shouldAcceptVertexWithGeneratedValueEqualToThreshold() throws Exception
    {
        // given
        Vertex vertex = new Vertex(1.0, 1.0, 1.0);
        float threshold = 0.4f;
        Random generatorStub = mock(Random.class, withSettings().stubOnly());
        when(generatorStub.nextFloat()).thenReturn(threshold);
        VertexFilter filter = new ThinningVertexFilter(threshold, generatorStub);

        // when
        boolean isAccepted = filter.accept(vertex);

        // then
        assertThat(isAccepted).isTrue();
    }

    @Test
    public void shouldNotAcceptVertexWithGeneratedValueGreaterThanThreshold() throws Exception
    {
        // given
        Vertex vertex = new Vertex(1.0, 1.0, 1.0);
        float thinningFactor = 0.55f;
        float generatedRandomFloat = 0.654f;
        Random generatorStub = mock(Random.class, withSettings().stubOnly());
        when(generatorStub.nextFloat()).thenReturn(generatedRandomFloat);
        VertexFilter filter = new ThinningVertexFilter(thinningFactor, generatorStub);

        // when
        boolean isAccepted = filter.accept(vertex);

        // then
        assertThat(isAccepted).isFalse();
    }
}