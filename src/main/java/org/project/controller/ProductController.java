package org.project.controller;


import org.project.dto.Request;
import org.project.dto.Response;
import org.project.service.ProductService;
import org.project.util.Method;
import org.project.util.RestHandler;
import org.project.util.RouteKey;
import org.project.util.SecurityUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProductController implements Runnable{
    private final Socket socket;
    private final ProductService productService;
    private final Map<RouteKey, RequestHandler> routes = new HashMap<>();

    public ProductController(Socket socket) {
        this.socket = socket;
        this.productService = new ProductService();
        initializeRoutes();
    }

    private void initializeRoutes() {
        routes.put(new RouteKey(Method.GET, "/api/products"), this::handleGetAllProducts);
        routes.put(new RouteKey(Method.GET, "/api/products/:id"), this::handleGetProductByID);
        routes.put(new RouteKey(Method.POST, "/api/products"), this::handleCreateProduct);
        routes.put(new RouteKey(Method.PUT, "/api/products/:id"), this::handleUpdateProduct);
        routes.put(new RouteKey(Method.DELETE, "/api/products/:id"), this::handleDeleteProduct);
        routes.put(new RouteKey(Method.GET, "/api/products/search"), this::handleSearchProducts);
        routes.put(new RouteKey(Method.PUT, "/api/products/:id/sold"), this::handleMarkAsSold);
    }

    @Override
    public void run() {
        BufferedReader input = null;
        PrintWriter output = null;
        try {
            output = new PrintWriter(socket.getOutputStream(), false);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Request request = RestHandler.parseRequest(input);
            if (request == null) return;

            Response response = handleRoute(request);
            RestHandler.sendResponse(output, response);

        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) {
                    input.close();
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private Response handleRoute(Request request) {
        RouteKey key = new RouteKey(request.getMethod(), request.getPath());
        RequestHandler handler = routes.get(key);

        if (handler == null) {
            for (Map.Entry<RouteKey, RequestHandler> entry : routes.entrySet()) {
                RouteKey registeredKey = entry.getKey();
                if (registeredKey.method() == request.getMethod() && matchPath(registeredKey.path(), request.getPath())) {
                    handler = entry.getValue();
                    break;
                }
            }
        }

        if (handler == null) {
            return errorResponse(404, "Not Found", "Endpoint not found");
        }

        try {
            return handler.handle(request);
        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }

    private boolean matchPath(String registeredPath, String actualPath) {
        String[] registeredParts = registeredPath.split("/");
        String[] actualParts = actualPath.split("/");

        if (registeredParts.length != actualParts.length) {
            return false;
        }

        for (int i = 0; i < registeredParts.length; i++) {
            if (!registeredParts[i].equals(actualParts[i]) && !registeredParts[i].startsWith(":")) {
                return false;
            }
        }

        return true;
    }
    //Get All Products
    private Response handleGetAllProducts(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }
        try {
            Response response = new Response();
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setBody(productService.getAllProducts());
            return response;
        } catch (Exception e) {
            return errorResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    //Get Product by ID
    private Response handleGetProductByID(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }
        String id = request.getPath().split("/")[3];

        try {
            Response response = new Response();
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setBody(productService.getProductByID(id));
            return response;
        } catch (Exception e) {
            return errorResponse(404, "Not Found", e.getMessage());
        }
    }

    //Create Product
    private Response handleCreateProduct(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }

        String sellerID = SecurityUtils.getUserIdFromToken(token);
        try {
            productService.createProduct(request.getBody(),sellerID);

            Response response = new Response();
            response.setStatusCode(201);
            response.setStatusText("Created");
            response.setBody("{\"message\": \"Product created successfully\"}");
            return response;

        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }

    //Update Product
    private Response handleUpdateProduct(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }

        String role = SecurityUtils.getRoleFromToken(token);

        if (!"ADMIN".equals(role)) {
            return errorResponse(403, "Forbidden", "Admin only");
        }
        String id = request.getPath().split("/")[3];

        try {
            productService.updateProduct(id, request.getBody());

            Response response = new Response();
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setBody("{\"message\": \"Product updated successfully\"}");
            return response;

        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }

    //Delete Product
    private Response handleDeleteProduct(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }

        String role = SecurityUtils.getRoleFromToken(token);

        if (!"ADMIN".equals(role)) {
            return errorResponse(403, "Forbidden", "Admin only");
        }
        String id = request.getPath().split("/")[3];

        try {
            productService.deleteProduct(id);

            Response response = new Response();
            response.setBody("{\"message\": \"Product deleted successfully\"}");
            return response;

        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }

    //Search Products
    private Response handleSearchProducts(Request request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            return errorResponse(401, "Unauthorized", "Missing token");
        }
        String keyword = request.getHeader("keyword");

        try {
            Response response = new Response();
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setBody(productService.searchProducts(keyword));
            return response;

        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }

    // marked as sold
    private Response handleMarkAsSold(Request request) {

        String id = request.getPath().split("/")[3];

        try {
            productService.markAsSold(id);

            Response res = new Response();
            res.setStatusCode(200);
            res.setStatusText("OK");
            res.setBody("{\"message\": \"Product marked as sold\"}");
            return res;

        } catch (Exception e) {
            return errorResponse(400, "Bad Request", e.getMessage());
        }
    }


    private Response errorResponse(int code, String status, String message) {
        Response res = new Response();
        res.setStatusCode(code);
        res.setStatusText(status);
        res.setBody("{\"error\": \"" + message + "\"}");
        return res;
    }
}
