package com.mapping.util;

import java.beans.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liangzj
 * @date 2020/07/11 22:18
 */
public class AbsMapping<R extends AbsMapping> {

    public R convertFrom(Object t) {
        try {
            // 创建目标对象
            Class<? extends AbsMapping> targetClass = this.getClass();
            R targetObject = (R) targetClass.newInstance();
            // 获取目标对象信息
            BeanInfo targetBeanInfo = Introspector.getBeanInfo(targetObject.getClass());
            PropertyDescriptor[] targetDescriptors = targetBeanInfo.getPropertyDescriptors();
            if (targetDescriptors == null || targetDescriptors.length == 0) {
                throw new IllegalStateException("target object no descriptors");
            }
            // 获取数据来源对象的类信息
            Class<?> sourceClass = t.getClass();
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(sourceClass);
            PropertyDescriptor[] sourceDescriptors = sourceBeanInfo.getPropertyDescriptors();
            if (sourceDescriptors == null || sourceDescriptors.length == 0) {
                throw new IllegalStateException("target object no descriptors");
            }

            // 设置每个字段的值
            for (PropertyDescriptor targetDescriptor : targetDescriptors) {
                if ("class".equals(targetDescriptor.getDisplayName())) {
                    continue;
                }
                setFieldValue(targetObject, targetClass, targetDescriptor, t, sourceDescriptors);
            }
            // 返回结果
            return targetObject;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置字段值
     *
     * @param targetObject
     * @param targetClass
     * @param targetDescriptor
     * @param sourceObject
     * @param sourceDescriptors
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setFieldValue(R targetObject, Class<? extends AbsMapping> targetClass, PropertyDescriptor targetDescriptor,
                               Object sourceObject, PropertyDescriptor[] sourceDescriptors)
            throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获取目标字段名称及字段对象
        String targetFieldName = targetDescriptor.getDisplayName();
        Field targetField = targetClass.getDeclaredField(targetFieldName);
        // 根据注解判断是否要用自定义方法转换值
        CustomMapping customMapping = targetField.getAnnotation(CustomMapping.class);
        Object sourceValue;
        if (customMapping == null) {
            // 根据字段名称获取值
            sourceValue = getFieldValue(targetFieldName, sourceObject, sourceDescriptors);
        } else {
            // 执行转换方法获取值
            sourceValue = getByCustomMapping(
                    targetFieldName, customMapping,
                    targetObject, targetClass,
                    sourceObject, sourceDescriptors);
        }
        // 写入值
        writeFieldValue(targetObject, targetDescriptor, sourceValue);
    }

    /**
     * 写入字段值
     *
     * @param targetObject
     * @param targetDescriptor
     * @param sourceValue
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void writeFieldValue(R targetObject, PropertyDescriptor targetDescriptor, Object sourceValue)
            throws IllegalAccessException, InvocationTargetException {
        // 获取目标对象的写入方法
        Method writeMethod = targetDescriptor.getWriteMethod();
        // 执行set方法将目标值写入
        writeMethod.invoke(targetObject, sourceValue);
    }

    /**
     * 获取字段值
     *
     * @param filedName
     * @param sourceObject
     * @param sourceDescriptors
     * @return
     */
    private Object getFieldValue(String filedName,
                                 Object sourceObject, PropertyDescriptor[] sourceDescriptors)
            throws InvocationTargetException, IllegalAccessException {
        // 遍历源对象值，按照名称获取到数据
        for (PropertyDescriptor sourceDescriptor : sourceDescriptors) {
            if (filedName.equals(sourceDescriptor.getDisplayName())) {
                return sourceDescriptor.getReadMethod().invoke(sourceObject, null);
            }
        }
        return null;
    }

    /**
     * 根据注解设置字段值
     *
     * @param targetFieldName
     * @param customMapping
     * @param targetObject
     * @param targetClass
     * @param sourceObject
     * @param sourceDescriptors
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private Object getByCustomMapping(String targetFieldName, CustomMapping customMapping,
                                      R targetObject, Class<? extends AbsMapping> targetClass,
                                      Object sourceObject, PropertyDescriptor[] sourceDescriptors)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获取注解中的源字段名称
        String sourceFieldName = stringIsEmpty(customMapping.sourceField()) ? targetFieldName : customMapping.sourceField();
        // 获取源字段值
        Object sourceFieldValue = getFieldValue(sourceFieldName, sourceObject, sourceDescriptors);
        // 获取转换方法名
        String convertMethodName = customMapping.convertMethod();
        // 判断是否根据转换方法来设置值
        if (stringIsEmpty(convertMethodName)) {
            // 没有转换方法，直接设置
            return sourceFieldValue;
        } else {
            // 有转换方法，执行转换方法的到转换值，并写入
            Method method = targetClass.getMethod(customMapping.convertMethod(), sourceFieldValue.getClass());
            return method.invoke(targetObject, sourceFieldValue);
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    private boolean stringIsEmpty(String s) {
        return s == null || "".equals(s);
    }


    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    protected @interface CustomMapping {

        String sourceField() default "";

        String convertMethod() default "";
    }

}
