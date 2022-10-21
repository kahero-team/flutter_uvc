package co.kahero.flutter_uvc

import android.content.Context
import android.hardware.usb.UsbDevice

import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class UvcViewFactory: PlatformViewFactory {
    private var mChannel: MethodChannel

    private var mViewId: Int? = null
    private var mUvcView: UvcView? = null

    constructor(createArgsCodec: MessageCodec<Any>, channel: MethodChannel): super(createArgsCodec) {
        mChannel = channel   
    }

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val uvcView = UvcView(context, mChannel)
        mUvcView = uvcView
        mViewId = viewId
        return uvcView
    }

    fun takePicture(result: MethodChannel.Result) {
        mUvcView?.takePicture(result)
    }

    fun getDeviceList(): List<UsbDevice>? {
        return mUvcView?.getDeviceList()
    }

    fun selectDevice(device: UsbDevice) {
        mUvcView?.selectDevice(device)
    }
}
