import numpy as np
import matplotlib.pyplot as plt
from scipy.io.wavfile import read, write
from numpy.fft import fft, ifft
from scipy import signal

FRAME_SIZE = 1024
ZP_FACTOR = 2
FFT_SIZE = FRAME_SIZE * ZP_FACTOR
scale = 10


################## YOUR CODE HERE ######################
def ece420ProcessFrame(frame):
    curFft = np.zeros(FFT_SIZE)
    window_frame = signal.hamming(FRAME_SIZE) * frame
    curFft = fft(window_frame,n = FFT_SIZE)
    curFft = curFft[0:int(FFT_SIZE/2)]

    curFft = np.log10(np.square((np.absolute(curFft))))/scale

    return curFft

################# GIVEN CODE BELOW #####################

Fs, data = read('test_single_tones.wav')

numFrames = int(len(data) / FRAME_SIZE)
bmp = np.zeros((numFrames, int(FFT_SIZE / 2)))

for i in range(numFrames):
    frame = data[i * FRAME_SIZE : (i + 1) * FRAME_SIZE]
    curFft = ece420ProcessFrame(frame)
    bmp[i, :] = curFft

plt.figure()
plt.pcolormesh(bmp.T, vmin=0, vmax=1)
plt.axis('tight')
plt.show()