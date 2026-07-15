package com.maptanim.app.ui.components.isometric.camera

class CameraController(

    private val cameraState: CameraState

) {

    companion object {

        const val MIN_ZOOM = 0.6f
        const val MAX_ZOOM = 4f

    }

    fun zoomIn() {

        cameraState.zoom =
            (cameraState.zoom * 1.1f)
                .coerceAtMost(MAX_ZOOM)

    }

    fun zoomOut() {

        cameraState.zoom =
            (cameraState.zoom / 1.1f)
                .coerceAtLeast(MIN_ZOOM)

    }

    fun setZoom(zoom: Float) {

        cameraState.zoom =
            zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)

    }

    fun pan(

        dx: Float,

        dy: Float

    ) {

        cameraState.offsetX += dx

        cameraState.offsetY += dy

    }

    fun centerOn(

        row: Int,

        column: Int

    ) {

        cameraState.cameraRow = row
        cameraState.cameraColumn = column

        cameraState.offsetX = 0f
        cameraState.offsetY = 0f

    }

    fun reset() {

        cameraState.zoom = 1f

        cameraState.offsetX = 0f
        cameraState.offsetY = 0f

    }

}