#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLE2902.h>
#include <Wire.h>
#include "SparkFun_OPT4048.h"
#include <FastLED.h>

// ===== Hardware Configuration =====
#define SDA_PIN 4 //Blue Wires
#define SCL_PIN 5 //Yellow Wires
#define OPT4048_ADDR 0x44
// #define SDA_PIN 40 //blue wire
// #define SCL_PIN 41 //yellow wire


// ===== Delay Constants =====
#define DELAY_ONE 1000
#define DELAYVAL   5000  // 5 seconds on

// ===== NeoPixel Configuration =====
#define LED_PIN     14
// #define LED_PIN     2
#define NUM_LEDS    7
#define BRIGHTNESS  100
CRGB leds[NUM_LEDS];


// ===== Sensor & BLE Objects =====
SparkFun_OPT4048 colorSensor;
BLECharacteristic *pCharacteristic;
BLEServer* pServer;



// ===== BLE Configurations =====
#define DEVICE_NAME "ESP32-team6"
#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"

// ===== Configuration Overrides =====
#define CONFIG_BT_NIMBLE_TASK_STACK_SIZE 8192  // Default is 4096
#define CONFIG_BTDM_CONTROLLER_TASK_STACK_SIZE 8192

// ===== Global Variables =====
bool sensorConnected = false;
bool continueSendingData = true;
bool receivedMessage = false;
String receivedData = "";
byte busStatus;
static constexpr uint8_t kOPTMatrixRows = 4;
static constexpr uint8_t kOPTMatrixCols = 4;

// ===== Matrix & Data Structures =====
const double cieMatrix[3][4] = {
    {0.000234892992, -0.0000189652390, 0.0000120811684, 0},
    {0.0000407467441, 0.000198958202, -0.0000158848115, 0.00215},
    {0.0000928619404, -0.0000169739553, 0.000674021520, 0}
    };


struct CIEColorData {
    float x;
    float y;
    float Y;
};
struct MeasurementSet {
  CIEColorData readings[3]; // Stores all 3 iterations
};


struct LightingCondition {
  uint8_t r, g, b;
} lightingConditions[3] = {
    {64, 156, 255},  // Daylight
    {255, 255, 251},  // Fluorescent
    {255, 147, 41}    // Incandescent
};

// ===== NeoPixel Functions =====
void setupNeoPixel() {
  FastLED.addLeds<WS2812B, LED_PIN, GRB>(leds, NUM_LEDS)
    .setCorrection(TypicalLEDStrip)
    .setDither(BRIGHTNESS < 255);
  
  FastLED.setBrightness(BRIGHTNESS);
  FastLED.clear(true);
  Serial.println("NeoPixel Initialized (RMT)");
}

void showColor(int led, int r, int g, int b) {
  FastLED.clear();
  leds[led] = CRGB(r, g, b);
  FastLED.show();
  delay(DELAYVAL);
  FastLED.clear();
  FastLED.show();
  delay(1000);
}

// ===== Sensor Functions =====
MeasurementSet computeLightingMeasurement() {
  MeasurementSet set;
  for (int i = 0; i < 3; i++) {
    leds[0] = CRGB(lightingConditions[i].r, lightingConditions[i].g, lightingConditions[i].b);
    FastLED.show();
    delay(500); // Let light stabilize
    set.readings[i] = getData();

    //Cleanup
    FastLED.clear();
    FastLED.show();
    delay(200);
    if (i < 2) delay(2000);
  }

  return set;
}
CIEColorData computeAverage(CIEColorData* measurements) {
  CIEColorData avg = {0, 0, 0};
  for (int i = 0; i < 3; i++) {
    avg.x += measurements[i].x;
    avg.y += measurements[i].y;
    avg.Y += measurements[i].Y;
  }
  avg.x /= 3.0;
  avg.y /= 3.0;
  avg.Y /= 3.0;

  return avg;
}

CIEColorData  getData() {
    colorSensor.setOperationMode(OPERATION_MODE_ONE_SHOT);
    while(!colorSensor.getConvReadyFlag()) { 
        delay(10);
    }

    sfe_color_t raw;
    colorSensor.getAllChannelData(&raw); // Get raw ADC values for all channels

    // Compute XYZ using the calibration matrix
    float X = raw.red * cieMatrix[0][0] + raw.green * cieMatrix[1][0] + raw.blue * cieMatrix[2][0];
    float Y = raw.red * cieMatrix[0][1] + raw.green * cieMatrix[1][1] + raw.blue * cieMatrix[2][1];
    float Z = raw.red * cieMatrix[0][2] + raw.green * cieMatrix[1][2] + raw.blue * cieMatrix[2][2];

    // Calculate CIEx and CIEy (normalized chromaticity coordinates)
    float sum = X + Y + Z;
    float CIEx = (sum == 0) ? 0 : X / sum; // Handle division by zero
    float CIEy = (sum == 0) ? 0 : Y / sum;
    // float CIEz = (sum == 0) ? 0: Z / sum;
    return {CIEx, CIEy, Y};
}

