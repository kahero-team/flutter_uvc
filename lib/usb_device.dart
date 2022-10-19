class UsbDevice {
  UsbDevice({
    required this.deviceId,
    required this.deviceName,
    required this.productName,
  });

  final int deviceId;
  final String deviceName;
  final String? productName;

  static UsbDevice fromJson(dynamic json) => UsbDevice(
        deviceId: json["deviceId"],
        deviceName: json["deviceName"],
        productName: json["productName"],
      );
}
