import matplotlib.pyplot as plt
import numpy as np
from scipy import signal

# Your filter design here
# firls() can be called via signal.firls()
# b = ??

# goal stopband down : -20dB
sampling_freq = 48000  # Hz
nyq = sampling_freq / 2

stopband_left_one = 975
stopband_left_two = 1025
stopband_right_one = 1975
stopband_right_two = 2025

tap = 67

band = [0,
        stopband_left_one,
        stopband_left_two,
        stopband_right_one,
        stopband_right_two,
        24000]

desired = [1, 1, 0, 0, 1, 1]

b = signal.firls(tap, band, desired, nyq=nyq)

# Signal analysis
w, h = signal.freqz(b)

plt.figure()
plt.subplot(2, 1, 1)
plt.title('Digital filter frequency response, N = ' + str(len(b)))
plt.plot(w / np.pi, 20 * np.log10(abs(h)), 'b')
plt.ylabel('Amplitude [dB]', color='b')
plt.grid()
plt.axis('tight')

plt.subplot(2, 1, 2)
angles = np.unwrap(np.angle(h))
plt.plot(w / np.pi, angles, 'g')
plt.ylabel('Angle (radians)', color='g')
plt.grid()
plt.axis('tight')
plt.xlabel('Frequency [0 to Nyquist Hz, normalized]')
plt.show()

'''
testing
'''
F_s = 48000
t = [i / F_s for i in range(2 * F_s)]

test_data = signal.chirp(t, 1, t[-1], 24000, method='logarithmic')


def my_convolution(data, filter):
    N = len(test_data)
    M = len(b)
    out_put = [0 for i in range(N + M - 1)]
    left = 0
    right = 0

    for i in range(len(out_put)):
        if i >= M - 1 and i <= N - 1:
            '''
            if the filter is within the data
            '''
            left = 0
            right = M

        elif i < M - 1:
            '''
            left out of range
            '''
            left = 0
            right = i + 1

        elif i > N - 1:
            '''
            right out of range
            '''
            left = i + 1 - N
            right = M
        for j in range(left, right):
            out_put[i] = out_put[i] + test_data[i - j] * b[j]

    return out_put


out = my_convolution(data=test_data, filter=b)

plt.plot(out, 'r')
plt.figure()
plt.plot(test_data)

plt.show()
