package cms.views;

import java.util.List;

public class HtmlPageRenderer {
    public String homePage() {
        return """
            <!doctype html>
            <html lang=\"en\">
            <head><meta charset=\"utf-8\"><title>CMS Home</title></head>
            <body>
              <h1>Conference Management System</h1>
              <p><a href=\"/register\">Register</a> | <a href=\"/login\">Login</a></p>
            </body>
            </html>
            """;
    }

    public String registrationPage(List<String> errors) {
        StringBuilder errorHtml = new StringBuilder();
        if (errors != null && !errors.isEmpty()) {
            errorHtml.append("<div role=\"alert\"><ul>");
            for (String error : errors) {
                errorHtml.append("<li>").append(escapeHtml(error)).append("</li>");
            }
            errorHtml.append("</ul></div>");
        }

        return """
            <!doctype html>
            <html lang="en">
            <head><meta charset="utf-8"><title>Register</title></head>
            <body>
              <h1>Register User Account</h1>
              <p>Email will be used as your username for login.</p>
            """ + errorHtml + """
              <form method="post" action="/register">
                <label for="email">Email</label>
                <input id="email" type="email" name="email" required>
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required>
                <button type="submit">Create Account</button>
              </form>
              <p><a href="/">Back to home</a></p>
            </body>
            </html>
            """;
    }

    public String loginPage(String message, List<String> errors) {
        String notice = (message == null || message.isBlank()) ? "" : "<p role=\"status\">" + escapeHtml(message) + "</p>";
        StringBuilder errorHtml = new StringBuilder();
        if (errors != null && !errors.isEmpty()) {
            errorHtml.append("<div role=\"alert\"><ul>");
            for (String error : errors) {
                errorHtml.append("<li>").append(escapeHtml(error)).append("</li>");
            }
            errorHtml.append("</ul></div>");
        }

        return """
            <!doctype html>
            <html lang="en">
            <head><meta charset="utf-8"><title>Login</title></head>
            <body>
              <h1>Login</h1>
            """ + notice + """
            """ + errorHtml + """
              <p>Email will be used as your username for login.</p>
              <form method="post" action="/login">
                <label for="email">Email</label>
                <input id="email" type="email" name="email" required>
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required>
                <button type="submit">Log In</button>
              </form>
              <p><a href="/register">Register a new account</a></p>
            </body>
            </html>
            """;
    }

    public String dashboardPage(String role) {
        return """
            <!doctype html>
            <html lang="en">
            <head><meta charset="utf-8"><title>Dashboard</title></head>
            <body>
              <h1>Authorized Home Page</h1>
              <p>You are logged in with role: """ + escapeHtml(role == null ? "unknown" : role) + """
              </p>
            </body>
            </html>
            """;
    }

    private String escapeHtml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
}
