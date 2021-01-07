import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:convert';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  AndroidNotificationListener _notifications;//4
  StreamSubscription<NotificationEventV2> _subscription;
var jsonDatax = "";
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),

        ),

        body: Container(child: Text(jsonDatax.toString(),

        style: TextStyle(fontSize: 25),
        )
        ),
      ),

    );

  }


  @override
  void initState() {
    super.initState();
    initPlatformState();//1
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    startListening();//2
  }

  void onData(NotificationEventV2 event) {

    print(event);
    print('converting package extra to json');

    jsonDatax = event.packageName;
    print(jsonDatax);
  }

  void startListening() {
    _notifications = new AndroidNotificationListener();//3
    try {
      _subscription = _notifications.notificationStream.listen(onData);//5
    } on NotificationExceptionV2 catch (exception) {
      print(exception);
    }
  }

  void stopListening() {
    _subscription.cancel();
  }


}

class NotificationExceptionV2 implements Exception {
  String _cause;

  NotificationExceptionV2(this._cause);

  @override
  String toString() {
    return _cause;
  }
}

class NotificationEventV2 {
  String packageMessage;
  String packageName;
  String packageExtra;
  String packageText;
  DateTime timeStamp;

  NotificationEventV2({this.packageName, this.packageMessage, this.timeStamp , this.packageExtra , this.packageText});

  factory NotificationEventV2.fromMap(Map<dynamic, dynamic> map) {
    DateTime time = DateTime.now();
    String name = map['packageName'];
    String message = map['packageMessage'];
    String text = map['packageText'];
    String extra =  map['packageExtra'];

    return NotificationEventV2(packageName: name, packageMessage: message, timeStamp: time,packageText: text , packageExtra: extra);
  }

  @override
  String toString() {
    return "Notification Event \n Package Name: $packageName \n - Timestamp: $timeStamp \n - Package Message: $packageMessage";
  }
}

NotificationEventV2 _notificationEvent(dynamic data) {
  return new NotificationEventV2.fromMap(data);
}

class AndroidNotificationListener {
  static const EventChannel _notificationEventChannel =
  EventChannel('events');

  Stream<NotificationEventV2> _notificationStream;

  Stream<NotificationEventV2> get notificationStream {
    if (Platform.isAndroid) {

      if (_notificationStream == null) {
        _notificationStream = _notificationEventChannel
            .receiveBroadcastStream()
            .map((event) => _notificationEvent(event));
      }
      return _notificationStream;
    }
    throw NotificationExceptionV2(
        'Notification API exclusively available on Android!');
  }
}
