class UvcException implements Exception {
  UvcException(this.code, this.description);

  String code;
  String? description;

  @override
  String toString() => 'UvcException($code, $description)';
}
