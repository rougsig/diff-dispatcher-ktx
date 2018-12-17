package com.github.rougsig.diffdispatcherktx.runtime

interface DiffDispatcherBuilder {
  fun <T> build(): T
}