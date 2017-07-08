/*LIBRARIES*/
#include <DHT.h>
#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include <LiquidCrystal.h>
/*CONSTANTS*/
#define SENSOR 1
#define CLOUD 2
#define NO_CONNECTION -1
#define LINE_ENDING "\r\n"
const String SERVER="192.168.0.3";
const String PATH="/chobi";
/*TEMPERATURE SENSOR*/
#define DHTTYPE DHT11
#define TEMP_PIN A1
DHT dht(TEMP_PIN, DHTTYPE);
/*LEDS*/
#define OUT_RED_PIN 11
#define OUT_BLUE_PIN 10
/*WIFI MODULE*/
#define TX 13
#define RX 12
SoftwareSerial SSoft(TX, RX);
/*SOUND*/
#define MIC_PIN A0
/*DISPLAY*/
LiquidCrystal lcd(8, 7, 5, 4, 3, 2);
#define LCD_POTE_PIN 6
#define LCD_POTE_NUM 127
#define LCD_BACKGRD_PIN 9
boolean LCD_BKG = true;
boolean SHOW = true;

struct Temperature {
  char location[25];
  float temperature;
  int humidity;
};

struct Arduino {
  char ip[25];
  bool light = 1;
  int option;
  Temperature cloud;
  Temperature sensor;
};

Arduino arduino;

void setup() {
  //INICIALIZACION SENSOR TEMP Y LCD
  dht.begin();
  lcd.begin(16, 2);
  lcd.setCursor(0, 0);
  lcd.display();
  //INICIALIZACION SERIAL Y MODULO WIFI
  Serial.begin(9600);
  SSoft.begin(115200);
  sendCommand("AT+CIOBAUD=9600\r\n", 30);
  SSoft.begin(9600);
  sendCommand("AT+CWMODE=3\r\n",1000);
  sendCommand("AT+CIPMUX=1\r\n",1000);
  sendCommand("AT+CIPSERVER=1,80\r\n",1000);
  sendCommand("AT+CWJAP=\"#real\",\"Af84005719\"\r\n",10000);
  Serial.println("WIFI MODULE STARTED");
  readSensorTemp();
  sendIP();
  
  pinMode(OUT_RED_PIN, OUTPUT);
  pinMode(OUT_BLUE_PIN, OUTPUT);
  pinMode(MIC_PIN, INPUT);
  pinMode(LCD_BACKGRD_PIN, OUTPUT);

  analogWrite(LCD_POTE_PIN, LCD_POTE_NUM);
  digitalWrite(LCD_BACKGRD_PIN, LCD_BKG);
}

void loop() {
  Temperature current;
  int connectionID = readRequests();
  String msg;
  if(connectionID != NO_CONNECTION) {
    readSensorTemp();
    sendResponse(connectionID);
    SHOW = !SHOW;
  }
  
  switch(arduino.option) {
      case CLOUD:
        current = arduino.cloud;
        msg = "Clima Internet";
        break;
      case SENSOR:
      default:
        current = arduino.sensor;
        msg = "Clima Sensor  ";
  }
  
  if(SHOW) {
    lcd.clear();
    delay(200);
    String message = "Temp:" + String((int)current.temperature) + (char)223 + "Hum:" + String(current.humidity) + "% ";
    delay(10);
    
    lcd.setCursor(0, 0);
    lcd.print(msg);
    lcd.setCursor(0, 1);
    lcd.print(message);
    delay(200);
    SHOW = !SHOW;
  }
  
  int redLight = calculateRedLight(current.temperature);
  int blueLight = 255 - redLight;
  if(arduino.light) {
    analogWrite(OUT_RED_PIN, redLight);
    analogWrite(OUT_BLUE_PIN, blueLight);
  } else {
    analogWrite(OUT_RED_PIN, 0);
    analogWrite(OUT_BLUE_PIN, 0);
  }
  
  int sensorValue = analogRead(MIC_PIN);
  if(sensorValue > 822) {
      LCD_BKG = !LCD_BKG;
      digitalWrite(LCD_BACKGRD_PIN, LCD_BKG);
  }
}

