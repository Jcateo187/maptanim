package com.maptanim.app.ui.components.isometric.camera

class CameraController(

    private val cameraState: CameraState

) {

    fun zoomIn() {

        cameraState.zoom *= 1.1f

    }

    fun zoomOut() {

        cameraState.zoom /= 1.1f

    }

    fun setZoom(

        zoom: Float

    ) {

        cameraState.zoom = zoom

    }

    fun pan(

        dx: Float,

        dy: Float

    ) {

        cameraState.offsetX += dx

        cameraState.offsetY += dy

    }

    fun reset() {

        cameraState.zoom = 1f

        cameraState.offsetX = 0f

        cameraState.offsetY = 0f

    }

}