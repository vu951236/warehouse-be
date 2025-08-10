package com.example.warehousesystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1000, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN(1001, "Invalid refresh token" , HttpStatus.BAD_REQUEST),
    USER_EXISTS(1002, "Username or email already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_INCORRECT(1004, "Old password incorrect", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Invalid password", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_CONFIRMATION_CODE(1010, "The confirmation code is incorrect or has expired.", HttpStatus.NOT_FOUND),
    APPLICATION_NOT_FOUND(1012, "Application not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(4004, "Resource not found", HttpStatus.NOT_FOUND),
    WAREHOUSE_NOT_FOUND(4005, "Warehouse not found", HttpStatus.NOT_FOUND),

    // SKU & Item
    BARCODE_EXISTS(5000, "Barcode already exists", HttpStatus.CONFLICT),
    SKU_NOT_FOUND(5001, "SKU not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND(5005, "Item not found", HttpStatus.NOT_FOUND),
    ITEM_ALREADY_EXPORTED(5006, "Item already exported", HttpStatus.BAD_REQUEST),
    ITEM_QUANTITY_MISMATCH(5007, "Export quantity does not match order quantity", HttpStatus.BAD_REQUEST),

    // Box & Bin capacity
    NO_BIN_CAPACITY(5002, "No available Bin with enough capacity", HttpStatus.BAD_REQUEST),
    NO_BOX_CAPACITY(5003, "No suitable Box found or created", HttpStatus.BAD_REQUEST),
    BOX_NOT_FOUND(5008, "Box not found", HttpStatus.NOT_FOUND),

    // Export Order
    EXPORT_ORDER_NOT_FOUND(6000, "Export order not found", HttpStatus.NOT_FOUND),
    EXPORT_ORDER_SKU_MISMATCH(6001, "Export order SKU does not match items", HttpStatus.BAD_REQUEST),
    EXPORT_ORDER_ALREADY_COMPLETED(6002, "Export order already completed", HttpStatus.BAD_REQUEST),

    INVALID_INPUT(5004, "Invalid Input", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1015, "Email already exists", HttpStatus.CONFLICT);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
