# Tsinova BLE SDK 		
 		
 [![JitPack](https://jitpack.io/v/TsinovaTech/TsinovaBluetooth.svg)](https://jitpack.io/#TsinovaTech/TsinovaBluetooth)		
 [![Bintray](https://api.bintray.com/packages/warry19940712/maven/TsinovaBluetooth/images/download.svg)](https://bintray.com/warry19940712/maven/TsinovaBluetooth)		
 [![License](https://img.shields.io/github/license/TsinovaTech/tsinovablesdk.svg)](https://github.com/TsinovaTech/tsinovablesdk/blob/master/LICENSE)		
 		
 		
 ## Gradle集成		
 ## Gradle integration 		
 		
  ``` 		
  compile('com.tsinova:TsinovaBluetooth:1.0.3') {		
         transitive = true		
         exclude group: 'com.google.code.gson', module: 'gson'		
         exclude group: 'org.greenrobot', module: 'eventbus'		
         exclude group: 'com.getkeepsafe.relinker', module: 'relinker'		
  } 		
   ```		
   		
 ## 快速开始		
 ## Rapid Starting 		
 		
 1.请在清单文件中声明以下service (Please state the following service in the list of files)		
 		
  ``` 		
 <service		
             android:name="com.tsinova.bluetoothandroid.bluetooth.BluetoothLeService"		
             android:enabled="true" />		
 ``` 		
 		
 2.设置 (Configure) application context、bikeBluetoothNumber、pageName		
 		
 ```		
 SingletonBTInfo.INSTANCE.setApplicationContext(applicationContext);		
 SingletonBTInfo.INSTANCE.setBikeBluetoothNumber(bikeBluetoothNumber);		
 SingletonBTInfo.INSTANCE.setPageName("pageName");		
 		
 //如果你的电单车蓝牙传输数据不需要加密，请调用以下方法，设置为false		
 //If the Bluetooth transmission of your ebike isn’t necessarily encrypted, please utilize the following method, set to false		
 SingletonBTInfo.INSTANCE.setEncryption(false);		
 ```		
 3.连接电单车代码请看BluetoothManager.class (Please refer to BluetoothManager.class for codes to connect to the ebike)		
 ```		
 mManager.scanLeDevice()		
 mManager.setScanCallBack()		
 mManager.connect()
