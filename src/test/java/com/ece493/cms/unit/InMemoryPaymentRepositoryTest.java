package com.ece493.cms.unit;

import com.ece493.cms.model.Payment;
import com.ece493.cms.service.InMemoryPaymentRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryPaymentRepositoryTest {
    @Test
    void storesFindsAndFailsOnDemand() {
        InMemoryPaymentRepository repository = new InMemoryPaymentRepository();
        Payment first = repository.saveConfirmed("a@cms.com", "student", 100.0);
        Payment second = repository.saveConfirmed("b@cms.com", "author", 200.0);

        assertEquals("1", first.getPaymentId());
        assertEquals("2", second.getPaymentId());
        assertEquals(2, repository.count());
        assertTrue(repository.findById("2").isPresent());
        assertTrue(repository.findById("99").isEmpty());

        repository.setFailOnSave(true);
        assertThrows(IllegalStateException.class, () -> repository.saveConfirmed("c@cms.com", "regular", 300.0));
    }
}
