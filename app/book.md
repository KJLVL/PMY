#### □ CALL_SТАТЕ_IDLE - устройство не используется для телефонного звонка (не принимается входящий звонок и не устанавливается исходящий);<br>
#### □ CALL_SТАТЕ_RINGING - устройство принимает входящий звонок;<br>
#### □ CALL_STATE_OFFHOOK - пользователь говорит по телефону.<br>
#### Сейчас мы напишем программу, которая реагирует на все три состояния звонка
и ...ничего не делает (действия вы сможете определить сами). Такое решение
я принял, чтобы не захламлять код. А что делать, решите вы сами - можно,
например, при получении звонка вывести соответствующее уведомление. Чтобы
вы могли определить собственные действия при изменении состояния звонка, мы
переопределим метод oncallStateChanged() (листинг 11.7).<br>
### Листинг 11.7. Реакция на входящий звонок<br>
...<br>
import android.telephony.PhoneStateListener;<br>
import android.telephony.TelephonyМanager;<br>
public class HardwareTelephony extends Activity<br>
TextView info; // Сюда можно выводить информацию о звонке<br>
TelephonyМanager tm;<br>
<br>
@Override<br>
public void onCreate(Btmdle savedinstanceState)<br>
super.onCreate(savedinstanceState);<br>
setContentView(R.layout.main);<br>
<br>
log =(TextView) findViewByid(R.id.log);<br>
// Создаем объект класса TelephonyМanager<br>
tm = (TelephonyМanager)getSystemService(TELEPHONY_SERVICE);<br>
// Устанавливаем прослушку для LISTEN_CALL_STATE<br>
tm.listen(new TelListener(),PhoneStateListener.LISTEN_CALL_STATE);<br>
}<br>
private class TelListener extends PhoneStateListener {<br>
public void onCallStateChanged(int state, String incomingNumber);<br>
super.onCallStateChanged(state, incomingNumber);<br>
switch (state) {<br>
case TelephonyManager.CALL_STATE_IDLE:<br>
log.setText("IDLE");<br>
break;<br>
case TelephonyМanager.CALL_STATE OFFHOOK:<br>
log.SetText("OFFНOOK, Входящий звонок:" +incomingNurnЬer);<br>
break;<br>
case TelephonyМanager.CALL_STAТE_RINGING:<br>
log.SetText("RINGING, Входящий звонок:" +incomingNumber);<br>
break;<br>
default:<br>
break;<br>
} // switch<br>
} // onCallStateChanged <br>
}<br>
}<br>
<br>
#### Наше приложение выводит в текстовую область (тextView) с именем log состояние
телефона и номер входящего звонка, если таковой имеется. Чтобы приложение
работало корректно, в файл манифеста нужно добавить строку:<br>
<uses-peпnission android:name="android.peпnission.READ_PHONE_STATE" /><br>
<br>
## 11.7. Получение информации о смартфоне <br><br>

