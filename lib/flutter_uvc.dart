import 'dart:async';

import 'package:flutter/services.dart';

class FlutterUvc {
  static const MethodChannel _channel = MethodChannel('flutter_uvc');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<List<String>> get deviceList async {
    final List resDeviceList = await _channel.invokeMethod('getDeviceList');
    return resDeviceList.map((device) => device as String).toList();
  }

  static Future<String?> takePicture() async {
    final String? result = await _channel.invokeMethod("takePicture");
    return result;
  }
}
