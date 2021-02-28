#include <SoftwareSerial.h>

SoftwareSerial bluetoothModule(10, 11); 

const int leftEngineForward=2;
const int leftEngineBack=3;
const int rightEngineForward=4;
const int rightEngineBack=5;

void setup()
{
  bluetoothModule.begin(9600);
  pinMode(led0,OUTPUT);
  pinMode(leftEngineForward, OUTPUT);
  pinMode(leftEngineBack, OUTPUT);
  pinMode(rightEngineForward, OUTPUT);
  pinMode(rightEngineBack, OUTPUT);
}

char character;
void loop()
{
  while(bluetoothModule.available()>0){
    character = bluetoothModule.read();
    switch(character){ 
      case '0': //GO FORWARD
        digitalWrite(leftEngineForward, HIGH);
        digitalWrite(rightEngineForward, HIGH);
        digitalWrite(leftEngineBack, LOW);
        digitalWrite(rightEngineBack, LOW);
        bluetoothModule.println("go forward");
        break;
      case '1': //GO BACK
        digitalWrite(leftEngineForward, LOW);
        digitalWrite(rightEngineForward, LOW);
        digitalWrite(leftEngineBack, HIGH);
        digitalWrite(rightEngineBack, HIGH);
        bluetoothModule.println("go back");
        break; 
      case '2': //TURN LEFT
        digitalWrite(leftEngineForward, LOW);
        digitalWrite(rightEngineForward, HIGH);
        digitalWrite(leftEngineBack, HIGH);
        digitalWrite(rightEngineBack, LOW);
        bluetoothModule.println("turn left");
        break;  
      case '3': //TURN RIGHT
        digitalWrite(leftEngineForward, HIGH);
        digitalWrite(rightEngineForward, LOW);
        digitalWrite(leftEngineBack, LOW);
        digitalWrite(rightEngineBack, HIGH);
        bluetoothModule.println("turn right");
        break;
      case '4': //STOP
        digitalWrite(leftEngineForward, LOw);
        digitalWrite(rightEngineForward, LOW);
        digitalWrite(leftEngineBack, LOW);
        digitalWrite(rightEngineBack, LOW);
        bluetoothModule.println("stop");
        break; 
    }
  }
}