//LEER REQUESTS DEL SERVIDOR
int readRequests() {
  if(SSoft.available()) {
    String request = SSoft.readString();
    int requestIndex = request.indexOf("+IPD,");
    if(requestIndex > 0) {
      int optionIndex = request.indexOf("option=") + 7;
      int option = optionIndex > 0 ? request.charAt(optionIndex) - 48 : arduino.option;
      int connectionID = request.charAt(requestIndex + 5) - 48;
      
      request = request.substring(request.indexOf("{"));
      request.replace(": ", ":");
      request.replace("\t", "");
      request.replace("\n", "");
      DynamicJsonBuffer jsonBuffer;
      JsonObject& root = jsonBuffer.parseObject(request);

      if(root.success()) {
        strcpy(arduino.ip, root["ip"]);
        arduino.light = root["light"].as<bool>();
        arduino.option = option;
        strcpy(arduino.cloud.location, root["cloud"]["location"]);
        arduino.cloud.temperature = root["cloud"]["temperature"].as<float>();
        arduino.cloud.humidity = root["cloud"]["humidity"].as<int>();
      }

      return connectionID;
    }
  }
  return NO_CONNECTION;
}

//ENVIAR RESPONSE AL SERVIDOR
void sendResponse(int connectionId) {
  String data = createResponseJSON();

  String response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + data;
  postData(response, connectionId, false);
}

//ENVIAR IP AL SERVIDOR
void sendIP() {
  String data = createResponseJSON();
   
  String postRequest = "POST " + PATH + " HTTP/1.1\r\nHost: " + SERVER + "\r\n" + 
                       "Accept: */*\r\nContent-Length: " + data.length() + "\r\n" +
                       "Content-Type: application/json\r\n\r\n" + data;
  
  postData(postRequest, 0L, true);
}

//CREAR JSON
String createResponseJSON() {
  char data[256];
  StaticJsonBuffer<256> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  JsonObject& sensor = root.createNestedObject("condition");

  root["light"] = arduino.light ? true : false;
  sensor["location"] = arduino.sensor.location;
  sensor["temperature"] = arduino.sensor.temperature;
  sensor["humidity"] = arduino.sensor.humidity;
  root.printTo(data, sizeof(data));

  return String(data);
}

//POST DATOS AL SERVIDOR
void postData(String postRequest, const long connectionId, boolean start) {
    //Establecer conexion con el servidor si es la primera vez.
    if(start) {
      String command = "AT+CIPSTART=";command.concat(String(connectionId));command.concat(",\"TCP\",\"");
      command.concat(SERVER);command.concat("\",8080");command.concat(LINE_ENDING);
      sendCommand(command, 500);
    }

    //Se realiza el post al servidor
    String command = "AT+CIPSEND=";command.concat(connectionId);command.concat(',');command.concat(postRequest.length());command.concat(LINE_ENDING);
    SSoft.print(command);

    if(SSoft.find(">")) { 
      SSoft.print(postRequest);
      if(SSoft.find("SEND OK")) { 
        closeConection(connectionId);
      }
    }
}

//CERRAR CONEXION
void closeConection(int connectionId) {
  String command = "AT+CIPCLOSE=";command.concat(String(connectionId));command.concat(LINE_ENDING);
  sendCommand(command, 50);
}

//ENVIAR COMANDOS AL MODULO ESP8266
String sendCommand(String command, const int timeout) {
    String buffer = "";    
    SSoft.print(command);
    long int time = millis();
    while((time + timeout) > millis())
      while(SSoft.available())
        char c = SSoft.read();
   
    return buffer;
}

float calculateRedLight(const float temperature) {
  int redLight = (255 * temperature) / 20 - 3060 / 20;
  if(redLight > 255)
    redLight = 255;
  if(redLight < 0)
    redLight = 0;
  return redLight;
}

void readSensorTemp() {
  arduino.sensor.humidity = dht.readHumidity(); //Se lee la humedad
  arduino.sensor.temperature = dht.readTemperature(); //Se lee la temperatura
}
