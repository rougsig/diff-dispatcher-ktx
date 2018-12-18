package com.github.rougsig.diffdispatcherktx.processor

import com.github.rougsig.diffdispatcherktx.runtime.DiffDispatcherKtx
import com.squareup.kotlinpoet.*

internal class DiffDispatcherKtxExtensionGenerator(
  private val diffElements: List<DiffElementType>
) : Generator {
  override fun generateFile(): FileSpec {
    return FileSpec.builder(RUNTIME_PACKAGE, "DiffDispatcherKtx")
      .apply {
        diffElements.forEach { el ->
          addFunction(
            FunSpec
              .builder("target")
              .apply { if (el.isInternal) addModifiers(KModifier.INTERNAL) }
              .returns(el.diffDispatcherBuilderClassName)
              .receiver(DiffDispatcherKtx::class)
              .addParameter(ParameterSpec.builder("target", el.diffReceiverTypeName).build())
              .addStatement("return %T().target(target)", el.diffDispatcherBuilderClassName)
              .build()
          )
        }
      }
      .build()
  }
}