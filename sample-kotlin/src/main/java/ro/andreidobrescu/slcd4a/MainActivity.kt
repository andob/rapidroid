package ro.andreidobrescu.slcd4a

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        Run.thread {
            println("hello!")

            Run.async {
                println("Hello procedure!")
                Thread.sleep(1000)
                println("Stop procedure!")
                return@async 4
            }
            .onAny { println("Any was called!") }
            .onError { println("It was error! $it") }
            .onSuccess { println("It was success! $it") }
        }
    }
}
