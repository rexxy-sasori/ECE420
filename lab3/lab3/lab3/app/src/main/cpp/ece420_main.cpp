//
// Created by daran on 1/12/2017.
//

#include <jni.h>
#include "ece420_main.h"
#include "ece420_lib.h"
#include "kiss_fft/kiss_fft.h"

float fftOut[FFT_SIZE] = {};
bool isWritingFft = false;

extern "C" {
JNIEXPORT void JNICALL
Java_com_ece420_lab3_MainActivity_getFftBuffer(JNIEnv *env, jclass, jobject bufferPtr);
}

void ece420ProcessFrame(sample_buf *dataBuf) {
    isWritingFft = true;

    // Keep in mind, we only have a small amount of time to process each buffer!
    struct timeval start;
    struct timeval end;
    gettimeofday(&start, NULL);

    // Data is encoded in signed PCM-16, little-endian, mono channel
    int16_t data[FRAME_SIZE];
    for (int i = 0; i < FRAME_SIZE; i++) {
        data[i] = ((uint16_t) dataBuf->buf_[2 * i]) | (((uint16_t) dataBuf->buf_[2 * i + 1]) << 8);
    }

    // This could be changed in lower levels, but for now, we will keep our data input as int16_t
    float dataFloat[FRAME_SIZE];
    for (int i = 0; i < FRAME_SIZE; i++) {
        dataFloat[i] = (float) data[i];
    }


    // ********************* START YOUR CODE HERE ***********************
    // NOTE: This code block is a suggestion to get you started. You will have to
    // add/change code outside this block to implement FFT buffer overlapping.
    //
    // Keep all of your code changes within java/MainActivity and cpp/ece420_*




    // ********************* END YOUR CODE HERE *************************

    isWritingFft = false;

    gettimeofday(&end, NULL);
    LOGD("Time delay: %ld us",  ((end.tv_sec * 1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec)));
}


// http://stackoverflow.com/questions/34168791/ndk-work-with-floatbuffer-as-parameter
// Do not modify
JNIEXPORT void JNICALL
Java_com_ece420_lab3_MainActivity_getFftBuffer(JNIEnv *env, jclass, jobject bufferPtr) {

    jfloat *buffer = (jfloat *) env->GetDirectBufferAddress(bufferPtr);

    // thread-safe, kinda
    while (isWritingFft) {}

    for (int i = 0; i < FFT_SIZE; i++) {
        buffer[i] = fftOut[i];
    }
}
