/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;

public class ASMUtil {

    private static HashMap<String, Field> sFieldCache = new HashMap<>();

    /**
     * Creates a new instance with the provided constructor and arguments.
     *
     * @param constructor The constructor of the class for which a new instance should be created.
     * @param arguments   Array of objects to be passed as arguments to the constructor call.
     * @return A new object created by calling the constructor.
     */
    public static <T> T getInstance(Constructor<T> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            GalacticraftCore.logger.error("Exception creating instance of " + constructor.getClass().getName());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String className, Class<T> assignable) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!assignable.isAssignableFrom(assignable)) {
                throw new RuntimeException("Class '" + className + "' is not assignable from " + assignable.getSimpleName());
            }
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            GalacticraftCore.logger.error("Exception creating instance of " + className);
        }
        return null;
    }

    /**
     * Obtains the constructor for the named class identified by the parameter types.
     *
     * @param className     The fully qualified name of the class.
     * @param argumentTypes Parameter types to identify the constructor.
     * @return Constructor object representing the declared constructor for the parameter types.
     */
    public static Constructor<?> getConstructor(final String className, final Class<?>... argumentTypes) {
        if (className == null || argumentTypes == null) {
            GalacticraftCore.logger.error("The provided class name or arguments can't be null.");
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getDeclaredConstructor(argumentTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception ex) {
            GalacticraftCore.logger.error("Exception getting constructor of " + className);
        }
        return null;
    }

    /**
     * Obtains the constructor for the class identified by the parameter types.
     *
     * @param clazz The Class for which the constructor should be obtained.
     * @param types Parameter types to identify the constructor.
     * @return Constructor object representing the declared constructor for the parameter types.
     */
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... types) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception ex) {
            GalacticraftCore.logger.error("Exception getting constructor of " + clazz.getName());
        }
        return null;
    }

    /**
     * Returns the value of a private field for an object instance.
     *
     * @param object     An object instance from which the field value is to be extracted.
     * @param fieldNames A list of field names for which the value should be extracted. The functions returns value of the first field found.
     * @return The value of the provided field name.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(Object object, String... fieldNames) {
        Class<?> cls = object.getClass();
        for (String field : fieldNames) {
            try {
                Field result = cls.getDeclaredField(field);
                result.setAccessible(true);
                return (T) result.get(object);
            } catch (Exception ex) {
                GalacticraftCore.logger.error("Exception in getObject()");
            }
        }
        GalacticraftCore.logger.error("Could not retrieve any object for the provided field names.");

        return null;
    }

    /**
     * Returns the value of a private final field for an object instance and removes the final modifier.
     *
     * @param object     An object instance from which the field value is to be extracted.
     * @param fieldNames A list of field names for which the value should be extracted. The functions returns value of the first field found.
     * @return The value of the provided field name.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFinalObject(Object object, String... fieldNames) {
        Class<?> cls = object.getClass();
        for (String field : fieldNames) {
            try {
                Field result = cls.getDeclaredField(field);
                result.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(result, result.getModifiers() & ~Modifier.FINAL);
                return (T) result.get(object);
            } catch (Exception ex) {
                GalacticraftCore.logger.error("Exception in getFinalObject()");
            }
        }
        GalacticraftCore.logger.error("Could not retrieve any final object for the provided field names.");

        return null;
    }

    /**
     * Returns the value of a private static field for a class.
     *
     * @param clazz      The class for which the field value is to be extracted.
     * @param fieldNames A list of field names for which the value should be extracted.
     * @return The value of the provided field name.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getStaticObject(Class<?> clazz, String fieldName) {
        Optional<T> object = Optional.empty();
        try {
            Field result = clazz.getDeclaredField(fieldName);
            result.setAccessible(true);
            object = Optional.of((T) result.get(null));
        } catch (Exception e) {
        }
        return object;
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible. <p> Throws an exception if the method is not found.
     *
     * @param  clazz          The class to find the method on.
     * @param  methodName     The name of the method to find (used in developer environments, i.e. "getWorldTime").
     * @param  parameterTypes The parameter types of the method to find.
     * 
     * @return                The method with the specified name and parameters in the given class.
     */
    @Nonnull
    public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(methodName), "Method name cannot be empty");
        try
        {
            Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (Exception e)
        {
            throw new ReflectionThrows.UnableToFindMethodException(e);
        }
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible. Note: for performance, store the returned value and avoid calling this repeatedly. <p> Throws an exception if the method is not found.
     *
     * @param  clazz          The class to find the method on.
     * @param  methodName     The name of the method to find (used in developer environments, i.e. "getWorldTime").
     * @param  methodObfName  The obfuscated name of the method to find (used in obfuscated environments, i.e. "getWorldTime"). If the name you are looking for is on a class that is never obfuscated, this should be null.
     * @param  parameterTypes The parameter types of the method to find.
     * 
     * @return                The method with the specified name and parameters in the given class.
     */
    @Nonnull
    public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable String methodObfName, Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(methodName), "Method name cannot be empty");

        String nameToFind = FMLLaunchHandler.isDeobfuscatedEnvironment() ? methodName : MoreObjects.firstNonNull(methodObfName, methodName);

        try
        {
            Method m = clazz.getDeclaredMethod(nameToFind, parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (Exception e)
        {
            throw new ReflectionThrows.UnableToFindMethodException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, String fieldName) {
        try {
            return (T) findField(classToAccess, fieldName, null).get(instance);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, String fieldName, @Nullable String fieldObfName) {
        try {
            return (T) findField(classToAccess, fieldName, fieldObfName).get(instance);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, @Nullable T instance, @Nullable E value, String fieldName) {
        try {
            findField(classToAccess, fieldName, null).set(instance, value);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, @Nullable T instance, @Nullable E value, String fieldName, @Nullable String fieldObfName) {
        try {
            findField(classToAccess, fieldName, fieldObfName).set(instance, value);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static Field findField(@Nonnull Class<?> clazz, @Nonnull String fieldName, @Nullable String fieldObfName) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(fieldName), "Field name cannot be empty");

        String nameToFind = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ? fieldName : MoreObjects.firstNonNull(fieldObfName, fieldName);
        try {
            Field f = clazz.getDeclaredField(nameToFind);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public static void setStaticFieldObject(Class<?> clazz, String fieldName, Object value) {
        Field field = getStaticField(clazz, fieldName);
        try {
            field.set(clazz, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            GalacticraftCore.logger.error("An error occured when attempting to set static Field [ " + fieldName + " ]");
        }
    }

    private static Field getStaticField(Class<?> clazz, String fieldName) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(fieldName), "Field name cannot be empty");

        String fullFieldName = genFieldFullName(clazz, fieldName);
        if (sFieldCache.containsKey(fullFieldName)) {
            return sFieldCache.get(fullFieldName);
        }
        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
        }
        if (field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
        }
        if (field == null) {
            for (clazz = clazz.getSuperclass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                } catch (NoSuchFieldException e) {
                }
            }
        }
        if (field == null) {
            GalacticraftCore.logger.error("Can't get [ " + fieldName + " ] Field from Class [ " + clazz.getSimpleName() + " ]");
        }
        sFieldCache.put(fullFieldName, field);
        return field;
    }

    private static String genFieldFullName(Class<?> clazz, String fieldName) {
        StringBuilder name = new StringBuilder();
        name.append(clazz.getName());
        name.append(":");
        name.append(fieldName);
        return name.toString();
    }

    public static <E> String getBiomeName(BiomeProperties instance) {
        return ASMUtil.getPrivateValue(Biome.BiomeProperties.class, instance, "biomeName", "field_185412_a");
    }

    static class ReflectionThrows
    {

        public static class UnableToFindMethodException extends RuntimeException
        {

            private static final long serialVersionUID = 1L;

            public UnableToFindMethodException(String[] methodNames, Exception failed)
            {
                super(failed);
            }

            public UnableToFindMethodException(Throwable failed)
            {
                super(failed);
            }
        }

        public static class UnableToFindClassException extends RuntimeException
        {

            private static final long serialVersionUID = 1L;

            public UnableToFindClassException(String[] classNames, @Nullable Exception err)
            {
                super(err);
            }
        }

        public static class UnableToAccessFieldException extends RuntimeException
        {

            private static final long serialVersionUID = 1L;

            public UnableToAccessFieldException(String[] fieldNames, Exception e)
            {
                super(e);
            }

            public UnableToAccessFieldException(Exception e)
            {
                super(e);
            }
        }

        public static class UnableToFindFieldException extends RuntimeException
        {

            private static final long serialVersionUID = 1L;

            public UnableToFindFieldException(String[] fieldNameList, Exception e)
            {
                super(e);
            }

            public UnableToFindFieldException(Exception e)
            {
                super(e);
            }
        }

        public static class UnknownConstructorException extends RuntimeException
        {

            private static final long serialVersionUID = 1L;

            public UnknownConstructorException(final String message)
            {
                super(message);
            }
        }
    }
}
