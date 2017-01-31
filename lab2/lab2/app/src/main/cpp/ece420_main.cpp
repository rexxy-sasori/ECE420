//
// Created by daran on 1/12/2017.
//

#include "ece420_main.h"
#include <iostream>
#include <queue>
#include <vector>
#define FRAME_SIZE 128
using namespace std;

// TODO: Change this to match your filter
#define N_TAPS 67

int16_t firFilter(int16_t sample);
int16_t elementMul();

//read fir csv data file



void ece420ProcessFrame(sample_buf *dataBuf) {
    // Keep in mind, we only have a small amount of time to process each buffer!
//    struct timeval start;
//    struct timeval end;
//    gettimeofday(&start, NULL);

    // Using {} initializes all values in the array to zero
    int16_t bufferIn[FRAME_SIZE] = {};
    int16_t bufferOut[FRAME_SIZE] = {};


    // ******************** START YOUR CODE HERE ********************

    // Your buffer conversion here

    //from PCM to int16_t

    for (int i = 0; i < dataBuf->size_; i+=2) {
        bufferIn[i/2]=(((int16_t)(dataBuf->buf_[i+1]))<<8)|((int16_t)((dataBuf->buf_[i])&0x00FF));
    }

    LOGD("Input: %d", bufferIn[0] );

    // Loop code provided as a suggestion. This loop simulates sample-by-sample processing.
    for (int sampleIdx = 0; sampleIdx < FRAME_SIZE; sampleIdx++) {
        int16_t sample = bufferIn[sampleIdx];
        // Your function implementation
        int16_t output = firFilter(sample);
        //int16_t output = sample;
        bufferOut[sampleIdx] = output;
    }

    LOGD("Output: %d",bufferOut[0]);

    // Your buffer conversion here
    // input:bufferOut
    // output:PCM 16 bit code
    for(int i = 0; i < dataBuf->size_;i+=2){
        //last 8 bits of the bufferOut
        dataBuf->buf_[i] = (uint8_t)(bufferOut[i/2]&);
        //first 8 bits of the bufferOut
        dataBuf->buf_[i+1] = (uint8_t)(bufferOut[i/2]>>8);
    }

    // ********************* END YOUR CODE HERE *********************
//    gettimeofday(&end, NULL);
//    LOGD("Loop timer: %ld us",  long((end.tv_sec * 1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec)));

}


vector<int16_t> circBuf(N_TAPS);
int16_t circBufIdx = 0;

int16_t elementMul(vector<double> & filter, vector<int16_t> & buffer){
//    LOGD("%f", filter[0]);
    int16_t result = 0;

    for (int i = 0; i < N_TAPS; i++){
        //@TO DO
        result += filter[i]*buffer[i];
    }

    return result;
}



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
    //

    //shift circBuf269
    //stick in sample
    //update circBufIdx
//    circBufIdx = 0;
    vector<double> coefs(N_TAPS);
    coefs[0]=-0.0155752887632; coefs[1]=-0.0170968842151;
    coefs[2]=-0.0179645741715; coefs[3]=-0.0180511257341;
    coefs[4]=-0.0172634363391; coefs[5]=-0.015550380848;
    coefs[6]=-0.0129086065417; coefs[7]=-0.00938584401296;
    coefs[8]=-0.00508143390833; coefs[9]=-0.000143923209485;
    coefs[10]=0.00523424838199; coefs[11]=0.0108247810173;
    coefs[12]=0.0163739114543; coefs[13]=0.0216146717685;
    coefs[14]=0.0262803858113; coefs[15]=0.0301186904011;
    coefs[16]=0.0329053185198; coefs[17]=0.0344568748437;
    coefs[18]=0.034641871008; coefs[19]=0.0333893677331;
    coefs[20]=0.0306946894724; coefs[21]=0.0266218283707;
    coefs[22]=0.0213023298595; coefs[23]=0.0149306424448;
    coefs[24]=0.00775610845079; coefs[25]=7.19596327373e-05;
    coefs[26]=-0.00779814911724; coefs[27]=-0.0155153931071;
    coefs[28]=-0.0227420377225; coefs[29]=-0.029157861033;
    coefs[30]=-0.0344758782583; coefs[31]=-0.0384565377257;
    coefs[32]=-0.0409196345635; coefs[33]=0.958246692968;
    coefs[34]=-0.0409196345635; coefs[35]=-0.0384565377257;
    coefs[36]=-0.0344758782583; coefs[37]=-0.029157861033;
    coefs[38]=-0.0227420377225; coefs[39]=-0.0155153931071;
    coefs[40]=-0.00779814911724; coefs[41]=7.19596327373e-05;
    coefs[42]=0.00775610845079; coefs[43]=0.0149306424448;
    coefs[44]=0.0213023298595; coefs[45]=0.0266218283707;
    coefs[46]=0.0306946894724; coefs[47]=0.0333893677331;
    coefs[48]=0.034641871008; coefs[49]=0.0344568748437;
    coefs[50]=0.0329053185198; coefs[51]=0.0301186904011;
    coefs[52]=0.0262803858113; coefs[53]=0.0216146717685;
    coefs[54]=0.0163739114543; coefs[55]=0.0108247810173;
    coefs[56]=0.00523424838199; coefs[57]=-0.000143923209485;
    coefs[58]=-0.00508143390833; coefs[59]=-0.00938584401296;
    coefs[60]=-0.0129086065417; coefs[61]=-0.015550380848;
    coefs[62]=-0.0172634363391; coefs[63]=-0.0180511257341;
    coefs[64]=-0.0179645741715; coefs[65]=-0.0170968842151;
    coefs[66]=-0.0155752887632;
    for (int i = N_TAPS-2; i >= 0; i--) {
        circBuf[i+1] = circBuf[i];
    }
    circBuf[0] = sample;
    circBufIdx++;
    if (circBufIdx >= N_TAPS) {
        output = elementMul(coefs, circBuf);
    }

    // ********************* END YOUR CODE HERE *********************

    return output;
}





