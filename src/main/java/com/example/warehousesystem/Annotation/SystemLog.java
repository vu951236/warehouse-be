package com.example.warehousesystem.Annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLog {
    String action();              // Hành động: "Tạo đơn nhập", "Xóa đơn xuất", ...
    String targetTable() default ""; // Bảng liên quan: "importorder", "exportorder"...
}