#### С помощью класса TelephonyМanager можно получить информацию о смартфоне,
определить его состояние и набрать номер. Прежде чем приступить к написанию
кода, нужно добавить в файл манифеста следующую строку:<br>
<uses-peпnission android:name="android.peпnission.READ_PHONE_STATE" /><br>
#### Далее создайте в файле разметки текстовое представление с именем info - в него
мы будем выводить информацию о состоянии:<br>
<TextView<br>
android:id="@+id/info"<br>
android:layout_width="fill_parent"<br>
android:layout_height="wrap_content"<br>
android:text="@string/hello"<br>
/><br>
Подготовительный код:<br>
import android.telephony.TelephonyМanager;<br>
...<br>
TextView info; // Текстовая область TextView <br>
TelephonyManager trn; // Для информации об устройстве <br>
String EOL = "\n"; <br>
<br>
// Находим текстовую область в разметке<br>
info = (TextView) findViewByid{R.id.info);<br>
// Создаем объект trn для получения информации о телефоне<br>
tm = {TelephonyManager)getSystemService{TELEPHONY_SERVICE);<br>
// буфер строк<br>
StringBuilder buffer = new StringBuilder();<br>
<br>
Теперь выводим в буфер строк всю информацию о телефоне:<br>
// Общая информация об устройстве<br>
buffer.append{"General info:\n\n");<br>
buffer.append("Device ID :")<br>
.append(tm.getDeviceid()) .append(EOL);<br>
buffer.append("Software version: ")<br>
.append(tm.getDeviceSoftwareVersion()) .append(EOL);<br>
buffer. append ( "Number: ")<br>
.append(tm.getLinelNurnЬer()) .append(EOL);<br>
<br>
// Информация об операторе, выдавшем SIМ-карту<br>
buffer. append ( "\nOperator: \n\n");<br>
buffer.append("Country Code (ISO): ")<br>
.append(tm.getSimCountryiso()) .append(EOL);<br>
buffer.append("Provider: ")<br>
.append(tm.getSimOperator()) .append(EOL);<br>
buffer.append("IМEI: ")<br>
.append(tm.getimei()) .append(EOL);<br>
buffer.append("Name: ")<br>
.append(tm.getSimOperatorName()) .append(EOL);<br>
buffer.append("Serial number of SIM: ")<br>
.append(tm.getSimSerialNurnЬer()) .append(EOL);<br>
<br>
// Информация о текущей сети<br>
buffer.append("\nNrtwork:\n\n");<br>
buffer.append("Country Code (ISO): ")<br>
.append(tm.getNetworkCountryiso()) .append(EOL);<br>
buffer. append ( "Operator: ")<br>
.append(tm.getNetworkOperator()) .append(EOL);<br>
buffer. append ( "Name: ")<br>
.append(tm.getNetworkOperatorName()) .append(EOL);<br>
<br>
#### Теперь осталось поместить буфер строк в область info:<br>
info.setText(sb.toString());<br>
## 11.8. Ориентация экрана<br>
#### Любое Android-ycтpoйcтвo, оснащенное акселерометром, может определить, в каком положении оно сейчас находится. При этом в зависимости от показаний акселерометра может изменяться ориентация экрана: альбомная или портретная (книжная). Но не всегда\это хорошо. Одно дело, если вы разрабатываете офисное приложение, но совсем другое, когда разрабатывается игра. В этом случае изменение
ориентации экрана может оказаться не слишком желательным. Но вы можете принудительно задать ориентацию экрана для каждой деятельности. Для этого в файле
манифеста в элемент activity следует добавить параметр orientation:<br>
android:screenOrientation="portrait"<br>
android:screenOrientation="laпdscape"<br>
#### Значение portrait означает портретную (книжную) ориентацию, значение landscape - альбомную.<br>
#### Скрыть клавиатуру можно путем добавления следующего параметра:<br>
android:configChanges="orientation|keyboardНidden"<br>
#### Иногда нужно знать, когда скрыта клавиатура или когда изменена ориентация
экрана. Тогда вам следует переопределить метод onConfigurationChanged () (листинг 11.8).<br>
### Листинг 11.8. Переопределение метода nConfigurationChanged<br>
@Override<br>
public void onConfigurationChanged(Configuration newConfig) {<br>
super.onConfigurationChanged(newConfig);<br><br>
// Проверяем ориентацию экрана<br>
if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {<br>
Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();<br>
} else<br>
if (newConfig.orientation == Configuration.ORIENТATION_PORTRAIT) {<br>
Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();<br>
}<br>
// Проверяем видимость клавиатуры<br>
if (newConfig.hardКeyboardНidden = Configuration.НARDКEYВOARDHIDDEN_NO) {<br>
Toast.makeText(this, "keyboard visible",<br>
Toast.LENGТH_SHORT).show();<br>
} else<br>
if (newConfig.hardКeyboardНidden ==<br>
Configuration.НARDКEYВOARDHIDDEN_YES) {<br>
Toast.makeText(this, "keyboard hidden",<br>
Toast.LENGTH_SHORT) .show();<br>
}<br>
}<br>
#### На этом тема о датчиках исчерпана, и в следующей главе мы поговорим об отправке SMS и выходе в Интернет. <br>

# ГЛАВА 12<br><br>
# Соединение с внешним миром<br><br><br>
## 12.1. Отправка SMS<br>
#### Для отправки SMS используется класс SrnsManager, находящийся в пакете android.telephony.<br>
#### Отправить SMS совсем несложно. Прежде всего нужно добавить в файл манифеста
соответствующее разрешение:<br>
<uses-permission android:name="android.permission.SEND_SMS"><br>
#### Затем надо определить объект класса SrnsManager - для этого используется статический метод getDefault(). Далее следует определить получателя сообщения (его
номер телефона) и текст самого сообщения:<br>
import android.telephony.SrnsManager;<br>
import android.telephony.SmsMessage;<br>
...<br>
SrnsManager sendSMS = SmsManager.getDefault();<br>
String nurn = "номер получателя";<br>
String msg = "Му first SMS";<br>
sendSMS.sendTextMessage(nurn, null, msg, null, null);<br>
#### Отправляет SMS метод sendTextMessage(). Обычно достаточно указать первый и
третий его параметры. Первый параметр задает номер телефона получателя, третий - текст сообщения. Второй параметр позволяет указать номер SМS-центра.
Если указано значение null, будет использован номер SМS-центра, заданный в настройках устройства. Четвертый и пятый параметры служат для отслеживания факта отправки и доставки сообщения соответственно.<br>
#### Разберемся, как использовать четвертый и пятый параметры:<br>
// Определяем флаги отправки и доставки SMS<br>
String SENТ_SMS_FLAG = "SENТ_SMS";<br>
String DELIVER SMS FLAG = "DELIVER SMS"; <br>
// Создаем соответствующие действия<br>
// Действие, связанное с отправкой SMS<br>
Intent sent_sms = new Intent(SENT_SMS_FLAG);<br>
// отложенная деятельность, связанная с sent_sms<br>
Pendingintent spin ~ Pendingintent.getBroadcast(this,0,sent_sms,0);<br>
// Аналогично для доставки:<br>
Intent deliver sms new Intent(DELIVER_SMS FLAG);<br>
Pendingintent dpin = Pendingintent.getBroadcast(this,0,deliver_sms,0);<br><br>

