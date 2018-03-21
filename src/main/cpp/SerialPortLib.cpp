#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPortLib.h"

#include "android/log.h"
#include "termios.h"


static const char *TAG="SerialPort";

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_hc_serialport_SerialPortLib_open(JNIEnv *env, jclass type, jstring path_, jint baudrate,
                                           jint flags) {
        int fd;
        speed_t speed;
        jobject mFileDescriptor;

    //    选择波特率
        {
            speed=getBaudrate(baudrate);
            if (speed==-1){
                return NULL;
            }
        }

    //打开串口
        {
            jboolean iscopy;
            const char *path_utf=env->GetStringUTFChars(path_,&iscopy);
            fd=open(path_utf,O_RDWR|flags);
            env->ReleaseStringUTFChars(path_,path_utf);
            if (fd==-1){
                return NULL;
            }
        }

        //配置串口
        {
            struct termios cfg;
            //		LOGD("Configuring serial port");
            if (tcgetattr(fd,&cfg))
            {
                close(fd);

                return NULL;
            }

            cfmakeraw(&cfg);
            cfsetispeed(&cfg,speed);
            cfsetospeed(&cfg,speed);

            if (tcsetattr(fd,TCSANOW,&cfg)){
                //			LOGE("tcsetattr() failed");
                close(fd);
                /* TODO: throw an exception */
                return NULL;
            }

            {
                jclass cFileDescripter=env->FindClass("java/io/FileDescriptor");
                jmethodID  iFileDescripter=env->GetMethodID(cFileDescripter,"<init>","()V");
                jfieldID descripterID=env->GetFieldID(cFileDescripter,"descriptor", "I");
                mFileDescriptor=env->NewObject(cFileDescripter,iFileDescripter);
                env->SetIntField(mFileDescriptor,descripterID,(jint)fd);
            }
            return mFileDescriptor;
        }

        // TODO

}

JNIEXPORT void JNICALL
Java_com_hc_serialport_SerialPortLib_close(JNIEnv *env, jobject instance) {

    // TODO
        jclass SerialPortClass=env->GetObjectClass(instance);
        jclass FileDescriptorClass=env->FindClass("java/io/FileDescriptor");

        jfieldID mFdID=env->GetFieldID(SerialPortClass,"mFileDes", "Ljava/io/FileDescriptor;");
        jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

        jobject mFd=env->GetObjectField(instance,mFdID);
        jint descriptor=env->GetIntField(mFd,descriptorID);
        close(descriptor);

}