/*
 * Copyright (c) 2013, 2022, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

#ifndef SIZECALC_H
#define SIZECALC_H

/*
 * A machinery for safe calculation of sizes used when allocating memory.
 *
 * All size checks are performed against the SIZE_MAX (the maximum value for
 * size_t). All numerical arguments as well as the result of calculation must
 * be non-negative integers less than or equal to SIZE_MAX, otherwise the
 * calculated size is considered unsafe.
 *
 * If the SIZECALC_ALLOC_THROWING_BAD_ALLOC macro is defined, then _ALLOC_
 * helper macros throw the std::bad_alloc instead of returning NULL.
 */

#include <stdint.h> /* SIZE_MAX for C99+ */
/* http://stackoverflow.com/questions/3472311/what-is-a-portable-method-to-find-the-maximum-value-of-size-t */
#ifndef SIZE_MAX
#define SIZE_MAX ((size_t)-1)
#endif

#define IS_SAFE_SIZE_T(x) ((x) >= 0 && (unsigned long long)(x) <= SIZE_MAX)

#define IS_MUL_OVERFLOW(m, n) \
        ((m) != 0 && (n) != 0 && (((size_t)((m)*(n))) != (((size_t)(m)) * ((size_t)(n)))))

#define IS_SAFE_SIZE_MUL(m, n) \
    (IS_SAFE_SIZE_T(m) && IS_SAFE_SIZE_T(n) && \
     ((m) == 0 || (n) == 0 || (size_t)(n) <= (SIZE_MAX / (size_t)(m))) && \
     !IS_MUL_OVERFLOW(m, n))

#define IS_SAFE_SIZE_ADD(a, b) \
    (IS_SAFE_SIZE_T(a) && IS_SAFE_SIZE_T(b) && (size_t)(b) <= (SIZE_MAX - (size_t)(a)))



/* Helper macros */

#ifdef SIZECALC_ALLOC_THROWING_BAD_ALLOC
#define FAILURE_RESULT throw std::bad_alloc()
#else
#define FAILURE_RESULT NULL
#endif

/*
 * A helper macro to safely allocate an array of size m*n.
 * Example usage:
 *    int* p = (int*)SAFE_SIZE_ARRAY_ALLOC(malloc, sizeof(int), n);
 *    if (!p) throw OutOfMemory;
 *    // Use the allocated array...
 */
#define SAFE_SIZE_ARRAY_ALLOC(func, m, n) \
    (IS_SAFE_SIZE_MUL((m), (n)) ? ((func)((m) * (n))) : FAILURE_RESULT)

#define SAFE_SIZE_ARRAY_REALLOC(func, p, m, n) \
    (IS_SAFE_SIZE_MUL((m), (n)) ? ((func)((p), (m) * (n))) : FAILURE_RESULT)

/*
 * A helper macro to safely allocate an array of type 'type' with 'n' items
 * using the C++ new[] operator.
 * Example usage:
 *    MyClass* p = SAFE_SIZE_NEW_ARRAY(MyClass, n);
 *    // Use the pointer.
 * This macro throws the std::bad_alloc C++ exception to indicate
 * a failure.
 * NOTE: if 'n' is calculated, the calling code is responsible for using the
 * IS_SAFE_... macros to check if the calculations are safe.
 */
#define SAFE_SIZE_NEW_ARRAY(type, n) \
    (IS_SAFE_SIZE_MUL(sizeof(type), (n)) ? (new type[(n)]) : throw std::bad_alloc())

#define SAFE_SIZE_NEW_ARRAY2(type, n, m) \
    (IS_SAFE_SIZE_MUL((m), (n)) && IS_SAFE_SIZE_MUL(sizeof(type), (n) * (m)) ? \
     (new type[(n) * (m)]) : throw std::bad_alloc())

/*
 * Checks if a data structure of size (a + m*n) can be safely allocated
 * w/o producing an integer overflow when calculating its size.
 */
#define IS_SAFE_STRUCT_SIZE(a, m, n) \
    ( \
      IS_SAFE_SIZE_MUL((m), (n)) && IS_SAFE_SIZE_ADD((m) * (n), (a)) \
    )

/*
 * A helper macro for implementing safe memory allocation for a data structure
 * of size (a + m * n).
 * Example usage:
 *    void * p = SAFE_SIZE_ALLOC(malloc, header, num, itemSize);
 *    if (!p) throw OutOfMemory;
 *    // Use the allocated memory...
 */
#define SAFE_SIZE_STRUCT_ALLOC(func, a, m, n) \
    (IS_SAFE_STRUCT_SIZE((a), (m), (n)) ? ((func)((a) + (m) * (n))) : FAILURE_RESULT)


#endif /* SIZECALC_H */

