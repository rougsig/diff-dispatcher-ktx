package com.github.rougsig.diffdispatcherktx.runtime

import java.lang.IllegalStateException

object DiffDispatcherKtx

fun DiffDispatcherKtx.target(target: Any): DiffDispatcherBuilder {
  throw IllegalStateException("DiffDispatcher class not generated.")
}