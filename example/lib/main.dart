import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_uvc/flutter_uvc.dart';
import 'package:flutter_uvc/usb_device.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String? _filePath;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await FlutterUvc.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // try {
    //   final String? path = await FlutterUvc.takePicture();
    // } on PlatformException {
    //   platformVersion = 'Failed to get picture path.';
    // }

    // try {
    //   final deviceList = await FlutterUvc.deviceList;
    //   print(deviceList);
    // } on PlatformException {
    //   print("Unable to get device list");
    // }

    // try {
    //   final device = await FlutterUvc.device;
    //   print(device?.productName ?? '');
    // } on PlatformException {
    //   print("Unable to get device.");
    // }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter UVC Example'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              const SizedBox(
                width: 640,
                height: 480,
                child: AndroidView(viewType: "flutter_uvc_view"),
              ),
              TextButton(
                child: const Text("Take picture"),
                onPressed: () async {
                  try {
                    final res = await FlutterUvc.takePicture();
                    setState(() {
                      _filePath = res;
                    });
                  } on PlatformException {
                    print("Take picture");
                  }
                },
              ),
              const DialogDeviceList(),
              if (_filePath != null) Image.file(File(_filePath!)),
            ],
          ),
        ),
      ),
    );
  }
}

class DialogDeviceList extends StatelessWidget {
  const DialogDeviceList({Key? key}) : super(key: key);

  Future<void> handleSelectDevice(context) async {
    try {
      final deviceList = await FlutterUvc.deviceList;
      promptDeviceList(context, deviceList);
    } on PlatformException {
      print("Unable to get devices.");
    }
    return Future.value(null);
  }

  Future<void> promptDeviceList(
    BuildContext context,
    List<UsbDevice> deviceList,
  ) async {
    final selectedDevice = await showDialog(
      context: context,
      builder: (BuildContext context) {
        return SimpleDialog(
          title: const Text("Select device"),
          children: deviceList
              .map(
                (device) => SimpleDialogOption(
                  onPressed: () {
                    Navigator.pop(context, device);
                  },
                  child: Column(
                    children: [
                      Text(device.productName ?? ''),
                      Text(device.deviceName),
                    ],
                  ),
                ),
              )
              .toList(),
        );
      },
    );
    try {
      final res = await FlutterUvc.selectDevice(selectedDevice);
      if (res) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("Device selected."),
          ),
        );
      }
    } on PlatformException {
      print("Unable to select devices.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return TextButton(
      child: const Text("Select device"),
      onPressed: () => handleSelectDevice(context),
    );
  }
}
