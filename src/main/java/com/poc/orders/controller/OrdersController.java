package com.poc.orders.controller;

import com.poc.orders.entities.Orders;
import com.poc.orders.exception.OrderNotFoundException;
import com.poc.orders.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poc.orders.response.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    private static final String ORDER_NOT_FOUND_MESSAGE = "Order with ID %d not found"; // Define a format string
    private static final String UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred.";

    /**
     * This method handles the POST request to add a new order.
     * It receives an order object in the request body, sends it to the service layer
     * to be processed, and returns the saved order along with an HTTP status of OK.
     *
     * @param orders the order details sent in the request body
     * @return ResponseEntity containing the saved order and HTTP status
     */
    @Operation(summary = "Place an order", description = "Endpoint to place a new order. Accepts order details in the request body and returns the placed order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/addOrder")
    public ResponseEntity<Object> placeOrder(@RequestBody Orders orders) {
        try {
            if (orders.getProductname() == null || orders.getProductprice() <= 0) {
                // Return 400 Bad Request with a descriptive error message
                String errorMessage = "Invalid order data: Product name cannot be null and product price must be greater than zero.";
                log.error(errorMessage); // Log the error message
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST); // 400 Bad Request with message
            }

            // Process the order and return the response
            Orders order = ordersService.placeOrder(orders);
            return new ResponseEntity<>(order, HttpStatus.OK); // 200 OK

        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>("Internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error with message
        }
    }

    /**
     * This method retrieves all orders from the system.
     * It calls the service layer to get the list of orders and returns it in the response.
     * If an error occurs during retrieval, it returns a 404 Not Found status.
     *
     * @return ResponseEntity containing a list of Orders and HTTP status
     */
    @Operation(summary = "Get all orders", description = "Endpoint to retrieve all orders from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "404", description = "No orders found")
    })
    @GetMapping("/allOrders")
    public ResponseEntity<Object> getAllOrders(){
        try {
            List<Orders> orders = ordersService.getAllOrders();
            if (orders.isEmpty()){
                log.info("No orders found."); // Log the information
                ErrorResponse errorResponse = new ErrorResponse("No orders data found.", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found with message
            }
            log.info("Successfully retrieved {} orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error retrieving orders: {}", e.getMessage()); // Log the error message
            ErrorResponse errorResponse = new ErrorResponse("Order not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found with error response
        }
    }

    /**
     * This endpoint retrieves an order by its ID.
     * If the order is found, it returns the order details with a 200 OK status.
     * If the order is not found, it returns a 404 Not Found status with an error message.
     * For any unexpected errors, it returns a 500 Internal Server Error status.
     *
     * @param orderid the ID of the order to retrieve
     * @return ResponseEntity containing the order details or an error message
     */
    @Operation(summary = "Get Order by ID", description = "Retrieve an order using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderid}")
    public ResponseEntity<Object> getOrderById(@PathVariable int orderid) {
        // Fetch the order using the service layer
        Orders orders = ordersService.getOrderById(orderid);
        return new ResponseEntity<>(orders, HttpStatus.OK); // 200 OK with order details
//        try {
//            // Fetch the order using the service layer
//            Orders orders = ordersService.getOrderById(orderid);
//            return new ResponseEntity<>(orders, HttpStatus.OK); // 200 OK with order details
//        } catch (OrderNotFoundException e) {
//            // Log the exception message and return 404 Not Found
//            String errorMessage = String.format(ORDER_NOT_FOUND_MESSAGE, orderid);
//            log.error(e.getMessage());
//            ErrorResponse errorResponse = new ErrorResponse(errorMessage, 404);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found with error message
//        } catch (Exception e) {
//            // Log any other unexpected errors
//            log.error("Unexpected error occurred while fetching order: {}", e.getMessage());
//            ErrorResponse errorResponse = new ErrorResponse(UNEXPECTED_ERROR_OCCURRED, 500);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Internal Server Error
//        }
    }

    /**
     * This method retrieves an order from the system based on its ID and product name.
     * It calls the service layer to fetch the order and returns it in the response.
     * If the order is not found, it returns a 404 Not Found status with an error message.
     * If an unexpected error occurs, it returns a 500 Internal Server Error status.
     *
     * @param orderid      The ID of the order to retrieve
     * @param productname  The name of the product associated with the order
     * @return ResponseEntity containing the found Order and HTTP status
     */
    @Operation(summary = "Find Order by ID and Product Name",
            description = "Retrieve an order based on its ID and product name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/findByIdAndName")
    public ResponseEntity<Object> findByIdAndProductname(
            @RequestParam(value = "orderid") int orderid,
            @RequestParam(value = "productname", required = false) String productname) {

        try {
            // Fetch the order using the service layer based on order ID and product name
            Orders order = ordersService.findByIdAndProductname(orderid, productname);

            // Return the found order with HTTP status 200 OK
            return new ResponseEntity<>(order, HttpStatus.OK);

        } catch (OrderNotFoundException e) {
            // Handle case where the order is not found
            String errorMessage = String.format(ORDER_NOT_FOUND_MESSAGE +   " or Product name " + productname, orderid);
            log.error(e.getMessage()); // Log the error message

            // Create error response for not found status
            ErrorResponse errorResponse = new ErrorResponse(errorMessage, 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Handle any unexpected errors
            log.error("Unexpected error occurred while fetching order: {}", e.getMessage());

            // Create error response for internal server error status
            ErrorResponse errorResponse = new ErrorResponse(UNEXPECTED_ERROR_OCCURRED, 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * This endpoint updates an existing order by its ID.
     * If the order is found, it returns the updated order details with a 200 OK status.
     * If the order is not found, it returns a 404 Not Found status with an error message.
     *
     * @param orders the order details to update
     * @param orderid the ID of the order to update
     * @return ResponseEntity containing the updated order details or an error message
     */
    @Operation(summary = "Update Order by ID", description = "Update an existing order using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated order details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/updateOrder/{orderid}")
    public ResponseEntity<Object> updateOrderById(@RequestBody Orders orders, @PathVariable int orderid){
        try {
            Orders updatedOrder = ordersService.updateOrderById(orders, orderid);
            return ResponseEntity.ok(updatedOrder);
        }catch (OrderNotFoundException e) {
            String errorMessage = String.format(ORDER_NOT_FOUND_MESSAGE, orderid);
            log.error(e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(errorMessage, 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found with error response
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating order: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(UNEXPECTED_ERROR_OCCURRED, 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Internal Server Error with error response
        }
    }

    /**
     * This endpoint deletes an existing order by its ID.
     * If the order is found and deleted, it returns a 204 No Content status.
     * If the order is not found, it returns a 404 Not Found status with an error message.
     *
     * @param orderid the ID of the order to delete
     * @return ResponseEntity indicating the outcome of the delete operation
     */
    @Operation(summary = "Delete Order by ID", description = "Delete an existing order using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/deleteOrder/{orderid}")
    public ResponseEntity<Object> deleteOrderById(@PathVariable Integer orderid) { // Change to Integer
        try {
            ordersService.deleteOrderById(orderid);
            String successMessage = "Order with ID " + orderid + " deleted successfully."; // Success message
            log.info(successMessage); // Log the successful deletion
            return ResponseEntity.ok(successMessage); // 200 OK with success message
        } catch (OrderNotFoundException e) {
            log.error(e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Order not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found with error response
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting order: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(UNEXPECTED_ERROR_OCCURRED, 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Internal Server Error with error response
        }
    }
}
