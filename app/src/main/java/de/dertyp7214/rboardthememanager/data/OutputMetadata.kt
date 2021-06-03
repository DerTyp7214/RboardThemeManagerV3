package de.dertyp7214.rboardthememanager.data

data class OutputMetadata(
    val version: String,
    val artifactType: ArtifactType,
    val applicationId: String,
    val variantName: String,
    val elements: List<Element>,
    val elementType: String
)

data class ArtifactType(
    val type: String,
    val kind: String
)

data class Element(
    val type: String,
    val filters: List<String>,
    val attributes: List<String>,
    val versionCode: Int,
    val versionName: String,
    val outputFile: String
)