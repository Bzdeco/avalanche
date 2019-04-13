package las2etin.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
public class TerrainSettings
{
    private final int widthInCells;
    private final int heightInCells;
}
