package las2etin.las;

import org.junit.Test;
import tinfour.common.Vertex;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class VertexLoaderTest
{
    /*@Test
    public void checkingFileProperties() throws Exception
    {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test.las").getFile());

        LasFileReader reader = new LasFileReader(file);
        System.out.println(reader.getCoordinateReferenceSystemOption());
        System.out.println(reader.getPointDataRecordFormat());
        System.out.println(reader.usesGeographicCoordinates());

        LasScaleAndOffset scaleAndOffset = reader.getScaleAndOffset();
        double xOffset = scaleAndOffset.xOffset;
        double xScaleFactor = scaleAndOffset.xScaleFactor;
        double yOffset = scaleAndOffset.yOffset;
        double yScaleFactor = scaleAndOffset.yScaleFactor;

        double x = reader.getMaxX() * xScaleFactor + xOffset;
        double y = reader.getMaxY() * yScaleFactor + yOffset;

        System.out.println(x);
        System.out.println(y);
        System.out.println(reader.getMaxX());
        System.out.println(reader.getMaxY());
    }*/

    @Test
    public void createVertexLoaderForExistingFile() throws Exception
    {
        // given
        File testLasFile = loadTestLasFile();

        // when
        Throwable shouldBeEmptyThrowable = catchThrowable(() -> VertexLoader.create(testLasFile));

        // then
        assertThat(shouldBeEmptyThrowable).doesNotThrowAnyException();
    }

    @Test
    public void throwExceptionWhenCreatingVertexLoaderForNonexistentFile() throws Exception
    {
        // given
        File nonexistentFile = new File("file.las");

        // when
        Throwable shouldThrowIllegalStateException = catchThrowable(() -> VertexLoader.create(nonexistentFile));

        // then
        assertThat(shouldThrowIllegalStateException).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void getAllVerticesFromFileReturnsFilledList() throws Exception
    {
        // given
        File testLasFile = loadTestLasFile();
        VertexLoader loader = VertexLoader.create(testLasFile);
        int knownNumberOfVerticesInFile = 11_558_281;

        // when
        List<Vertex> allLoadedVertices = loader.getAllVerticesFromFile();

        // then
        assertThat(allLoadedVertices.size()).isEqualTo(knownNumberOfVerticesInFile);
    }

    private File loadTestLasFile()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("test.las").getFile());
    }
}