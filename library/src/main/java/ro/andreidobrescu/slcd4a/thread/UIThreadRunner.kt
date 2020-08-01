package ro.andreidobrescu.slcd4a.thread

import ro.andreidobrescu.slcd4a.SLCD4A
import ro.andreidobrescu.slcd4a.functional_interfaces.Procedure
import ro.andreidobrescu.slcd4a.functional_interfaces.toRunnable

object UIThreadRunner
{
    val implementation : (Procedure) -> (Unit) by lazy {

        try
        {
            val androidHandlerClass=Class.forName("android.os.Handler")!!
            val looperClass=Class.forName("android.os.Looper")!!
            val mainLooper=looperClass.getDeclaredMethod("getMainLooper").invoke(null)!!

            val handler=androidHandlerClass.declaredConstructors.find { constructor ->
                constructor.parameterTypes.size==1&&
                constructor.parameterTypes.first()==looperClass
            }!!.newInstance(mainLooper)

            val postMethod=androidHandlerClass.declaredMethods.find { method ->
                method.name=="post"&&
                method.parameterTypes.size==1&&
                method.parameterTypes.first()==Runnable::class.java
            }!!

            return@lazy implementation@ { toRun : Procedure ->
                try { postMethod.invoke(handler, toRun.toRunnable()) }
                catch (ex : Throwable) { SLCD4A.exceptionLogger.log(ex) }
                return@implementation Unit
            }
        }
        catch (ex : ClassNotFoundException)
        {
            val awtEventQueueClass=Class.forName("java.awt.EventQueue")

            val invokeLaterMethod=awtEventQueueClass.declaredMethods.find { method ->
                method.parameterTypes.size==1&&
                        method.parameterTypes.first()==Runnable::class.java
            }!!

            return@lazy implementation@ { toRun : Procedure ->
                try { invokeLaterMethod.invoke(null, toRun.toRunnable()) }
                catch (ex : Throwable) { SLCD4A.exceptionLogger.log(ex) }
                return@implementation Unit
            }
        }
    }
}
