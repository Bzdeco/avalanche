package las2etin.tin;

import las2etin.model.Bounds;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.tinfour.common.IIncrementalTin;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
public class Tin
{
    private final IIncrementalTin incrementalTin;
    private final Bounds bounds;
}
