package com.github.rougsig.diffdispatcherktx.runtime

import com.github.dimsuz.diffdispatcher.DiffDispatcher

interface DiffDispatcherBuilder {
  fun build(): DiffDispatcher<Any>
}