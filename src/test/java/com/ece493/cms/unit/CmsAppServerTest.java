package com.ece493.cms.unit;

import com.ece493.cms.controller.CmsAppServer;
import com.ece493.cms.db.Db;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CmsAppServerTest {
    @Test
    void seedDefaultTestUserInsertsThenSkipsWhenAlreadyPresent() throws Exception {
        DataSource dataSource = Db.createDataSource("jdbc:h2:mem:seed_default_user;DB_CLOSE_DELAY=-1");
        Db.runSchema(dataSource);
        JdbcUserAccountRepository repository = new JdbcUserAccountRepository(dataSource);

        Method seedMethod = CmsAppServer.class.getDeclaredMethod("seedDefaultTestUser", DataSource.class);
        seedMethod.setAccessible(true);

        seedMethod.invoke(null, dataSource);

        assertEquals(1L, repository.countByEmail("admin@user.com"));
        Optional<UserAccount> seeded = repository.findByEmail("admin@user.com");
        assertTrue(seeded.isPresent());
        assertEquals("Admin User", seeded.get().getFullName());
        assertTrue(new PasswordHasher().matches("monkey", seeded.get().getPasswordSalt(), seeded.get().getPasswordHash()));

        seedMethod.invoke(null, dataSource);

        assertEquals(1L, repository.countByEmail("admin@user.com"));
    }
}
