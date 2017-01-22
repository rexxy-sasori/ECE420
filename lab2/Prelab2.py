
# coding: utf-8

# In[1]:

from scipy.io.wavfile import read,write
from IPython.display import Audio
import numpy as np
import numpy.fft as fft
import matplotlib.pyplot as plt
import scipy.signal

print ("Modules imported")


# In[37]:

#importing wav files
sampling_hum,data_hum = read("with_hum.wav")
sampling_no_hum,data_no_hum = read("without_hum.wav")
#playing files
Audio("with_hum.wav")
#Audio("without_hum.wav")


# In[235]:

#analysis
#with_hum

size = len(data_hum)
pi = np.pi
HUM = np.fft.fft(data_hum,size)
HUM = np.fft.fftshift(HUM)

w = 2*pi*(np.fft.fftfreq(size))
w = np.fft.fftshift(w)
analog = sampling_hum * w /2/pi

plt.plot(analog,20*np.log10(np.absolute(HUM)))
plt.title("Spectrum of with_hum")
plt.xlabel("frequency(Hz)")
plt.ylabel("Magnitude")




# In[186]:

#analysis
#without hum
size_no = len(data_no_hum)
NO_HUM = np.fft.fft(data_no_hum,size_no)
NO_HUM = np.fft.fftshift(NO_HUM)

w_no = 2*pi*(np.fft.fftfreq(size_no))
w_no = np.fft.fftshift(w_no)
analog_no = sampling_no_hum*w_no/2/pi

plt.plot(analog_no,np.absolute(NO_HUM))
plt.title("Spectrum of without_hum")
plt.xlabel("frequency(Hz)")
plt.ylabel("Magnitude")


# In[187]:

FRE = []
for x,y in zip(analog_no,np.absolute(NO_HUM)):
    FRE.append((x,y))
print (min(FRE,key = lambda t:t[1]))


# In[226]:

#filter design



wp = [300*2*pi,500*2*pi]
ws = [350*2*pi,450*2*pi]
gpass = 0.25
gstop = 90

b,a = scipy.signal.iirdesign(wp,ws,gpass,gstop,analog = True)
b_d,a_d = scipy.signal.bilinear(b,a,fs = sampling_hum)



w,h = scipy.signal.freqz(b_d,a_d)
plt.semilogx(w*sampling_hum/2/pi, 20 * np.log10(abs(h)))
plt.title("Butterworth filter frequency response")
plt.xlabel("Frequency(Hz)")
plt.ylabel("Magnitude(dB)")
plt.margins(0, 0.1)
plt.grid(which='both', axis='both')

plt.figure()

angle = np.unwrap(np.angle(h))
plt.plot(w*sampling_hum/2/pi,angle,'g')
plt.margins(0, 0.1)
plt.grid(which='both', axis='both')
plt.title("Butterworth filter phase response")
plt.xlabel("Frequency(Hz)")
plt.ylabel("Phase")


# In[231]:

# filter the signal
y = scipy.signal.lfilter(b_d, a_d, data_hum).astype('int')
print(y)
print(data_no_hum)


# In[233]:

write("wi.wav",sampling_hum,3*y)
Audio("wi.wav")


# In[229]:

size_no = len(y)
NO_HUM = np.fft.fft(y,size_no)
NO_HUM = np.fft.fftshift(NO_HUM)

w_no = 2*pi*(np.fft.fftfreq(size_no))
w_no = np.fft.fftshift(w_no)
analog_no = sampling_no_hum*w_no/2/pi

plt.plot(analog_no,(np.absolute(NO_HUM)))
plt.title("Spectrum of without_hum")
plt.xlabel("frequency(Hz)")
plt.ylabel("Magnitude")


# In[230]:

size_no = len(data_no_hum)
NO_HUM = np.fft.fft(data_no_hum,size_no)
NO_HUM = np.fft.fftshift(NO_HUM)

w_no = 2*pi*(np.fft.fftfreq(size_no))
w_no = np.fft.fftshift(w_no)
analog_no = sampling_no_hum*w_no/2/pi

plt.plot(analog_no,np.absolute(NO_HUM))
plt.title("Spectrum of without_hum")
plt.xlabel("frequency(Hz)")
plt.ylabel("Magnitude")


# In[ ]:



