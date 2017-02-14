//
// Created by daran on 1/8/2017.
//

#ifndef AUTOTUNE_ECE420_LIB_H
#define AUTOTUNE_ECE420_LIB_H

#include <math.h>
#include <vector>

#define FRAME_SIZE 960
#define BUFFER_SIZE (3 * FRAME_SIZE)
#define F_S 48000 // TODO this should be inferable from somewhere
//#define FREQ_NEW 300
#define EPOCH_PEAK_REGION_WIGGLE 30
#define VOICED_THRESHOLD 200000000

float getHanningCoef(int N, int idx);
int findMaxArrayIdx(float *array, int minIdx, int maxIdx);
int findClosestIdxInArray(float *array, float value, int minIdx, int maxIdx);
int findClosestInVector(std::vector<int> vector, float value, int minIdx, int maxIdx);

#endif //AUTOTUNE_ECE420_LIB_H
