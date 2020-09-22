package pers.clare.bufferid.controller;

@FunctionalInterface
interface BufferFunction<V> {
    V apply(Integer buffer, String id, String prefix, Integer length);
}
