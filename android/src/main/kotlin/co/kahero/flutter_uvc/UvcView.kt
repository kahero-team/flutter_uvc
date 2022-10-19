package co.kahero.flutter_uvc

import android.view.View
import android.view.LayoutInflater
import android.content.Context

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

import com.serenegiant.widget.AspectRatioSurfaceView

class UvcView: PlatformView {
    private var mNativeView: View
    private var mCameraView: AspectRatioSurfaceView

    constructor(context: Context, channel: MethodChannel) {
        mNativeView = LayoutInflater.from(context).inflate(R.layout.camera_view, null, false)
        mCameraView = mNativeView.findViewById(R.id.surface_view)
    }

    override fun getView(): View {
        return mNativeView
    }

    override fun dispose() {

    }
}
