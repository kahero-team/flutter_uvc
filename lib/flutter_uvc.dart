import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

import 'usb_device.dart';
import 'uvc_exception.dart';

class FlutterUvc {
  static const MethodChannel _channel = MethodChannel('flutter_uvc');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<List<UsbDevice>> get deviceList async {
    final List<UsbDevice> usbDeviceList = [];
    final List resDeviceList =
        await _channel.invokeMethod('getDeviceList') ?? [];
    resDeviceList.forEach((device) {
      usbDeviceList.add(UsbDevice.fromJson(device));
    });
    return usbDeviceList;
  }

  static Future<File> takePicture() async {
    final String? path = await _channel.invokeMethod("takePicture");
    if (path == null) {
      throw UvcException("INVALID_PATH", "Unable to find the picture needed");
    }
    return File(path);
  }

  static Future<bool> selectDevice(UsbDevice usbDevice) async {
    final bool result = await _channel.invokeMethod("selectDevice", {
      'deviceId': usbDevice.deviceId,
    });
    return result;
  }
}
