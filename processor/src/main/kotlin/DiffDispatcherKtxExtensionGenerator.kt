package com.github.rougsig.diffdispatcherktx.processor

import com.github.rougsig.diffdispatcherktx.runtime.DiffDispatcher
import com.squareup.kotlinpoet.*

internal class DiffDispatcherKtxExtensionGenerator(
  private val diffElements: List<DiffElementType>
) : Generator {
  override fun generateFile(): FileSpec {
    return FileSpec.builder(RUNTIME_PACKAGE, "DiffDispatcher")
      .apply {
        diffElements.forEach { el ->
          addFunction(
            FunSpec
              .builder("target")
              .apply { if (el.isInternal) addModifiers(KModifier.INTERNAL) }
              .returns(el.diffDispatcherBuilderClassName)
              .receiver(DiffDispatcher::class)
              .addParameter(ParameterSpec.builder("target", el.diffReceiverTypeName).build())
              .addStatement("return %T().target(target)", el.diffDispatcherBuilderClassName)
              .build()
          )
        }
      }
      .build()
  }
}