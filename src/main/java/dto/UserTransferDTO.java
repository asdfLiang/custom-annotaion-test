package dto;

import com.alibaba.fastjson.JSONObject;
import enums.StatusEnum;
import jdk.nashorn.internal.objects.annotations.Function;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class UserTransferDTO {

    private Long id;

    private String name;

    @CustomMapping
    private Date birthday;

    @CustomMapping(
            sourceField = "statusValue",
            convertMethod = "oldStatus2NewStatus")
    private String status;

    private JSONObject bizParams;

    private static boolean stringIsEmpty(String s) {
        return null == s || "".equals(s);
    }

    public static <T> UserTransferDTO convertFrom(T t) throws NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        UserTransferDTO userTransferDTO = new UserTransferDTO();
        Class<? extends UserTransferDTO> aClass = userTransferDTO.getClass();
        Field[] targetFields = aClass.getDeclaredFields();

        Class<?> sourceUserClass = t.getClass();
        // 字段赋值
        for (Field targetField : targetFields) {
            boolean accessible = targetField.isAccessible();
            targetField.setAccessible(true);
            String fieldName = targetField.getName();
            CustomMapping customMapping = targetField.getAnnotation(CustomMapping.class);
            // 如果包含自定义的注解，则根据注解来赋值，否则根据字段名称来赋值
            if (customMapping != null) {
                String sourceFieldName = (stringIsEmpty(customMapping.sourceField())) ? targetField.getName() : customMapping.sourceField();
                String convertMethodName = customMapping.convertMethod();
                // 获取数据源对象的字段，设置到目标对象中
                Field sourceField = sourceUserClass.getDeclaredField(sourceFieldName);
                sourceField.setAccessible(true);
                // 判断是否需要调用转换方法转换
                if (stringIsEmpty(customMapping.convertMethod())) {
                    targetField.set(userTransferDTO, sourceField.get(t));
                } else {
                    targetField.set(userTransferDTO,
                            aClass.getMethod(convertMethodName, sourceField.getType())
                                    .invoke(userTransferDTO, sourceField.get(t)));
                }
                sourceField.setAccessible(accessible);
            } else {
                // 获取数据源对象的字段，设置到目标对象中
                Field sourceField = sourceUserClass.getDeclaredField(fieldName);
                boolean accessible1 = sourceField.isAccessible();
                sourceField.setAccessible(true);
                targetField.set(userTransferDTO, sourceField.get(t));
                sourceField.setAccessible(accessible1);
            }

            targetField.setAccessible(accessible);
        }

        return userTransferDTO;
    }

    public String oldStatus2NewStatus(StatusEnum statusValue) {
        return statusValue.name();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    private @interface CustomMapping {

        String sourceField() default "";

        String convertMethod() default "";
    }

    ///////////////////////
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONObject getBizParams() {
        return bizParams;
    }

    public void setBizParams(JSONObject bizParams) {
        this.bizParams = bizParams;
    }

    @Override
    public String toString() {
        return "UserTransferDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", status='" + status + '\'' +
                ", bizParams=" + bizParams +
                '}';
    }
}
