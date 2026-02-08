.class public Main
.super java/lang/Object
.method public static main([Ljava/lang/String;)V
.limit locals 100
.limit stack 100
ldc 10
istore 0
ldc 2.5
fstore 1
iload 0
ldc 2
imul
ldc 5
iadd
istore 2
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 2
invokevirtual java/io/PrintStream/println(I)V
fload 1
ldc 1.5
fadd
fstore 3
getstatic java/lang/System/out Ljava/io/PrintStream;
fload 3
invokevirtual java/io/PrintStream/println(F)V
ldc 100
istore 4
iload 4
ldc 2
idiv
istore 4
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 4
invokevirtual java/io/PrintStream/println(I)V
return
.end method
