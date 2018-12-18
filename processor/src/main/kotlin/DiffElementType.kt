package com.github.rougsig.diffdispatcherktx.processor

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.processor.utils.className
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import kotlin.reflect.KClass

data class DiffElementType(
  val name: ClassName,
  val packageName: String,
  val diffDispatcherName: String,
  val diffDispatcherBuilderClassName: ClassName,
  val diffReceiverTypeName: TypeName
) {
  companion object {
    fun get(
      targetElement: TypeElement,
      types: Types
    ): DiffElementType {
      val name = targetElement.className
      val packageName = name.packageName
      val diffDispatcherName = "${name.simpleName}DiffDispatcher"
      val diffDispatcherBuilderClassName = ClassName.bestGuess("$packageName.$diffDispatcherName.Builder")
      val diffReceiverTypeName = getDiffReceiverTypeName(targetElement, types)

      return DiffElementType(
        name = name,
        packageName = packageName,
        diffDispatcherName = diffDispatcherName,
        diffDispatcherBuilderClassName = diffDispatcherBuilderClassName,
        diffReceiverTypeName = diffReceiverTypeName
      )
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

    private fun getDiffReceiverTypeName(targetElement: TypeElement, types: Types): TypeName {
      val annotation = targetElement.getAnnotationMirror(DiffElement::class)
        ?: throw IllegalArgumentException("State must by provided")
      val stateValue = annotation.getFieldByName("diffReceiver")
      val stateTypeMirror = stateValue!!.value as TypeMirror
      return (types.asElement(stateTypeMirror) as TypeElement).asType().asTypeName()
    }
  }
}