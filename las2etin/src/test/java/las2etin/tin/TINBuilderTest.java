package las2etin.tin;

import las2etin.las.vertex.Bounds;
import org.junit.Test;
import testutil.TestUtil;
import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TINBuilderTest
{
    @Test
    public void buildFromPreparedListOfVertices() throws Exception
    {
        // given
        int numberOfVertices = 100_000;
        List<Vertex> verticesBuildingMesh = TestUtil.generateRandomVertices(numberOfVertices);
        TinBuilder tinBuilder = new TinBuilder();

        // when
        Tin tin = tinBuilder.withVertices(verticesBuildingMesh)
                            .withBounds(new Bounds(0, 0, 0, 0, 0, 0))
                            .build();

        // then
        assertThat(tin.getIncrementalTin().isBootstrapped()).isTrue();
        assertThat(tin.getIncrementalTin().getVertices().size()).isLessThanOrEqualTo(numberOfVertices).isGreaterThan(0);
    }

}