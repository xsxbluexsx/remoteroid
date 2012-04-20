#include "suinput.h"

bool loadInputDevice();
void unloadInputDevice();

void touchDown();
void touchUp();
void touchSetPtr(int, int);
void touchPtr(int, int);

int inputFd = -1;

bool loadInputDevice(){
	struct input_id id = {
			BUS_VIRTUAL,
			1,
			1,
			1
	};

	inputFd = suinput_open("qwerty", &id);
	if(inputFd==-1) // Load failed
		return false;
	else
		return true;
}

void unloadInputDevice(){
	if(inputFd!=-1)
		suinput_close(inputFd);
}

void touchDown(){
	suinput_write(inputFd, EV_KEY, BTN_TOUCH, 1);
	suinput_write(inputFd, EV_SYN, SYN_REPORT, 0);
}

void touchUp(){
	suinput_write(inputFd, EV_KEY, BTN_TOUCH, 0);
	suinput_write(inputFd, EV_SYN, SYN_REPORT, 0);
}

void touchSetPtr(int x, int y){
	suinput_write(inputFd, EV_ABS, ABS_X, x);
	suinput_write(inputFd, EV_ABS, ABS_Y, y);
	suinput_write(inputFd, EV_SYN, SYN_REPORT, 0);
}

void touchPtr(int x, int y){
	touchSetPtr(x, y);
	touchDown();
	touchUp();
}
