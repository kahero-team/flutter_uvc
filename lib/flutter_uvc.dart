import 'dart:async';

import 'package:flutter/services.dart';

import 'usb_device.dart';

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

  static Future<UsbDevice?> get device async {
    final resDevice = await _channel.invokeMethod('getDevice');
    return resDevice != null ? UsbDevice.fromJson(resDevice) : null;
  }

  static Future<String?> takePicture() async {
    final String? result = await _channel.invokeMethod("takePicture");
    return result;
  }

  static Future<bool> selectDevice(UsbDevice usbDevice) async {
    final bool result = await _channel.invokeMethod("selectDevice", {
      'deviceId': usbDevice.deviceId,
    });
    return result;
  }
}
