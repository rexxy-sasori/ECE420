import matplotlib.pyplot as plt
import numpy as np
from scipy import signal


# Your filter design here
# firls() can be called via signal.firls()
# b = ??

# goal stopband down : -20dB
sampling_freq = 48000  # Hz
nyq = sampling_freq / 2

stopband_left_one = 900
stopband_left_two = 1100
stopband_right_one = 1900
stopband_right_two = 2100

tap = 67

band = [0,
        stopband_left_one,
        stopband_left_two,
        stopband_right_one,
        stopband_right_two,
        24000]

desired = [1, 1, 0, 0, 1, 1]

b = signal.firls(tap, band, desired, nyq=nyq)
#c++ convertion
coef_str = "float coefs[] = {"

for val in b:
    coef_str += str(val) + ", "

coef_str = coef_str[:-2]
coef_str += "};"

print(len(coef_str))

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
plt.figure()
plt.plot(b)
plt.show()



'''
testing
'''

F_s = 48000
t = [i / F_s for i in range(2 * F_s)]

test_data = signal.chirp(t, 1, t[-1], 24000, method='logarithmic')


def my_convolution(data, filter):
    out_put = np.zeros(len(b) + len(test_data) - 1)

    left = 0
    right = len(b)

    for i in range(len(out_put)):
        if i < len(b) - 1:
            right = i + 1

        elif i > len(test_data) - 1:
            left = i + 1 - len(test_data)

        for j in range(left, right):
            out_put[i] = out_put[i] + test_data[i - j] * b[j]

    return np.array(out_put)


out = my_convolution(data=test_data, filter=b)


plt.plot(out, 'r')
plt.figure()
plt.plot(signal.convolve(test_data, b), "g")

plt.show()

