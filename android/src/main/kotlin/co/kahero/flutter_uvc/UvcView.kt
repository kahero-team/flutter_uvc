package co.kahero.flutter_uvc

import android.view.View
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.content.Context
import android.hardware.usb.UsbDevice

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

import com.serenegiant.widget.AspectRatioSurfaceView

import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper

class UvcView: PlatformView, SurfaceHolder.Callback, ICameraHelper.StateCallback {
    private var mNativeView: View
    private var mCameraView: AspectRatioSurfaceView
    private var mCameraHelper: ICameraHelper

    constructor(context: Context, channel: MethodChannel) {
        mNativeView = LayoutInflater.from(context).inflate(R.layout.camera_view, null, false)
        mCameraView = mNativeView.findViewById(R.id.surface_view)
        mCameraView.setAspectRatio(640, 480)
        mCameraView.getHolder().addCallback(this)

        mCameraHelper = CameraHelper()
        mCameraHelper.setStateCallback(this)
    }

    override fun getView(): View {
        return mNativeView
    }

    override fun dispose() {
        mCameraHelper.release()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mCameraHelper.addSurface(surfaceHolder.getSurface(), false)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mCameraHelper.removeSurface(surfaceHolder.getSurface())
    }

    override fun onAttach(device: UsbDevice) {
        mCameraHelper.selectDevice(device)
    }

    override fun onDeviceOpen(device: UsbDevice, isFirstOpen: Boolean) {
        mCameraHelper.openCamera()
    }

    override fun onCameraOpen(device: UsbDevice) {
        mCameraHelper.startPreview()
        mCameraHelper.addSurface(mCameraView.getHolder().getSurface(), false)
    }

    override fun onCameraClose(device: UsbDevice) {
        mCameraHelper.removeSurface(mCameraView.getHolder().getSurface())
    }

    override fun onDeviceClose(device: UsbDevice) {
        println("onDeviceClose")
    }

    override fun onDetach(device: UsbDevice) {
        println("onDetach")
    }

    override fun onCancel(device: UsbDevice) {
        println("onCancel")
    }
}
