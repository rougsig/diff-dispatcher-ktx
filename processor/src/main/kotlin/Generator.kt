package com.github.rougsig.diffdispatcherktx.processor

import com.squareup.kotlinpoet.FileSpec

internal interface Generator {
  fun generateFile(): FileSpec
}