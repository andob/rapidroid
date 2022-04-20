package ro.andob.rapidroid.future

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ro.andob.rapidroid.CancellationToken
import ro.andob.rapidroid.Consumer

class LoadingViewHandler<LIFECYCLE_OWNER : LifecycleOwner>
(
    val lifecycleOwner : LIFECYCLE_OWNER,
    val showLoadingView : Consumer<LIFECYCLE_OWNER> = Consumer {},
    val hideLoadingView : Consumer<LIFECYCLE_OWNER> = Consumer {},
    val cancellationToken : CancellationToken? = null,
) : LifecycleOwner
{
    override fun getLifecycle() : Lifecycle = lifecycleOwner.lifecycle

    class Builder<LIFECYCLE_OWNER : LifecycleOwner>(private val lifecycleOwner : LIFECYCLE_OWNER)
    {
        private var showLoadingView : Consumer<LIFECYCLE_OWNER> = Consumer {}
        fun withShowLoadingViewFunction(lambda : Consumer<LIFECYCLE_OWNER>) = apply { showLoadingView=lambda }

        private var hideLoadingView : Consumer<LIFECYCLE_OWNER> = Consumer {}
        fun withHideLoadingViewFunction(lambda : Consumer<LIFECYCLE_OWNER>) = apply { hideLoadingView=lambda }

        private var cancellationToken : CancellationToken? = null
        fun withLoadingViewCancellationToken(token : CancellationToken) = apply { cancellationToken=token }

        fun build() = LoadingViewHandler(lifecycleOwner, showLoadingView, hideLoadingView, cancellationToken)
    }
}
