import numpy as np
import matplotlib.pyplot as plt
import os
from scipy import signal

'''
    t : time sequence iterable
    sigs: signal vector
'''
def peak_detection(t,sigs,dt=125):
    peaks = []
    N = len(sigs)
    for i in range(dt,N-dt-1):
        left_neighbor = sigs[i-dt:i+1]
        right_neighbor = sigs[i:i+dt+1]
        if max(left_neighbor) == sigs[i] and max(right_neighbor) == sigs[i]:
            peaks.append((t[i],sigs[i]))
    return np.array(peaks)

csv_filename = 'sample_sensor_data.csv'
data = np.genfromtxt(csv_filename,delimiter=',').T
timestamps = (data[0] - data[0,0])/1000

accel_data = data[1:4]
gyro_data = data[4:-1]

max_peaks = peak_detection(timestamps, accel_data[0])

plt.scatter(max_peaks[:,0], max_peaks[:,1], color = 'red')
#plt.scatter(timestamps,accel_data[0],color = 'black')


plt.plot(timestamps,accel_data[0])
plt.xlabel('Time')
plt.ylabel('Meters per second')
plt.title('sample sensor data')
plt.show()