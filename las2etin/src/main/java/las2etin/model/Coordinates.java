package las2etin.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Coordinates implements Serializable
{
    private static final long serialVersionUID = 4596453708352625490L;

    private final int x;
    private final int y;
}
