<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>My App</title>
        <link rel="stylesheet" href="index.css">
    </head>
    <body>
        <main>
            <div class="element">
                <a href="sign_in">Sign In</a>
            </div>

            <div class="element">
                <p class="text-lg">Hello World!<p>
                <a href="hello_servlet">Hello Servlet</a>
            </div>

            <div class="element">
                <p class="text-lg">Multiply a Digit</p>
                <form action="multiplication_servlet" method="get">
                    <label for="digit">Enter a digit:</label><br>
                    <input type="number" id="digit" name="digit"><br>
                    <input type="submit" value="Submit">
                </form>
            </div>

            <div class="element">
                <a href="users_servlet">Users Servlet</a>
            </div>

            <div class="element">
                <p>Other pages:</p>
                <ol>
                    <li><a href="pages/not_found.jsp">404 HTTP Error - Not Found</a></li>
                    <li><a href="pages/internal_server_error.jsp">500 HTTP Error - Internal Server Error</a></li>
                </ol>
            </div>
        </main>
    </body>
</html>