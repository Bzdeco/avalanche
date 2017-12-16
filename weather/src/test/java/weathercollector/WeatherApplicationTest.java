package weathercollector;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class WeatherApplicationTest
{
    @Test
    public void shouldStartWithNoExceptions() throws Exception
    {
        //when
        final Throwable throwable = catchThrowable(() -> WeatherApplication.main(new String[]{}));
        //then
        assertThat(throwable).doesNotThrowAnyException();
    }
}
