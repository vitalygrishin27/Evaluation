package app.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedPasswordUtils {

    public static String encrytePassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public  static void main(String[] args){
        String password ="test";
        String encryptedPassword =encrytePassword(password);
        System.out.println("EP   "+encryptedPassword);

    }
}
