#include <SoftwareSerial.h>
#include <HX711.h>

HX711 hx711;
float calibration_factor = -25800; // 초기 보정 값 설정
#define LOADCELL_DOUT_PIN 2
#define LOADCELL_SCK_PIN 3
SoftwareSerial BTSerial(4, 5); // RX, TX 핀 설정

void setup() {
    Serial.begin(115200);
    hx711.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);
    hx711.set_scale();
    hx711.tare();

    BTSerial.begin(9600);
}

void loop() {
    if (Serial.available() > 0) {
        String str = Serial.readStringUntil('\n');
        float new_calibration_factor = str.toFloat();

        if (new_calibration_factor != 0) { // 유효한 값이 입력된 경우에만 보정 값 변경
            calibration_factor = new_calibration_factor;
        }
    }
    
    hx711.set_scale(calibration_factor);
    float weight = hx711.get_units();

    if (!isnan(weight) && !isinf(weight)) { // 유효한 값을 전송
        String weightStr = String(weight);
        Serial.print("Weight : ");
        Serial.print(weight);
        Serial.print(" Kg\tCalibration: ");
        Serial.println(calibration_factor);
        
        BTSerial.println(weightStr);
    } else {
        Serial.println("Invalid weight reading.");
        BTSerial.println("Error");
    }

    delay(1000);
}
