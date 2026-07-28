package com.maptanim.app.ui.components.isometric.camera

class CameraController(
    private val cameraState: CameraState
) {

    fun zoomBy(scale: Float) {

        cameraState.zoom =
            (cameraState.zoom * scale)
                .coerceIn(
                    cameraState.minZoom,
                    cameraState.maxZoom
                )

    }

    fun pan(dx: Float, dy: Float) {

        cameraState.offsetX =
            (cameraState.offsetX + dx)
                .coerceIn(
                    -cameraState.maxPanX,
                    cameraState.maxPanX
                )

        cameraState.offsetY =
            (cameraState.offsetY + dy)
                .coerceIn(
                    -cameraState.maxPanY,
                    cameraState.maxPanY
                )

    }

    fun centerOnFarm() {

        cameraState.offsetX = 0f
        cameraState.offsetY = 0f
        cameraState.zoom = cameraState.minZoom

    }

}