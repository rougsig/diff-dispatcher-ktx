package com.github.rougsig.diffdispatcherktx.sample

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.runtime.DiffDispatcher
import com.github.rougsig.diffdispatcherktx.runtime.target
import com.github.dimsuz.diffdispatcher.DiffDispatcher as BaseDiffDispatcher

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
    override val diffDispatcher = DiffDispatcher.target(renderer).build()
  }

  val dispatcher: BaseDiffDispatcher<ViewState> = DiffDispatcher.target(renderer).build()
  println("Generated")
}

interface Config<VS> {
  val diffDispatcher: BaseDiffDispatcher<VS>?
}