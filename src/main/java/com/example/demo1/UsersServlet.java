package com.example.demo1;

import com.example.demo1.user.User;
import com.example.demo1.user.UserRepository;
import com.example.demo1.user.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.extractor.Html;
import com.example.demo1.util.response.DispatcherResponse;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Response;
import jakarta.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "usersServlet", value = "/users_servlet")
public class UsersServlet extends Controller {
    private static final Logger logger = LogManager.getLogger(UsersServlet.class);

    private UserService userService;

    @Override
    protected void initController() throws Exception {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    public IntoResponse get(RequestContext ctx) throws IOException {
        List<User> users;

        try {
            users = userService.getAll();
        } catch (SQLException e) {
            return new DispatcherResponse("pages/internal_server_error.jsp");
        }

        StringBuilder out = new StringBuilder();
        out.append(
            "<!DOCTYPE html>" +
            "<html><head><title>My App</title><link rel=\"stylesheet\" href=\"index.css\"></head><body><main>" +
            "<form action=\"create_user_servlet\" method=\"get\" class=\"element\">" +

            "   <label for=\"email\">Email</label><br>" +
            "   <input type=\"text\" id=\"email\" name=\"email\" autocomplete=\"email\" required><br>" +

            "   <label for=\"password\">Password</label><br>" +
            "   <input type=\"text\" id=\"password\" name=\"password\" autocomplete=\"new-password\" required><br>" +

            "   <label for=\"name\">Name</label><br>" +
            "   <input type=\"text\" id=\"name\" name=\"name\" required><br>" +

            "   <label for=\"age\">Age</label><br>" +
            "   <input type=\"number\" id=\"age\" name=\"age\" required><br>" +

            "   <input type=\"submit\" value=\"Submit\">" +
            "</form>" +
            "<table class=\"text-lg\">" +
            "<thead><tr><th>Id</th><th>Email</th><th>Password</th><th>Password Salt</th><th>Name</th><th>Age</th><tr></thead>" +
            "<tbody>"
        );

        for (User user : users) {
            out
                .append("<tr><td>")
                .append(user.getId())
                .append("</td><td>")
                .append(user.getEmail())
                .append("</td><td>")
                .append(user.getPassword())
                .append("</td><td>")
                .append(user.getPasswordSalt())
                .append("</td><td>")
                .append(user.getName())
                .append("</td><td>")
                .append(user.getAge())
                .append("</td></tr>");
        }

        out.append("</tbody></table></main></body></html>\r\n");

        return new Response<>(StatusCode.OK, new Html(out.toString()));
    }
}
