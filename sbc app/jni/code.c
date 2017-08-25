#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <sys/poll.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "code.h"

#include "android/log.h"
static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


JNIEXPORT jint JNICALL Java_ua_taras_appc_MainActivity_configurePin26
(JNIEnv *env, jobject thiz)
{
    int fd;
    char c;
    int err;

    fd = open("/sys/class/gpio/gpio225/value", O_RDONLY);
    if (fd < 0) {
    	LOGD("C_open_val_F", "Failed to open gpio225!");
    	return -1;
    }

    read(fd, &c, sizeof(c));

    LOGD("C_open_val_S_%c", c);

    return fd;
}

JNIEXPORT jint JNICALL Java_ua_taras_appc_MainActivity_configurePin33
(JNIEnv *env, jobject thiz)
{
    int fd;
    char c;
    int err;

    fd = open("/sys/class/gpio/gpio234/value", O_RDONLY);
    if (fd < 0) {
    	LOGD("C_open_val_F", "Failed to open gpio234!");
    	return -1;
    }

    read(fd, &c, sizeof(c));

    LOGD("C_open_val_S_gpio234_%c", c);

    return fd;
}

int interruption_flag = 0;

JNIEXPORT void JNICALL Java_helperClasses_RfidListenerManager_set_1interruption_1status(JNIEnv *env, jobject thiz, jint status) {
	interruption_flag = status;
}

JNIEXPORT jobjectArray JNICALL Java_ua_taras_appc_MainActivity_gpioLineVal(
	JNIEnv *env, jobject thiz, jint iFd26, jint iFd33) {

struct pollfd pollFd[2];
char c;
int err;
int counter = 0;

//array size of 3 means [0], [1], [2]
jobjectArray idArray;
//creates String[3] = ""
idArray = (jobjectArray)(*env)->NewObjectArray(env, 29, (*env)->FindClass(env, "java/lang/String"), (*env)->NewStringUTF(env, ""));

do {

	pollFd[0].fd = iFd26;
	pollFd[0].events = POLLPRI | POLLERR;
	pollFd[0].revents = 0;

	pollFd[1].fd = iFd33;
	pollFd[1].events = POLLPRI | POLLERR;
	pollFd[1].revents = 0;

	LOGD("C_POLL_BLOCKED", "");

	err = poll(pollFd, 2, 6000);
		if (interruption_flag == 0) {
			if (err != 1) {
				if (counter != 26)
					LOGD("C_poll_error", "");
				else
					return idArray;
			}
		} else {
			// interruption occurred...reset flag
			interruption_flag = 0;
			return NULL;
		}

    LOGD("C_INTERRUPT_VAL_IS_%d", interruption_flag);
	LOGD("C_POLL_UNLOCKED", "");

	if (pollFd[0].revents & POLLPRI){

		lseek(iFd26, 0, SEEK_SET);

		err = read(iFd26, &c, sizeof(c));
		if(err != 1)
			LOGD("C_gpio26_error", "");

		(*env)->SetObjectArrayElement(env, idArray, counter, (*env)->NewStringUTF(env, "1"));
		counter++;
	}

	if (pollFd[1].revents & POLLPRI){

		lseek(iFd33, 0, SEEK_SET);

		err = read(iFd33, &c, sizeof(c));
		if(err != 1)
			LOGD("C_gpio33_error", "");


		(*env)->SetObjectArrayElement(env, idArray, counter, (*env)->NewStringUTF(env, "0"));
		counter++;
	}
	LOGD("C_COUNTER_%d", counter);

} while(counter < 28);




return idArray;

}





