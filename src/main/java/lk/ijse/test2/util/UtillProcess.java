package lk.ijse.test2.util;

import java.util.UUID;

public class UtillProcess {
    public static String generateId(){
        return UUID.randomUUID().toString();
    }
}
