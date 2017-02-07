//
// Created by daran on 1/12/2017.
//

#include <jni.h>
#include "ece420_main.h"
#include "ece420_lib.h"
#include "kiss_fft/kiss_fft.h"
#define SCALE 15
#define PADDED FRAME_SIZE*2

float fftOut[FRAME_SIZE] = {};
bool isWritingFft = false;

extern "C" {
JNIEXPORT void JNICALL
Java_com_ece420_lab3_MainActivity_getFftBuffer(JNIEnv *env, jclass, jobject bufferPtr);
}

float windowed_frame[PADDED] = {};
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

    kiss_fft_cfg cfg = kiss_fft_alloc(PADDED,0,NULL,NULL);
    kiss_fft_scalar zero;
    memset(&zero,0,sizeof(zero));

    //window operation and zero padding
    for(int i = 0;i < PADDED;i++){
        if(i<FRAME_SIZE){windowed_frame[i] = (float) (0.54 - 0.46*cos(2.0 * M_PI * i / (FRAME_SIZE - 1))) * dataFloat[i];}
        else{windowed_frame[i] = 0;}//padded result
    }

    //************************performing fft*****************************
    kiss_fft_cpx cx_in[PADDED] = {};
    kiss_fft_cpx cx_out[PADDED] = {};
    for(int k = 0;k<PADDED;k++){
        //put kth sample in cx_in[k].r and cx_in[k].i
        cx_in[k].r = windowed_frame[k];
        cx_in[k].i = zero;
        cx_out[k].r = zero;
        cx_out[k].i = zero;
    }

    kiss_fft(cfg,cx_in,cx_out);//transform

    for(int k = 0;k<FRAME_SIZE;k++){
        fftOut[k] = log10((pow(cx_out[k].r,2) + pow(cx_out[k].i,2)))/SCALE;
    }

    free(cfg);

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

    for (int i = 0; i < FRAME_SIZE; i++) {
        buffer[i] = fftOut[i];
    }
}
