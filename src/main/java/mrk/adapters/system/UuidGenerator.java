package mrk.adapters.system;

import mrk.application.port.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator implements IdGenerator {
    @Override
    public UUID next() {
        return UUID.randomUUID();
    }
}