bool initColorSensor() {
  delay(DELAY_ONE);
  Wire.begin(SDA_PIN, SCL_PIN);
  
  if (!colorSensor.begin(OPT4048_ADDR)) {
    Serial.println("OPT4048 not found!");
    return false;
  }

  // Sensor configuration
  colorSensor.setConversionTime(CONVERSION_TIME_200MS);
  colorSensor.setRange(RANGE_AUTO);
  colorSensor.setOperationMode(OPERATION_MODE_ONE_SHOT);
  
  Serial.println("OPT4048 initialized");
  return true;
}
// ===== Packet Sending Functions =====
void sendAverage(BLECharacteristic* pChar, const CIEColorData& avg) {
  uint8_t packet[13];
  packet[0] = 0x02;
  memcpy(&packet[1], &avg.x, sizeof(float));
  memcpy(&packet[5], &avg.y, sizeof(float));
  memcpy(&packet[9], &avg.Y, sizeof(float));
  pChar->setValue(packet, 13);
  pChar->notify();
}

void sendMeasurementSet(BLECharacteristic* pChar, const MeasurementSet& set) {
  uint8_t packet[1 + sizeof(set)];
  packet[0] = 0x03; // Struct header
  memcpy(&packet[1], &set, sizeof(set));
  pChar->setValue(packet, sizeof(packet));
  pChar->notify();
}

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      Serial.println("Device connected");
    }

    void onDisconnect(BLEServer* pServer) {
      Serial.println("Device disconnected");
      BLEDevice::startAdvertising();
    }
};

class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* pCharacteristic) {
      String rxValue = pCharacteristic->getValue();
      rxValue.trim();

      if (rxValue == "1") {
        // Trigger one-shot measurement
        colorSensor.setOperationMode(OPERATION_MODE_ONE_SHOT);
        
        // Wait for conversion (4 channels * conversion time)
        // Read sensor values
        CIEColorData cieData = getData();
        float x = cieData.x;
        float y = cieData.y;
        float Y = cieData.Y;
  
        Serial.println("Data being Sent");
        Serial.print("x: ");
        Serial.print(x);
        Serial.print(" y: ");
        Serial.print(y);
        Serial.print(" Y: ");
        Serial.println(Y);

        // Prepare data packet (header + 3 floats)
        uint8_t packet[13];
        packet[0] = 0x01; // header
        
        // Use memcpy with properly aligned floats
        float sensor_values[3] = {x, y, Y};
        memcpy(&packet[1], sensor_values, 12);

        // Send data
        pCharacteristic->setValue(packet, 13);
        pCharacteristic->notify();
        Serial.println("Sent sensor data");

        delay(1000);
        
      }
      if (rxValue == "0") {
        delay(100);

        // Collect all measurements
        MeasurementSet measurements = computeLightingMeasurement();
        
        // Calculate average
        CIEColorData avg = computeAverage(measurements.readings);
        Serial.println("Sent Average Values");
        // Send average first
        sendAverage(pCharacteristic, avg);
        
        // Send full dataset
        Serial.println("Sent all Measurements");
        sendMeasurementSet(pCharacteristic, measurements);
      }
    }
};

void setupBLE() {
  BLEDevice::init(DEVICE_NAME);
  
  // Create server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create service (MUST be called after server creation)
  BLEService *pService = pServer->createService(SERVICE_UUID);

  pCharacteristic = pService->createCharacteristic(
                    CHARACTERISTIC_UUID,
                    BLECharacteristic::PROPERTY_READ |
                    BLECharacteristic::PROPERTY_WRITE |
                    BLECharacteristic::PROPERTY_WRITE_NR |
                    BLECharacteristic::PROPERTY_NOTIFY 
                    // BLECharacteristic::PROPERTY_INDICATE
                );

  // Add descriptor (REQUIRED)
  pCharacteristic->addDescriptor(new BLE2902());
  // After creating the characteristic:
  BLE2902* desc = new BLE2902();
  desc->setNotifications(true);
  desc->setIndications(true);  // Required for write operations
  // pCharacteristic->addDescriptor(desc);
  pCharacteristic->setCallbacks(new MyCallbacks());
  BLESecurity *pSecurity = new BLESecurity();
  pSecurity->setAuthenticationMode(ESP_LE_AUTH_BOND);
  pSecurity->setCapability(ESP_IO_CAP_NONE);

  // Start service (CRITICAL STEP)
  pService->start();

  // Configure advertising
  BLEAdvertising *pAdvertising = pServer->getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  
  // Start advertising
  BLEDevice::startAdvertising();
  Serial.println("Advertising started!");
}

void setup() {
  Serial.begin(115200);
  delay(100);
  
  setupNeoPixel();
  initColorSensor();
  setupBLE();
  
  Serial.println("System Ready");
}
void loop(){}

