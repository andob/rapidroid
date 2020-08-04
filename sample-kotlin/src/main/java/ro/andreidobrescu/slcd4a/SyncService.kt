package ro.andreidobrescu.slcd4a

import ro.andreidobrescu.slcd4a.thread.ThreadIsRunningFlag

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
