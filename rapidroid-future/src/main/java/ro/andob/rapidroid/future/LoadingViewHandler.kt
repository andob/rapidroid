package ro.andob.rapidroid.future

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ro.andob.rapidroid.CancellationToken
import ro.andob.rapidroid.Consumer
import ro.andob.rapidroid.Procedure

class LoadingViewHandler
(
    val lifecycleOwner : LifecycleOwner,
    val showLoadingView : Procedure = Procedure {},
    val hideLoadingView : Procedure = Procedure {},
    val cancellationToken : CancellationToken? = null,
) : LifecycleOwner
{
    override fun getLifecycle() : Lifecycle = lifecycleOwner.lifecycle

    class Builder<LIFECYCLE_OWNER : LifecycleOwner>(private val lifecycleOwner : LIFECYCLE_OWNER)
    {
        private var showLoadingView : Procedure = Procedure {}
        fun withShowLoadingViewFunction(lambda : Consumer<LIFECYCLE_OWNER>) = apply {
            showLoadingView=Procedure { lambda.accept(lifecycleOwner) }
        }

        private var hideLoadingView : Procedure = Procedure {}
        fun withHideLoadingViewFunction(lambda : Consumer<LIFECYCLE_OWNER>) = apply {
            hideLoadingView=Procedure { lambda.accept(lifecycleOwner) }
        }

        private var cancellationToken : CancellationToken? = null
        fun withLoadingViewCancellationToken(token : CancellationToken) = apply { cancellationToken=token }

        fun build() = LoadingViewHandler(lifecycleOwner, showLoadingView, hideLoadingView, cancellationToken)
    }
}
