package pl.droidsonroids.jspoon;

class ArrayUtils {

    /**
     * Converts a given array of wrappers to the same type of primitive array. Null values are
     * represented as initial value for that primitive type. If the wrapper array is null, will
     * return null.
     * @param array wrapper array
     * @param primitiveType type of the returned primitive array
     * @throws IllegalArgumentException if the given primitive type is not a class of a primitive
     * @throws ClassCastException if primitive type does not represent items of the wrapper array
     */
    static Object toPrimitive(final Object[] array, Class<?> primitiveType) {
        if (array == null) {
            return null;
        }
        if (!primitiveType.isPrimitive()) {
            throw new IllegalArgumentException(primitiveType + " is not a primitive type");
        }
        Class<?> pt = primitiveType;
        if(Boolean.TYPE.equals(pt)) {
            return toPrimitive((Boolean[]) array);
        }
        if(Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[]) array);
        }
        if(Long.TYPE.equals(pt)) {
            return toPrimitive((Long[]) array);
        }
        if(Short.TYPE.equals(pt)) {
            return toPrimitive((Short[]) array);
        }
        if(Byte.TYPE.equals(pt)) {
            return toPrimitive((Byte[]) array);
        }
        if(Character.TYPE.equals(pt)) {
            return toPrimitive((Character[]) array);
        }
        if(Double.TYPE.equals(pt)) {
            return toPrimitive((Double[]) array);
        }
        if(Float.TYPE.equals(pt)) {
            return toPrimitive((Float[]) array);
        }
        return array;
    }

    static char[] toPrimitive(Character[] array) {
        if (array == null) {
            return null;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new char[1])[0];
                continue;
            }
            result[i] = array[i].charValue();
        }
        return result;
    }

    static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new byte[1])[0];
                continue;
            }
            result[i] = array[i].byteValue();
        }
        return result;
    }

    static boolean[] toPrimitive(Boolean[] array) {
        if (array == null) {
            return null;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new boolean[1])[0];
                continue;
            }
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    static float[] toPrimitive(Float[] array) {
        if (array == null) {
            return null;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new float[1])[0];
                continue;
            }
            result[i] = array[i].floatValue();
        }
        return result;
    }

    static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new double[1])[0];
                continue;
            }
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    static short[] toPrimitive(Short[] array) {
        if (array == null) {
            return null;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new short[1])[0];
                continue;
            }
            result[i] = array[i].shortValue();
        }
        return result;
    }

    static long[] toPrimitive(Long[] array) {
        if (array == null) {
            return null;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new long[1])[0];
                continue;
            }
            result[i] = array[i].longValue();
        }
        return result;
    }

    static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                result[i] = (new int[1])[0];
                continue;
            }
            result[i] = array[i].intValue();
        }
        return result;
    }
}