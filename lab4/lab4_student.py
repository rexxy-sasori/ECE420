import numpy as np
import matplotlib.pyplot as plt
from scipy.io.wavfile import read, write
from numpy.fft import fft, ifft

FRAME_SIZE = 2048

################## YOUR CODE HERE ######################
def ece420ProcessFrame(frame, Fs):
    freq = -1

    Es = np.sum(np.square(np.absolute(frame)))

    THRESH = 3000000000
    if(Es > THRESH):
        X = np.fft.fft(frame)
        A = np.square(np.absolute(X))
        a = np.fft.ifft(A)

        max = -np.inf
        l = 0
        for i in range(50,int(FRAME_SIZE/2)):
            if(np.absolute(a[i]) > max):
                max = a[i]
                l = i

        if(l != 0):
            freq = Fs/l

    return freq



################# GIVEN CODE BELOW #####################

Fs, data = read('test_vector.wav')

print(len(data))
numFrames = int(len(data) / FRAME_SIZE)
frequencies = np.zeros(numFrames)

for i in range(numFrames):
    frame = data[i * FRAME_SIZE : (i + 1) * FRAME_SIZE]
    frequencies[i] = ece420ProcessFrame(frame.astype(float), Fs)


plt.figure()
plt.plot(frequencies)
plt.axis('tight')
plt.xlabel('Frame idx')
plt.ylabel('Hz')
plt.title('Detected Frequencies in Hz')
plt.show()
