package com.example.demo1;

import com.example.demo1.user.UserDto;
import com.example.demo1.user.UserRepository;
import com.example.demo1.user.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Redirect;
import com.example.demo1.util.response.DispatcherResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

@WebServlet(name = "createUserServlet", value = "/create_user_servlet")
@WebFilter(filterName = "CorsFilter", urlPatterns = "/create_user_servlet")
public class CreateUserServlet extends Controller {
    private static final Logger logger = LogManager.getLogger(CreateUserServlet.class);

    private UserService userService;

    @Override
    protected void initController() throws Exception {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    @AuthRequired
    public IntoResponse createUser(RequestContext ctx, Long userId) throws Exception {
        String email = ctx.getRequest().getParameter("email");
        String password = ctx.getRequest().getParameter("password");
        String name = ctx.getRequest().getParameter("name");
        Integer age = Integer.valueOf(ctx.getRequest().getParameter("age"));

        try {
            userService.create(new UserDto(email, password, name, age));
        } catch (SQLException e) {
            return new DispatcherResponse("pages/internal_server_error.jsp");
        }

        return new Redirect("users_servlet");
    }
}
