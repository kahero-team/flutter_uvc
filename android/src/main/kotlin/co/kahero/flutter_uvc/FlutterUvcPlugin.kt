package co.kahero.flutter_uvc

import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.ICameraHelper

/** FlutterUvcPlugin */
class FlutterUvcPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context : Context

  private var mCameraHelper : ICameraHelper? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_uvc")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext

    // init ICameraHelper 
    if (mCameraHelper == null) {
      mCameraHelper = CameraHelper()
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "getDeviceList") {
      val deviceList = mCameraHelper?.getDeviceList()
      result.success(deviceList?.map { device -> device.getDeviceName() })
    } else if (call.method == "takePicture") {
      val path = context.getExternalFilesDir(null)?.getParent()
      result.success(path)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
