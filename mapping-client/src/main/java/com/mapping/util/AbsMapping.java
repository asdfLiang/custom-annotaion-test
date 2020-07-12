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
    //
    private Class<? extends AbsMapping> targetClass;
    private BeanInfo targetBeanInfo;
    private PropertyDescriptor[] targetDescriptors;
    //
    private Class<?> sourceClass;
    private BeanInfo sourceBeanInfo;
    private PropertyDescriptor[] sourceDescriptors;

    public R convertFrom(Object t) {
        try {
            // 初始化
            R targetObject = init(t);
            // 设置每个字段的值
            for (PropertyDescriptor targetDescriptor : targetDescriptors) {
                if ("class".equals(targetDescriptor.getDisplayName())) {
                    continue;
                }
                setFieldValue(targetObject, targetDescriptor, t);
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

    private R init(Object t) throws InstantiationException, IllegalAccessException, IntrospectionException {
        targetClass = this.getClass();
        R targetObject = (R) targetClass.newInstance();
        // 获取目标对象信息
        targetBeanInfo = Introspector.getBeanInfo(targetObject.getClass());
        targetDescriptors = targetBeanInfo.getPropertyDescriptors();
        if (targetDescriptors == null || targetDescriptors.length == 0) {
            throw new IllegalStateException("target object no descriptors");
        }
        // 获取数据来源对象的类信息
        sourceClass = t.getClass();
        sourceBeanInfo = Introspector.getBeanInfo(sourceClass);
        sourceDescriptors = sourceBeanInfo.getPropertyDescriptors();
        if (sourceDescriptors == null || sourceDescriptors.length == 0) {
            throw new IllegalStateException("target object no descriptors");
        }
        return targetObject;
    }

    /**
     * 设置字段值
     *
     * @param targetObject
     * @param targetDescriptor
     * @param sourceObject
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setFieldValue(R targetObject, PropertyDescriptor targetDescriptor, Object sourceObject)
            throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获取目标字段名称及字段对象
        String targetFieldName = targetDescriptor.getDisplayName();
        Field targetField = targetClass.getDeclaredField(targetFieldName);
        // 根据注解判断是否要用自定义方法转换值
        // 此处根据类名获取注解
        CustomMapping customMapping = getCustomMappingByClassName(sourceObject.getClass().getName(), targetField);
        Object sourceValue;
        if (customMapping == null) {
            // 根据字段名称获取值
            sourceValue = getFieldValue(targetFieldName, sourceObject);
        } else {
            // 执行转换方法获取值
            sourceValue = getByCustomMapping(
                    targetFieldName,
                    customMapping,
                    targetObject,
                    sourceObject);
        }
        // 写入值
        writeFieldValue(targetObject, targetDescriptor, sourceValue);
    }

    /**
     * 根据类名获取字段
     *
     * @param className
     * @param field
     * @return
     */
    private CustomMapping getCustomMappingByClassName(String className, Field field) {
        /* 获取当前字段上所有注解
        只有一个注解直接获取该注解
        有多个且有类名则根据名称返回匹配的CustomMapping注解 */
        CustomMapping[] customMappings = field.getAnnotationsByType(CustomMapping.class);

        if (customMappings == null || customMappings.length == 0) {
            return null;
        }

        if (customMappings.length == 1) {
            return customMappings[0];
        } else {
            for (CustomMapping customMapping : customMappings) {
                if (className.equals(customMapping.sourceClassName())) {
                    return customMapping;
                }
            }
            throw new IllegalArgumentException("the 'className' is empty in the CustomMapping of " + field.getName());
        }
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
     * @return
     */
    private Object getFieldValue(String filedName, Object sourceObject)
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
     * @param sourceObject
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private Object getByCustomMapping(String targetFieldName,
                                      CustomMapping customMapping,
                                      R targetObject,
                                      Object sourceObject)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 获取注解中的源字段名称
        String sourceFieldName = stringIsEmpty(customMapping.sourceField()) ? targetFieldName : customMapping.sourceField();
        // 获取源字段值
        Object sourceFieldValue = getFieldValue(sourceFieldName, sourceObject);
        // 获取转换方法名
        String convertMethodName = customMapping.convertMethod();
        // 判断是否根据转换方法来设置值
        if (stringIsEmpty(convertMethodName)) {
            // 没有转换方法，直接设置
            return sourceFieldValue;
        } else {
            // 有转换方法，执行转换方法的到转换值
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
    @Repeatable(CustomMappings.class)
    protected @interface CustomMapping {
        /**
         * 源数据类名称
         *
         * @return
         */
        String sourceClassName() default "";

        /**
         * 源数据类中映射到当前字段的字段名称
         *
         * @return
         */
        String sourceField() default "";

        /**
         * 转换方法名称，该方法实现写到目标类中
         *
         * @return
         */
        String convertMethod() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    protected @interface CustomMappings {
        CustomMapping[] value();
    }
}
