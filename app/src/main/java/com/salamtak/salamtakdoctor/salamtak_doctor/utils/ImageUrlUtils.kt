package com.salamtak.salamtakdoctor.salamtak_doctor.utils

object ImageUrlUtils {

    // Images live in blob storage; the container name is lowercase and case-sensitive.
    private const val BLOB_BASE_URL = "https://salamtak.blob.core.windows.net/"
    private const val IMAGES_PREFIX = "images/"

    /**
     * Turns a relative path such as "/Images/MedicalType/Clinic1.png" (returned by some
     * endpoints) into a full URL. Absolute URLs and blank values are returned unchanged.
     */
    fun toFullUrl(path: String?): String? {
        if (path.isNullOrBlank()) return path
        if (path.startsWith("http://", true) || path.startsWith("https://", true)) return path
        val relative = path.trimStart('/')
        val withContainer = if (relative.startsWith(IMAGES_PREFIX, ignoreCase = true)) {
            IMAGES_PREFIX + relative.substring(IMAGES_PREFIX.length)
        } else {
            IMAGES_PREFIX + relative
        }
        return BLOB_BASE_URL + withContainer
    }
}
