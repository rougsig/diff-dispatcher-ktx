package com.github.rougsig.diffdispatcherktx.runtime

import java.lang.IllegalStateException

object DiffDispatcher

fun DiffDispatcher.target(target: Any): DiffDispatcherBuilder {
  throw IllegalStateException("DiffDispatcher class not generated.")
}