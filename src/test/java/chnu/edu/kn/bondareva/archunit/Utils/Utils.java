package chnu.edu.kn.bondareva.archunit.Utils;/*
  @author   User
  @project   ArchUnit
  @class  Utils
  @version  1.0.0 
  @since 01.12.2025 - 19.03
*/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
