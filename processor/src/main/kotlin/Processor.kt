package com.github.rougsig.diffdispatcherktx.processor

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.github.rougsig.diffdispatcherktx.processor.utils.Logger
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.TypeSpec
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import java.io.File
import java.lang.IllegalStateException
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class Processor : KotlinAbstractProcessor() {

  companion object {
    const val OPTION_GENERATED = "diffdispatcherktx.generated"
    private val POSSIBLE_GENERATED_NAMES = setOf(
      "javax.annotation.processing.Generated",
      "javax.annotation.Generated"
    )
  }

  private val annotation = DiffElement::class.java
  private var generatedType: TypeElement? = null
  private lateinit var logger: Logger

  override fun getSupportedAnnotationTypes() = setOf(annotation.canonicalName)

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

  override fun getSupportedOptions() = setOf(OPTION_GENERATED)

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    logger = Logger(messager)
    generatedType = processingEnv.options[OPTION_GENERATED]?.let {
      if (it !in POSSIBLE_GENERATED_NAMES) {
        throw IllegalArgumentException(
          "Invalid option value for $OPTION_GENERATED. Found $it, " +
              "allowable values are $POSSIBLE_GENERATED_NAMES."
        )
      }
      processingEnv.elementUtils.getTypeElement(it)
    }
  }

  override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    for (type in roundEnv.getElementsAnnotatedWith(annotation)) {
      val targetElement = type as TypeElement
      DiffDispatcherKtxExtensionGenerator(targetElement, typeUtils).generateAndWrite()
    }
    return false // not claiming the annotation
  }

  private fun Generator.generateAndWrite() {
    val fileSpec = generateFile()
    val outputDir = generatedDir ?: mavenGeneratedDir(fileSpec.name)
    fileSpec.writeTo(outputDir)
  }

  private fun mavenGeneratedDir(adapterName: String): File {
    // Hack since the maven plugin doesn't supply `kapt.kotlin.generated` option
    // Bug filed at https://youtrack.jetbrains.com/issue/KT-22783
    val file = filer.createSourceFile(adapterName).toUri().let(::File)
    return file.parentFile.also { file.delete() }
  }
}