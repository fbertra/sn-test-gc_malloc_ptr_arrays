Test gc_malloc_ptr_arrays
=========================

This app is a second test for future Pull Request gc_malloc_atomic_primitive_arrays 

This test checks that Array[Ptr[_]] aren't treated as primitive arrays and that GC_malloc
 is called to allocate their memory.
