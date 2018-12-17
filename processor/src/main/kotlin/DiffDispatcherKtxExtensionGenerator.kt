package com.github.rougsig.diffdispatcherktx.processor

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.processor.utils.className
import com.github.rougsig.diffdispatcherktx.runtime.DiffDispatcherKtx
import com.squareup.kotlinpoet.*
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import kotlin.reflect.KClass

internal class DiffDispatcherKtxExtensionGenerator(
  private val targetElement: TypeElement,
  private val types: Types
) : Generator {
  private val name = targetElement.className
  private val packageName = name.packageName
  private val diffDispatcherName = "${name.simpleName}DiffDispatcher"
  private val diffDispatcherBuilder = ClassName.bestGuess("$packageName.$diffDispatcherName.Builder")

  override fun generateFile(): FileSpec {
    return FileSpec.builder(RUNTIME_PACKAGE, "${diffDispatcherName}Ktx")
      .addFunction(
        FunSpec
          .builder("target")
          .returns(diffDispatcherBuilder)
          .receiver(DiffDispatcherKtx::class)
          .addParameter(ParameterSpec.builder("target", getDiffReceiverTypeName()).build())
          .addStatement("return %T().target(target)", diffDispatcherBuilder)
          .build()
      )
      .build()
  }

  private fun AnnotationMirror.getFieldByName(fieldName: String): AnnotationValue? {
    return elementValues.entries
      .firstOrNull { (element, _) ->
        element.simpleName.toString() == fieldName
      }
      ?.value
  }

  private fun TypeElement.getAnnotationMirror(annotationClass: KClass<*>): AnnotationMirror? {
    return annotationMirrors
      .find { it.annotationType.asElement().simpleName.toString() == annotationClass.simpleName.toString() }
  }

  private fun getDiffReceiverTypeName(): TypeName {
    val annotation = targetElement.getAnnotationMirror(DiffElement::class)
      ?: throw IllegalArgumentException("State must by provided")
    val stateValue = annotation.getFieldByName("diffReceiver")
    val stateTypeMirror = stateValue!!.value as TypeMirror
    return (types.asElement(stateTypeMirror) as TypeElement).asType().asTypeName()
  }
}