#### Теперь создаем объект BroadcastReceiver, необходимый для получения результата.
Такой объект нужно зарегистрировать для каждого отложенного действия: <br>
// Получаем отчет об отправке<br>
BroadcastReceiver sentReceiver = new BroadcastReceiver() {<br>
@Override puЬlic void onReceive(Context с, Intent in) {<br>
switch(getResultCode()) {<br>
case Activity.RESULT_OK:<br>
// SMS отправлено, вьmолняем какие-то действия<br>
break;<br>
default:<br>
// Сбой<br>
break;<br>
}<br>
} };<br>
// Получаем отчет о доставке<br>
BroadcastReceiver deliverReceiver = new BroadcastReceiver(){<br>
@Override public void onReceive(Context с, Intent in) {<br>
switch(getResultCode()) {<br>
case Activity.RESULT_OK:<br>
// SMS доставлено, вьmолняем какие-то действия<br>
break;<br>
default:<br>
// Сбой<br>
break;<br>
// Регистрируем BroadcastReceiver <br><br>
registerReceiver(sentReceiver, new IntentFilter(SENT_SMS_FLAG));<br>
registerReceiver(deliverReceiver, new IntentFilter(DELIVER_SMS_FLAG));<br><br>
#### В большинстве случаев SMS не должно превышать 140 байтов. Для отправки более
длинных сообщений служит метод divideMessage (), разбивающий сообщения на
фраrменты, равные максимальному размеру SМS-сообщения. Отправка такого
сообщения осуществляется методом sendМul tipartTextMessage (), который используется вместо метода sendTextMessage () . Для получения отчета о доставке (или
отправке) нужно задействовать уже не одно отложенное событие, а массив таких
событий . Количество элементов в таком массиве будет равно количеству частей,
на которые было разбито исходное сообщение: <br>
ArrayList<String> multiSMS = sendSMS.divideMessage(msg);<br>
ArrayList<Pendingintent> sent_sms = new ArrayList<Pendingintent>();<br>
ArrayList<Pendingintent> deliver_sms = new ArrayList<Pendingintent>();<br>
for(int i=0; i< multiSMS.size(}; i++} {<br>
sentins.add(sentin);<br>
deliverins.add(deliverin);<br>
sendSMS.sendМultipartTextMessage(num, null,<br>
multiSMS, sentins, deliverins); <br>
Ранее бьmо сказано, что максимальная длина SMS составляет 140 байтов. Обратите
внимание: именно байтов, а не символов. Когда вы отправляете SMS латинскими
символами, то 140 байтов и означает 140 символов. Когда же вы используете
в SМS-сообщении символы национальных алфавитов - например, кириллицу,
то максимальная длина сокращается до 70 символов. Для кодирования символов
национальных алфавитов используется кодировка UCS-2, где каждый символ представлен двумя байтами (16-ю битами), поэтому количество символов, которые
можно отправить в одной SMS, сокращается до 70. Помните об этом! <br>

## 12.2. Работа с браузером <br>

#### Запустить браузер для отображения заданной страницы Интернета можно с помощью действия ACTION _NEW - сделать это достаточно просто: <br>

Intent browser = Intent(Intent.ACTION_VIEW};<br>
browser.setData(Uri.parse("https: //www.dkws.org.ua")};<br>
startActivity (browser); <br>
#### Но запуск браузера нам мало интересен. Подумайте сами: напишете вы приложение, запускающее браузер. И какой толк от него будет? Пользователю проще
напрямую запустить браузер.<br>
#### Было бы гораздо интереснее создать свой браузер на основе класса WebView. Класс
WebView использует для отображения веб-страниц движок WebKit (открытый движок браузера) - на этом же движке построен браузер Apple Safari и некоторые
друтие.<br>
#### В файл манифеста для использования Интернета нужно добавить разрешение: <br>
<uses-permission android:name="android.permission.INТERNET" /><br>
#### К приложению надо подключить два пакета:<br>
import android.webkit.WebView;<br>
import android.webkit.WebSettings; <br>