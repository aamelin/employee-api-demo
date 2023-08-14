package company.employee.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UuidSource {

    public UUID randomUUID() {
        return UUID.randomUUID();
    }
}
