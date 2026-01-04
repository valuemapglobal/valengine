package com.value.data.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReadConfigurationFile {

    public static String apiTokenDecryptPassword;

    @Value("${business.apiTokenDecryptPassword:6460201d23954f8e}")
    public  void setApiTokenDecryptPassword(String apiTokenDecryptPassword) {
        ReadConfigurationFile.apiTokenDecryptPassword= apiTokenDecryptPassword;
    }

}
