package com.github.rougsig.diffdispatcherktx.processor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement

val Element.className: ClassName
  get() {
    val typeName = asType().asTypeName()
    return when (typeName) {
      is ClassName -> typeName
      is ParameterizedTypeName -> typeName.rawType
      else -> throw IllegalStateException("unexpected TypeName: ${typeName::class}")
    }
  }