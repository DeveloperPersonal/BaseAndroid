package com.datastore.job

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class CustomRepeatingJob(
    private val delay: Long = 100L,
    private val progressMax: Int = 100
) {

    interface RepeatingJobListener {
        fun onRepeatingJobChange(progress: Int)
    }

    private var progress = 0
    private var job: Job? = null
    private var listener: RepeatingJobListener? = null

    private var flowRepeating = flow {
        while (true) {
            emit(Unit)
            delay(delay)
        }
    }

    fun runJob(
        life: LifecycleCoroutineScope? = null,
        l: RepeatingJobListener? = null
    ) {
        cancelJob()
        this.listener = l
        if (progress >= progressMax) {
            listener?.onRepeatingJobChange(progress)
            return
        }
        job = life?.launch(Dispatchers.IO) {
            flowRepeating.collect {
                progress++
                withContext(Dispatchers.Main) {
                    listener?.onRepeatingJobChange(progress)
                }
                if (progress >= progressMax) {
                    cancelJob()
                }
            }
        }
    }

    fun cancelJob() {
        job?.cancel()
        job = null
        listener = null
    }

}