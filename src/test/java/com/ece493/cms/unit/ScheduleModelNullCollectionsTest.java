package com.ece493.cms.unit;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.ScheduleEditResult;
import com.ece493.cms.model.ScheduleGenerationResult;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleModelNullCollectionsTest {
    @Test
    void normalizesNullCollectionsToEmpty() throws Exception {
        Schedule schedule = new Schedule(1L, "admin@cms.com", Instant.now(), Instant.now(), "generated", null);

        Constructor<ScheduleGenerationResult> generationCtor = ScheduleGenerationResult.class
                .getDeclaredConstructor(int.class, String.class, String.class, String.class, java.util.List.class);
        generationCtor.setAccessible(true);
        ScheduleGenerationResult generation = generationCtor.newInstance(200, "ok", null, null, null);

        Constructor<ScheduleEditResult> editCtor = ScheduleEditResult.class
                .getDeclaredConstructor(int.class, String.class, String.class, String.class, java.util.List.class);
        editCtor.setAccessible(true);
        ScheduleEditResult edit = editCtor.newInstance(200, "ok", null, null, null);

        assertTrue(schedule.getSessions().isEmpty());
        assertTrue(generation.getMissing().isEmpty());
        assertTrue(edit.getFields().isEmpty());
    }
}
