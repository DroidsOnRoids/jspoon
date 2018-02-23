package pl.droidsonroids.jspoon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;

public class FieldType {

    private final Field wrapped;
    private final String name;
    private final int localHashCode;
    private Class<?> typeClass;
    private Type genericType;
    private boolean isArray = false;
    private Class<?> arrayContentType = null;
    private boolean hasTypeArguments = false;
    private Class<?>[] typeArguments = null;
    private Class<?> subType;

    FieldType(Class<?> fieldClass, Field field) {
        if (fieldClass == null || field == null) {
            throw new IllegalArgumentException("Field and its class cannot be null");
        }
        this.subType = fieldClass;
        this.wrapped = field;
        this.name = field.getName();
        this.typeClass = field.getType();
        this.genericType = field.getGenericType();
        this.localHashCode = field.hashCode() + name.hashCode();

        if (genericType instanceof Class) {
            Class<?> classType = (Class<?>) genericType;
            this.isArray = classType.isArray();
            this.arrayContentType = classType.getComponentType();
        } else if (genericType instanceof ParameterizedType) {
            processParametrizedType((ParameterizedType) genericType, subType);
        } else if (genericType instanceof GenericArrayType) {
            GenericArrayType typeVarArray = (GenericArrayType) genericType;
            this.isArray = true;
            Type component = typeVarArray.getGenericComponentType();
            this.arrayContentType = resolveClass(component, subType);
            if (component instanceof ParameterizedType) {
                processParametrizedType((ParameterizedType) component, subType);
            }
        } else if (genericType instanceof TypeVariable<?>) {
            TypeVariable<?> variable = (TypeVariable<?>) genericType;
            Type resolvedType = getTypeVariableMap(subType).get(variable);
            resolvedType = (resolvedType == null) ? resolveBound(variable) : resolvedType;
            this.genericType = resolvedType;
            this.typeClass = resolveClass(resolvedType, subType);
            this.isArray = typeClass.isArray();
            this.arrayContentType = typeClass.getComponentType();
            if (resolvedType instanceof ParameterizedType) {
                processParametrizedType((ParameterizedType) resolvedType, subType);
            }
            // TypeVariable extend array
        }
    }

    private void processParametrizedType(ParameterizedType paramType, Class<?> subType) {
        hasTypeArguments = true;
        Type[] typeArgs = paramType.getActualTypeArguments();
        typeArguments = new Class<?>[typeArgs.length];
        for (int i = 0; i < typeArgs.length; i++) {
            typeArguments[i] = resolveClass(typeArgs[i], subType);
        }
    }

