package com.ece493.cms.unit;

import com.ece493.cms.controller.RegistrationServlet;
import com.ece493.cms.service.RegistrationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNull;

class RegistrationServletTest {
    @Test
    void readFieldReturnsNullWhenBodyIsNull() throws Exception {
        RegistrationService service = (email, password) -> null;
        RegistrationServlet servlet = new RegistrationServlet(service, "<html></html>");

        Method readField = RegistrationServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, null, Pattern.compile("email"));

        assertNull(value);
    }
}
