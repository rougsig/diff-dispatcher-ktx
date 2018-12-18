package com.github.rougsig.diffdispatcherktx.processor

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.processor.utils.className
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.visibility
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
  val diffReceiverTypeName: TypeName,
  val isInternal: Boolean
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
      val diffReceiverElement = getDiffReceiverElement(targetElement, types)
      val diffReceiverTypeName = diffReceiverElement.asType().asTypeName()

      val typeMetadata = diffReceiverElement.kotlinMetadata as? KotlinClassMetadata
      val proto = typeMetadata?.data?.classProto
      val isInternal = proto?.visibility == ProtoBuf.Visibility.INTERNAL

      return DiffElementType(
        name = name,
        packageName = packageName,
        diffDispatcherName = diffDispatcherName,
        diffDispatcherBuilderClassName = diffDispatcherBuilderClassName,
        diffReceiverTypeName = diffReceiverTypeName,
        isInternal = isInternal
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

    private fun getDiffReceiverElement(targetElement: TypeElement, types: Types): TypeElement {
      val annotation = targetElement.getAnnotationMirror(DiffElement::class)
        ?: throw IllegalArgumentException("State must by provided")
      val stateValue = annotation.getFieldByName("diffReceiver")
      val stateTypeMirror = stateValue!!.value as TypeMirror
      return (types.asElement(stateTypeMirror) as TypeElement)
    }
  }
}