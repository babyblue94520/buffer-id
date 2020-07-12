package pers.clare.bufferid.controller;

@FunctionalInterface
interface BufferFunction<V> {
    V apply(Integer buffer, String group, String prefix, Integer length);
}
