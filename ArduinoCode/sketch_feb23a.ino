#include <SoftwareSerial.h>

SoftwareSerial bluetoothModule(10, 11); 

const int led0=13;
const int leftEngine=2;
const int rightEngine=3;

void setup()
{
  bluetoothModule.begin(9600);
  pinMode(led0,OUTPUT);
  pinMode(leftEngine, OUTPUT);
  pinMode(rightEngine, OUTPUT);
}

char character;
void loop()
{
  while(bluetoothModule.available()>0){
    character = bluetoothModule.read();
    switch(character){ 
      case '0':
        digitalWrite(leftEngine, HIGH);
        digitalWrite(rightEngine, HIGH);
        bluetoothModule.println("go");
        break;
      case '1':
        digitalWrite(leftEngine, LOW);
        digitalWrite(rightEngine, LOW);
        bluetoothModule.println("stop");
        break; 
      case '2':
        digitalWrite(leftEngine, LOW);
        digitalWrite(rightEngine, HIGH);
        bluetoothModule.println("turn left");
        break;  
      case '3':
        digitalWrite(leftEngine, HIGH);
        digitalWrite(rightEngine, LOW);
        bluetoothModule.println("turn right");
        break; 
    }
  }
}
