package com.github.rougsig.diffdispatcherktx.sample

import com.github.dimsuz.diffdispatcher.DiffDispatcher
import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.runtime.DiffDispatcherKtx
import com.github.rougsig.diffdispatcherktx.runtime.target

@DiffElement(Screen.Renderer::class)
data class ViewState(
  val catName: String,
  val duckName: String
)

internal interface Screen {
  interface Renderer {
    fun renderCatName(catName: String)
    fun renderDuckName(duckName: String)
  }
}

fun main(args: Array<String>) {
  val renderer = object : Screen.Renderer {
    override fun renderCatName(catName: String) = Unit
    override fun renderDuckName(duckName: String) = Unit
  }

  val config = object : Config<ViewState> {
    override val diffDispatcher = DiffDispatcherKtx.target(renderer).build()
  }

  val dispatcher: DiffDispatcher<ViewState> = DiffDispatcherKtx.target(renderer).build()
  println("Generated")
}

interface Config<VS> {
  val diffDispatcher: DiffDispatcher<VS>?
}