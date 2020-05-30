package dto;

import com.alibaba.fastjson.JSONObject;
import enums.StatusEnum;
import jdk.nashorn.internal.objects.annotations.Function;
import lombok.Data;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Date;

@Data
public class UserTransferDTO {

    private Long id;

    private String name;

    @CustomMapping
    private Date birthday;

    @CustomMapping(
            sourceFields = {"statusValue"},
            convertMethod = "dto.UserTransferDTO.oldStatus2NewStatus")
    private String status;

    private JSONObject bizParams;

    public static UserTransferDTO convertFrom(UserDTO userDTO) {
        UserTransferDTO userTransferDTO = new UserTransferDTO();
        Class<? extends UserTransferDTO> aClass = userTransferDTO.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String name = declaredField.getName();
            Annotation[] annotations = declaredField.getDeclaredAnnotations();
            if (annotations.length > 0) {
                System.out.println(name + annotations[0].annotationType());
            }
        }

        return userTransferDTO;
    }

    @Function
    public static String oldStatus2NewStatus(StatusEnum statusValue) {
        return statusValue.name();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    private  @interface CustomMapping {

        String[] sourceFields() default {"filedName"};

        String convertMethod() default "";
    }
}
