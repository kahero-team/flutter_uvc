package co.kahero.flutter_uvc

import android.content.Context
import android.hardware.usb.UsbDevice
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.StandardMessageCodec

/** FlutterUvcPlugin */
class FlutterUvcPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  private var mUvcViewFactory: UvcViewFactory? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_uvc")
    channel.setMethodCallHandler(this)

    context = flutterPluginBinding.applicationContext

    val uvcViewFactory = UvcViewFactory(StandardMessageCodec.INSTANCE, channel)
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("flutter_uvc_view", uvcViewFactory)

    mUvcViewFactory = uvcViewFactory
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "getDeviceList") {
      val deviceList = mUvcViewFactory?.getDeviceList()
      result.success(deviceList?.map { device -> serializeUsbDevice(device) })
    } else if (call.method == "takePicture") {
      mUvcViewFactory?.takePicture(result)
    } else if (call.method == "selectDevice") {
      val deviceId = call.argument("deviceId") as Int?

      val deviceList = mUvcViewFactory?.getDeviceList()
      if (deviceList == null) {
        result.error("selectDeviceError", "There are no devices to select", null)
      }

      val usbDevice = deviceList!!.filter { device -> device.getDeviceId() == deviceId }.single()
      if (usbDevice == null) {
        result.error("selectDeviceError", "There's no device with the id " + deviceId, null)
      }

      mUvcViewFactory?.selectDevice(usbDevice)
      result.success(null)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun serializeUsbDevice(device: UsbDevice): MutableMap<String, Any?> {
    val serializedData: MutableMap<String, Any?> = HashMap()
    serializedData.put("deviceId", device.getDeviceId())
    serializedData.put("deviceName", device.getDeviceName())
    serializedData.put("productName", device.getProductName())
    return serializedData
  }
}
