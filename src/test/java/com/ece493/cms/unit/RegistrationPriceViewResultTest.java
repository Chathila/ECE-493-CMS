package com.ece493.cms.unit;

import com.ece493.cms.model.RegistrationPriceViewResult;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationPriceViewResultTest {
    @Test
    void normalizesNullPriceListToEmpty() throws Exception {
        Constructor<RegistrationPriceViewResult> ctor = RegistrationPriceViewResult.class
                .getDeclaredConstructor(int.class, String.class, java.util.List.class);
        ctor.setAccessible(true);

        RegistrationPriceViewResult result = ctor.newInstance(200, "ok", null);

        assertTrue(result.getPrices().isEmpty());
    }
}
