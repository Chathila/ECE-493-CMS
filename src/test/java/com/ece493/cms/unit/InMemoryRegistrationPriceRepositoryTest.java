package com.ece493.cms.unit;

import com.ece493.cms.model.RegistrationPrice;
import com.ece493.cms.service.InMemoryRegistrationPriceRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryRegistrationPriceRepositoryTest {
    @Test
    void supportsPricesAndFailureToggle() {
        InMemoryRegistrationPriceRepository repository = new InMemoryRegistrationPriceRepository();
        repository.setPrices(List.of(new RegistrationPrice(1L, "student", 100.0)));

        assertEquals(1, repository.findAll().size());

        repository.setPrices(null);
        assertEquals(0, repository.findAll().size());

        repository.setFailOnRead(true);
        assertThrows(IllegalStateException.class, repository::findAll);
    }
}
