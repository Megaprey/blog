package ru.razzh.igor.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;

public class Utils {
    public static byte[] multipartFileToMassByte(MultipartFile file) throws IOException, SQLException {
        if (file == null || file.isEmpty()) {
            return null; // Или выбросить исключение, если файл обязателен
        }


        return file.getBytes();
    }
}