    /**
     * @param typeVariable
     * @return first bound, can be Class or ParametrizedType
     */
    private static Type resolveBound(TypeVariable<?> typeVariable) {
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length == 0) {
            return Object.class;
        }
        Type bound = bounds[0];
        if (bound instanceof TypeVariable) {
            bound = resolveBound((TypeVariable<?>) bound);
        }
        return bound;
    }

    private Class<?> resolveClass(Type type, Class<?> subType) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return resolveClass(((ParameterizedType) type).getRawType(), subType);
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            Class<?> component = resolveClass(gat.getGenericComponentType(), subType);
            return Array.newInstance(component, 0).getClass();
        } else if (type instanceof TypeVariable<?>) {
            TypeVariable<?> variable = (TypeVariable<?>) type;
            Type resolvedType = getTypeVariableMap(subType).get(variable);
            return (resolvedType == null) ? resolveClass(resolveBound(variable), subType)
                    : resolveClass(resolvedType, subType);
        } else if (type instanceof WildcardType) {
            WildcardType wcType = (WildcardType) type;
            Type[] bounds = wcType.getLowerBounds().length == 0 ? wcType.getUpperBounds()
                    : wcType.getLowerBounds();
            return resolveClass(bounds[0], subType);
        }
        // there are no more types in a standard JDK
        throw new IllegalArgumentException("Unknown type: " + type);
    }

    private static Map<TypeVariable<?>, Type> getTypeVariableMap(final Class<?> targetType) {
        Map<TypeVariable<?>, Type> map = new HashMap<TypeVariable<?>, Type>();
        // Populate interfaces
        populateSuperTypeArgs(targetType.getGenericInterfaces(), map);
        // Populate super classes and interfaces
        Type genericType = targetType.getGenericSuperclass();
        Class<?> type = targetType.getSuperclass();
        while (type != null && !Object.class.equals(type)) {
            if (genericType instanceof ParameterizedType) {
                populateTypeArgs((ParameterizedType) genericType, map);
            }
            populateSuperTypeArgs(type.getGenericInterfaces(), map);
            genericType = type.getGenericSuperclass();
            type = type.getSuperclass();
        }
        // Populate enclosing classes
        type = targetType;
        while (type.isMemberClass()) {
            genericType = type.getGenericSuperclass();
            if (genericType instanceof ParameterizedType){
                populateTypeArgs((ParameterizedType) genericType, map);
            }
            type = type.getEnclosingClass();
        }
        return map;
    }

    /**
     * Populates the {@code map} with with variable/argument pairs for the given {@code types}.
     */
    private static void populateSuperTypeArgs(final Type[] types, final Map<TypeVariable<?>, Type> map) {
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                populateTypeArgs(parameterizedType, map);
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class){
                    populateSuperTypeArgs(((Class<?>) rawType).getGenericInterfaces(), map);
                }
            } else if (type instanceof Class) {
                populateSuperTypeArgs(((Class<?>) type).getGenericInterfaces(), map);
            }
        }
    }

    /**
     * Populates the {@code map} with variable/argument pairs for the given {@code type}.
     */
    private static void populateTypeArgs(ParameterizedType type, Map<TypeVariable<?>, Type> map) {
        if (type.getRawType() instanceof Class) {
            TypeVariable<?>[] typeVariables = ((Class<?>) type.getRawType()).getTypeParameters();
            Type[] typeArguments = type.getActualTypeArguments();
            if (type.getOwnerType() != null) {
                Type owner = type.getOwnerType();
                if (owner instanceof ParameterizedType){
                    populateTypeArgs((ParameterizedType) owner, map);
                }
            }
            for (int i = 0; i < typeArguments.length; i++) {
                TypeVariable<?> variable = typeVariables[i];
                Type typeArgument = typeArguments[i];
                if (typeArgument instanceof Class) {
                    map.put(variable, typeArgument);
                } else if (typeArgument instanceof GenericArrayType) {
                    map.put(variable, typeArgument);
                } else if (typeArgument instanceof ParameterizedType) {
                    map.put(variable, typeArgument);
                } else if (typeArgument instanceof TypeVariable) {
                    TypeVariable<?> typeVariableArgument = (TypeVariable<?>) typeArgument;
                    Type resolvedType = map.get(typeVariableArgument);
                    if (resolvedType == null){
                        resolvedType = resolveBound(typeVariableArgument);
                    }
                    map.put(variable, resolvedType);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return this.localHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FieldType && this.wrapped.equals(obj);
    }

    Field unwrap() {
        return this.wrapped;
    }

    public Class<?> getType() {
        return this.typeClass;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAssignableTo(Class<?> superClass) {
        if (superClass == null) {
            return false;
        }
        return superClass.isAssignableFrom(typeClass);
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.wrapped.getModifiers());
    }

    public boolean isModifiable() {
        return !isFinal() && !this.wrapped.isSynthetic();
    }

    public boolean isConcrete() {
        return !Modifier.isAbstract(this.wrapped.getModifiers()) && !typeClass.isInterface();
    }

    public int getTypeArgumentCount() {
        return hasTypeArguments ? typeArguments.length : 0;
    }

    public Class<?> getTypeArgument(int index) {
        return typeArguments[index];
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return this.wrapped.getAnnotation(annotationClass);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("FieldType<");
        str.append(this.name).append("::");
        if (!isModifiable())
            str.append("unmodifiable ");
        str.append(isArray ? this.arrayContentType : this.typeClass);
        if (hasTypeArguments) {
            str.append("<");
            for (Class<?> arg : typeArguments) {
                str.append(arg).append(",");
            }
            str.deleteCharAt(str.length() - 1);
            str.append(">");
        }
        if (isArray)
            str.append("[]");
        return str.append(">").toString();
    }

    public void set(Object instance, Object value) throws IllegalArgumentException, IllegalAccessException {
        this.wrapped.setAccessible(true);
        this.wrapped.set(instance, value);
    }
}