Test gc_malloc_ptr_arrays
=========================

This app is a second test for future Pull Request gc_malloc_atomic_primitive_arrays 

This test checks that Array[Ptr[_]] aren't treated as primitive arrays and that GC_malloc
 is called to allocate their memory.
 
Important note
--------------

Boehm GC is exhibiting random behavior between program execution.

This test is successful if Boehm GC NEVER free the memory between "useMem (): after gc" and "testArrayPtr (): after useMemReturnPtr and after first gc" 

So we have to run it multiple times until the last gc succeeded.

For example, in this run, the last three "free bytes": are all equals to 184320.
So we don't know if the penultimate "free bytes" is only by chance.
 
```
francois@ubuntu:~/proyectos/oss/scala-native-fbd/sn-test-gc_malloc_ptr_arrays$ ./target/scala-2.11/sn-test-gc_malloc_ptr_arrays-out 
Test Array[Ptr]
testArrayPtr (): before useMemReturnPtr
heap size: 65536
free bytes: 53248

useMem (): before gc
ptr address hex: 0x113f000, int: 18083840
heap size: 10199040
free bytes: 184320

useMem (): after gc
heap size: 10199040
free bytes: 184320

testArrayPtr (): after useMemReturnPtr and after first gc
heap size: 10199040
free bytes: 184320

testArrayPtr (): after forgetting the pointer and after second gc
heap size: 10199040
free bytes: 184320

I'm not sure
```

In this second run, the last "free bytes" is equals to 10186752 and indicates that
memory was freed after forgetting the pointer and not before.

```
francois@ubuntu:~/proyectos/oss/scala-native-fbd/sn-test-gc_malloc_ptr_arrays$ ./target/scala-2.11/sn-test-gc_malloc_ptr_arrays-out 
Test Array[Ptr]
testArrayPtr (): before useMemReturnPtr
heap size: 65536
free bytes: 53248

useMem (): before gc
ptr address hex: 0x1a2b000, int: 27439104
heap size: 10199040
free bytes: 184320

useMem (): after gc
heap size: 10199040
free bytes: 184320

testArrayPtr (): after useMemReturnPtr and after first gc
heap size: 10199040
free bytes: 184320

testArrayPtr (): after forgetting the pointer and after second gc
heap size: 10199040
free bytes: 10186752

Test succesfull

```
