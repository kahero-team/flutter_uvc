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

import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper

/** FlutterUvcPlugin */
class FlutterUvcPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  private var mCameraHelper: ICameraHelper? = null
  private var mUsbDevice: UsbDevice? = null
  private var mUvcViewFactory: UvcViewFactory? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_uvc")
    channel.setMethodCallHandler(this)

    context = flutterPluginBinding.applicationContext

    mUvcViewFactory = UvcViewFactory(StandardMessageCodec.INSTANCE, channel)
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("flutter_uvc_view", mUvcViewFactory)

    // init ICameraHelper 
    if (mCameraHelper == null) {
      val cameraHelper = CameraHelper()
      val firstDevice = cameraHelper.getDeviceList().first()
      mUsbDevice = firstDevice
      mCameraHelper = cameraHelper
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "getDeviceList") {
      val deviceList = mCameraHelper?.getDeviceList()
      result.success(deviceList?.map { device -> serializeUsbDevice(device) })
    } else if (call.method == "takePicture") {
      val path = context.getExternalFilesDir(null)?.getParent()
      result.success(path)
    } else if (call.method == "getDevice") {
      val usbDevice: UsbDevice? = mUsbDevice
      result.success(if (usbDevice != null) serializeUsbDevice(usbDevice) else null)
    } else if (call.method == "selectDevice") {
      val deviceId = call.argument("deviceId") as Int?
      if (deviceId != null) {
        mUsbDevice = mCameraHelper?.getDeviceList()?.filter { device -> device.getDeviceId() == deviceId}?.single()
        result.success(true)
      } else {
        result.success(false)
      }
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
