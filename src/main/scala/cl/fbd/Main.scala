package cl.fbd

import scalanative.native._
import scalanative.runtime.GC
import scalanative.libc.stdlib._

/*
 *
 */

object Main {
  def main (args : Array [String]) = {
    testArrayPtr ()
    
    ()
  }
  
  /*
   * 
   */
   
  def testArrayPtr () : Unit = {
    fprintf(stdout, c"Test Array[Ptr]\n")
    
    val arr = new Array [Ptr[Byte]] (2)
    
    val heapSizeBefore = GC2.GC_get_heap_size ()
    val freeBytesBefore = GC2.GC_get_free_bytes ()
          
    fprintf(stdout, c"testArrayPtr (): before useMemReturnPtr\n")
    
    fprintf(stdout, c"heap size: %d\n", heapSizeBefore)
    fprintf(stdout, c"free bytes: %d\n", freeBytesBefore)
    fprintf(stdout, c"\n")
    
    arr (0) = useMemReturnPtr ()

    // GC shouldn't collect the memory allocated inside useMemReturnPtr ()     
    GC2.GC_gcollect ()
    
    val heapSizeAfter1 = GC2.GC_get_heap_size ()
    val freeBytesAfter1 = GC2.GC_get_free_bytes()
          
    fprintf(stdout, c"testArrayPtr (): after useMemReturnPtr and after first gc\n")
    
    fprintf(stdout, c"heap size: %d\n", heapSizeAfter1)
    fprintf(stdout, c"free bytes: %d\n", freeBytesAfter1)
    fprintf(stdout, c"\n")

    // forget the pointer returned by useMemReturnPtr   
    arr (0) = GC.malloc (1).cast[Ptr[Byte]]

    // the memory allocated inside useMemReturnPtr () is now unreachable
    // new allocated memory is much smaller
    GC2.GC_gcollect ()
    
    fprintf(stdout, c"testArrayPtr (): after forgetting the pointer and after second gc\n")
    
    val heapSizeAfter2 = GC2.GC_get_heap_size ()
    val freeBytesAfter2 = GC2.GC_get_free_bytes()
          
    fprintf(stdout, c"heap size: %d\n", heapSizeAfter2)
    fprintf(stdout, c"free bytes: %d\n", freeBytesAfter2)
    fprintf(stdout, c"\n")
    
    ()
  }

  /*
   * Allocates memory.
   *
   * The memory is reachable after this function return
   *
   * Return the Ptr to the memory
   */
   
  def useMemReturnPtr () : Ptr [Byte] = {
    import scalanative.runtime
    
    val ptr = GC.malloc (10000000).cast[Ptr[Byte]]
    
    fprintf(stdout, c"useMem (): before gc\n")
    
    fprintf(stdout, c"ptr address hex: %p, int: %d\n", ptr, ptr)
    
    val heapSizeBefore = GC2.GC_get_heap_size ()
    val freeBytesBefore = GC2.GC_get_free_bytes()
          
    fprintf(stdout, c"heap size: %d\n", heapSizeBefore)
    fprintf(stdout, c"free bytes: %d\n", freeBytesBefore)
    fprintf(stdout, c"\n")
    
    
    // the array is in used inside "useMem", so Boehm GC cannot free the memory    
    GC2.GC_gcollect ()
    
    val heapSizeAfter = GC2.GC_get_heap_size ()
    val freeBytesAfter = GC2.GC_get_free_bytes()
          
    fprintf(stdout, c"useMem (): after gc\n")
    
    fprintf(stdout, c"heap size: %d\n", heapSizeAfter)
    fprintf(stdout, c"free bytes: %d\n", freeBytesAfter)
    fprintf(stdout, c"\n")
    
    // do something with the memory (force the use of the array)
    !ptr = 'H'
    
    ptr
  }
}

// some external C library used in this test

// gc.h
@link("gc")
@extern 
object GC2 {
  @name("GC_gcollect")
  def GC_gcollect () : Unit = extern
  
  @name("GC_get_heap_size")
  def GC_get_heap_size(): CSize = extern
  
  @name("GC_get_free_bytes")
  def GC_get_free_bytes(): CSize = extern
}

// stdio.h
@extern
object stdio {
  def sprintf(s: CString, format: CString, args: Vararg*): CInt = extern
}

// stdlib.h
@extern
object stdlib2 {
  def atoi (s: CString): CInt = extern
}
