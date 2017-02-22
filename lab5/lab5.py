import numpy as np
from numpy.fft import fft
import matplotlib.pyplot as plt
import scipy.io.wavfile as spwav
from mpldatacursor import datacursor
import sys
from scipy import signal
from IPython.display import Audio


plt.style.use('ggplot')

# Note: this epoch list only holds for "test_vector_all_voiced.wav"
epoch_marks_orig = np.load("test_vector_all_voiced_epochs.npy")
F_s, audio_data = spwav.read("test_vector_all_voiced.wav")
N = len(audio_data)


######################## YOUR CODE HERE ##############################

#plt.plot(audio_data)
#plt.show()

F_new = 441

new_epoch_spacing = F_s / F_new
audio_out = np.zeros(N)
#print(epoch_marks_orig)

def find_nearest(array, value):
    idx = (np.abs(array - value)).argmin()
    return array[idx],idx

# Suggested loop
space = (int)(new_epoch_spacing)

for i in range(space,N-space,space):
    closest,closest_idx = find_nearest(epoch_marks_orig,i)

    if closest_idx == 0:
        p0 = closest
    elif closest_idx == len(epoch_marks_orig) - 1 or closest_idx == len(epoch_marks_orig):
        p0 = (int)((epoch_marks_orig[closest_idx]-epoch_marks_orig[closest_idx-1])/2)
    else:
        p0 = (int)((epoch_marks_orig[closest_idx+1]-epoch_marks_orig[closest_idx-1])/2)
    #'''
    start_window = closest - p0
    end_window = closest + p0 + 1
    current_impulse = audio_data[start_window:end_window]

    print("closest",closest,
          "current new spacing",i,
          "current p0",p0,
          "current size",len(current_impulse))

    current_impulse = signal.triang(len(current_impulse)) * current_impulse

    start_accept = i - p0
    end_accept = i + p0 + 1
    if start_accept < 0:
        start_accept = 0
        end_accept = len(current_impulse)
    if end_accept > N:
        end_accept = N
        start_accept = N - len(current_impulse)


    audio_out[start_accept:end_accept] += current_impulse
    #'''


spwav.write("audio_out.wav", rate = F_s, data = audio_out)
plt.plot(audio_data,'g')
plt.hold(True)
plt.plot(audio_out,'b')
plt.show()

