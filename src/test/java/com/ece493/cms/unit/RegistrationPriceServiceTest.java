package com.ece493.cms.unit;

import com.ece493.cms.model.RegistrationPrice;
import com.ece493.cms.model.RegistrationPriceViewResult;
import com.ece493.cms.service.InMemoryRegistrationPriceRepository;
import com.ece493.cms.service.RegistrationPriceService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationPriceServiceTest {
    @Test
    void coversSuccessUnavailableAndFailure() {
        InMemoryRegistrationPriceRepository repository = new InMemoryRegistrationPriceRepository();
        RegistrationPriceService service = new RegistrationPriceService(repository);

        RegistrationPriceViewResult unavailable = service.viewPrices();
        assertEquals(404, unavailable.getStatusCode());

        repository.setPrices(List.of(new RegistrationPrice(1L, "regular", 350.0)));
        RegistrationPriceViewResult found = service.viewPrices();
        assertEquals(200, found.getStatusCode());
        assertEquals(1, found.getPrices().size());

        repository.setFailOnRead(true);
        RegistrationPriceViewResult failed = service.viewPrices();
        assertEquals(500, failed.getStatusCode());
    }
}
