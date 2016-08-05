;; Produced with help from JasminVisitor program (BCEL)
;; http://bcel.sourceforge.net/
;; Sun May 15 08:19:15 PDT 2016

.source $MainDriver.java
.class public $MainDriver
.super java/lang/Object
.implements kareltherobot/Directions

.field private static startTime J

.method public <init>()V
.limit stack 1
.limit locals 1
.var 0 is this L$MainDriver; from Label0 to Label1

Label0:
	aload_0
	invokespecial java/lang/Object/<init>()V
Label1:
	return

.end method

.method public static main([Ljava/lang/String;)V
.limit stack 6
.limit locals 2
.var 0 is arg0 [Ljava/lang/String; from KLabel0 to KLabelNoExceptionPath0

Label3:
	invokestatic java/lang/System/currentTimeMillis()J
	putstatic $MainDriver/startTime J
Label1:
	invokestatic java/lang/System/currentTimeMillis()J
	getstatic $MainDriver/startTime J
	lsub
	ldc2_w 3000
	lcmp
	ifge KLabel0
	goto Label1
KLabel0:
	new ABCBot
	dup
	bipush 8
	bipush 4
	getstatic $MainDriver/North Lkareltherobot/Directions$Direction;
	bipush 0
	invokespecial ABCBot/<init>(IILkareltherobot/Directions$Direction;I)V
	astore_1
	aload_1
KLabelInvokeTask0:
	invokeinterface kareltester/TestableKarel/task()V 1
	goto KLabelNoExceptionPath0
KLabelCatch0:
	astore_1
KLabelNoExceptionPath0:
	
	return
.catch java/lang/Exception from KLabel0 to KLabelInvokeTask0 using KLabelCatch0
.end method
.method static <clinit>()V
.limit stack 1
.limit locals 0

	invokestatic kareltherobot/World/reset()V
	ldc "$KarelsHome.kwld"
	invokestatic kareltherobot/World/readWorld(Ljava/lang/String;)V
	bipush 50
	invokestatic kareltherobot/World/setDelay(I)V
	iconst_1
	invokestatic kareltherobot/World/setVisible(Z)V
	iconst_1
	invokestatic kareltherobot/World/showSpeedControl(Z)V
	return

.end method
