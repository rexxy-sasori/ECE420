import numpy as np
import matplotlib.pyplot as plt
import scipy
from scipy import signal

sampling_freq = 48000  # Hz
nyq = sampling_freq / 2

stopband_start = 800
stopband_end = 2200

order = 4
b,a = signal.butter(order,[stopband_start/nyq,stopband_end/nyq],btype = "bandstop",analog = False)
w,h = scipy.signal.freqz(b,a)
plt.semilogx(sampling_freq*w/2/np.pi,20*np.log10(abs(h)))
plt.margins(0,0.01)
plt.grid(which = 'both',axis = 'both')
plt.show()


coef_str_b = "float coefs_b[] = {"

for val in b:
    coef_str_b += str(val) + ", "

coef_str_b = coef_str_b[:-2]
coef_str_b += "};"

print(coef_str_b)


coef_str_a = "float coefs_a[] = {"

for val in a:
    coef_str_a += str(val) + ", "

coef_str_a = coef_str_a[:-2]
coef_str_a += "};"

print(coef_str_a)

print(len(a))
print(len(b))