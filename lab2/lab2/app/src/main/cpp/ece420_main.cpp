//
// Created by daran on 1/12/2017.
//

#include "ece420_main.h"
#define FRAME_SIZE 128

// TODO: Change this to match your filter
#define N_TAPS 10000

int16_t firFilter(int16_t sample);

void ece420ProcessFrame(sample_buf *dataBuf) {
    // Keep in mind, we only have a small amount of time to process each buffer!
    struct timeval start;
    struct timeval end;
    gettimeofday(&start, NULL);

    // Using {} initializes all values in the array to zero
    int16_t bufferIn[FRAME_SIZE] = {};
    int16_t bufferOut[FRAME_SIZE] = {};


    // ******************** START YOUR CODE HERE ********************

    // Your buffer conversion here

    // Loop code provided as a suggestion. This loop simulates sample-by-sample processing.
    for (int sampleIdx = 0; sampleIdx < dataBuf->size_; sampleIdx++) {
        int16_t sample = bufferIn[sampleIdx];

        // Your function implementation
        int16_t output = firFilter(sample);

        bufferOut[sampleIdx] = output;
    }

    // Your buffer conversion here

    // ********************* END YOUR CODE HERE *********************



    gettimeofday(&end, NULL);
    LOGD("Loop timer: %ld us",  ((end.tv_sec * 1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec)));

}


int16_t circBuf[N_TAPS] = {};
int16_t circBufIdx = 0;

int16_t firFilter(int16_t sample) {
    int16_t output = 0;

    // This function simulates sample-by-sample processing. Here you will
    // implement an FIR filter such as:
    //
    // y[n] = a x[n] + b x[n-1] + c x[n-2] + ...
    //
    // You will maintain a circular buffer to store your prior samples
    // x[n-1], x[n-2], ..., x[n-k]. Suggested initializations circBuf
    // and circBufIdx are given.
    //
    // Input 'sample' is the current sample x[n].
    // ******************** START YOUR CODE HERE ********************



    // ********************* END YOUR CODE HERE *********************

    return output;
}
