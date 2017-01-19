import matplotlib.pyplot as plt
import numpy as np
from scipy import signal

# Your filter design here
# firls() can be called via signal.firls()
#b = ??

sampling_freq = 48000  # Hz
band = [900, 1000]  # Hz
desired = [1, 0]

b = signal.firls(73, bands=band, desired=desired, nyq=sampling_freq)

# Signal analysis
w, h = signal.freqz(b)

plt.figure()
plt.subplot(2,1,1)
plt.title('Digital filter frequency response, N = ' + str(len(b)))
plt.plot(w / np.pi, 20 * np.log10(abs(h)), 'b')
plt.ylabel('Amplitude [dB]', color='b')
plt.grid()
plt.axis('tight')

plt.subplot(2,1,2)
angles = np.unwrap(np.angle(h))
plt.plot(w / np.pi, angles, 'g')
plt.ylabel('Angle (radians)', color='g')
plt.grid()
plt.axis('tight')
plt.xlabel('Frequency [0 to Nyquist Hz, normalized]')
plt.show()
