package ro.andreidobrescu.rapidroid

import ro.andreidobrescu.rapidroid.thread.ThreadIsRunningFlag

object SyncService
{
    private val isRunning = ThreadIsRunningFlag()
    fun isRunning() = isRunning.get()

    fun start()
    {
        Run.threadIfNotAlreadyRunning(
            threadIsRunningFlag = isRunning,
            task = {
                Run.workflow {
                    sequential {
                        task { println("1") }
                        parallel {
                            task { println("2") }
                            task { println("3") }
                            task { println("4") }
                        }
                        parallelList(
                            itemsProvider = { listOf(6,7,8) },
                            itemSubtask = { println(it) })
                        task { println("5") }
                    }
                }
            })
    }
}
