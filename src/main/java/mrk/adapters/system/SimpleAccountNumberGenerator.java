package mrk.adapters.system;

import mrk.application.port.AccountNumberGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimpleAccountNumberGenerator implements AccountNumberGenerator {
    @Override
    public String next() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
