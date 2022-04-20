package ro.andob.rapidroid.future

import ro.andob.rapidroid.Procedure

object FutureDefaults
{
    @JvmSynthetic internal var beforeAnyFuture : Procedure? = null
    @JvmSynthetic fun beforeAnyFuture(before : () -> Unit) { beforeAnyFuture=Procedure { before.invoke() } }
    fun beforeAnyFuture(before : Procedure) { beforeAnyFuture=before }

    @JvmSynthetic internal var afterAnyFuture : Procedure? = null
    @JvmSynthetic fun afterAnyFuture(after : () -> Unit) { afterAnyFuture=Procedure { after.invoke() } }
    fun afterAnyFuture(after : Procedure) { afterAnyFuture=after }
}
