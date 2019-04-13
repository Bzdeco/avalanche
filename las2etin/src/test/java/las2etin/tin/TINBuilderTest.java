package las2etin.tin;

import las2etin.model.Bounds;
import org.junit.jupiter.api.Test;
import org.tinfour.common.Vertex;
import testutil.TestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TINBuilderTest
{
    @Test
    void buildFromPreparedListOfVertices()
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