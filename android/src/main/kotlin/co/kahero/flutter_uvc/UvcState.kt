package co.kahero.flutter_uvc

/**
 * States that the camera can be in. The camera can only take one photo at a time
 */
enum class UvcState {
    /** Idle, showing preview, and not capturing anything */
    STATE_PREVIEW,

    /** Capturing an image */
    STATE_CAPTURING,
}
