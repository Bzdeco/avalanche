package las2etin.tin;

import org.junit.Test;
import testutil.TestUtil;
import tinfour.common.IIncrementalTin;
import tinfour.common.Vertex;
import tinfour.standard.IncrementalTin;

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
        TINBuilder tinBuilder = new TINBuilder();

        // when
        IIncrementalTin tin = tinBuilder.buildFrom(verticesBuildingMesh);

        // then
        assertThat(tin.isBootstrapped()).isTrue();
        assertThat(tin.getVertices().size()).isLessThanOrEqualTo(numberOfVertices)
                                            .isGreaterThan(0);
    }

}