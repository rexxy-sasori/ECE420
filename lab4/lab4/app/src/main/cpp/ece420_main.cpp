//
// Created by daran on 1/12/2017.
//

#include "ece420_main.h"
#include "ece420_lib.h"
#include "kiss_fft/kiss_fft.h"
#include "audio_common.h"

#define VOICED_THRESHOLD 50000000 // Write your own!

// TODO: better initialization, this is not great
float lastFreqDetected = -1;

extern "C" {
JNIEXPORT float JNICALL
        Java_com_ece420_lab4_MainActivity_getFreqUpdate(JNIEnv *env, jclass);
}

void ece420ProcessFrame(sample_buf *dataBuf) {
    // Keep in mind, we only have 20ms to process each buffer!
    struct timeval start;
    struct timeval end;
    gettimeofday(&start, NULL);

    // Data is encoded in signed PCM-16, little-endian, mono
    float bufferIn[FRAME_SIZE];
    for (int i = 0; i < FRAME_SIZE; i++) {
        int16_t val = (int16_t) (((uint16_t) dataBuf->buf_[2 * i]) | (((uint16_t) dataBuf->buf_[2 * i + 1]) << 8));
        bufferIn[i] = (float) val;
    }

    // In this section, you will be computing the autocorrelation of bufferIn
    // and picking the delay corresponding to the best match. Naively computing the
    // autocorrelation in the time domain is an O(N^2) operation and will not fit
    // in your timing window.
    //
    // First, you will have to detect whether or not a signal is voiced.
    // We will implement a simple voiced/unvoiced detector by thresholding
    // the power of the signal.
    //
    // Next, you will have to compute autocorrelation in its O(N logN) form.
    // Autocorrelation using the frequency domain is given as:
    //
    //  autoc = ifft(fft(x) * conj(fft(x)))
    //
    // where the fft multiplication is element-wise.
    //
    // You will then have to find the index corresponding to the maximum
    // of the autocorrelation. Consider that the signal is a maximum at idx = 0,
    // where there is zero delay and the signal matches perfectly.
    //
    // Finally, write the variable "lastFreqDetected" on completion. If voiced,
    // write your determined frequency. If unvoiced, write -1.
    // ********************** START YOUR CODE HERE ************************ //

    float Es = 0;
    for(int i = 0; i < FRAME_SIZE; i++)
    {
        Es += abs(bufferIn[i]*bufferIn[i]);
    }

    if(Es > VOICED_THRESHOLD)
    {
        kiss_fft_cfg cfg = kiss_fft_alloc(FRAME_SIZE,0,NULL,NULL);
        kiss_fft_scalar zero;
        memset(&zero,0,sizeof(zero));

        kiss_fft_cpx cx_in[FRAME_SIZE] = {};
        kiss_fft_cpx cx_out[FRAME_SIZE] = {};
        for(int k = 0;k<FRAME_SIZE;k++)
        {
            //put kth sample in cx_in[k].r and cx_in[k].i
            cx_in[k].r = bufferIn[k];
        }

        kiss_fft(cfg,cx_in,cx_out);

        for(int k = 0;k<FRAME_SIZE;k++)
        {
            cx_out[k].r = pow(cx_out[k].r,2) + pow(cx_out[k].i,2);
            cx_out[k].i = 0;
        }

        kiss_fft_cfg cfgi = kiss_fft_alloc(FRAME_SIZE,1,NULL,NULL);
        kiss_fft(cfgi,cx_out,cx_in);

        //a = cx_in;
        for(int k = 0;k<FRAME_SIZE;k++)
        {
            //put kth sample in cx_in[k].r and cx_in[k].i
            bufferIn[k] = cx_in[k].r;
        }
        int L = findMaxArrayIdx(bufferIn,50,FRAME_SIZE/2);

        float Fs = 48000;

        lastFreqDetected = Fs/L;


        free(cfg);
        free(cfgi);

    }
    else
        lastFreqDetected = -1;

    // ************************ END YOUR CODE HERE ************************ //

    gettimeofday(&end, NULL);
    LOGD("Time delay: %ld us",  ((end.tv_sec * 1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec)));
}

JNIEXPORT float JNICALL
Java_com_ece420_lab4_MainActivity_getFreqUpdate(JNIEnv *env, jclass) {
    return lastFreqDetected;
}




