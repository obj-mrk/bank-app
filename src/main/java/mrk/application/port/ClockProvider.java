package mrk.application.port;

import java.time.Instant;

public interface ClockProvider {
    Instant now();
}
