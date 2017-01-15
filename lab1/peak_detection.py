import numpy as np
import matplotlib.pyplot as plt
import os

'''
    t : time sequence iterable
    sig: signal vector
'''
def peak_detection(t,sigs):
    peaks = []
    max_val = -np.Inf

    for time,sig in zip(t,sigs):
        if sig > max_val:
            max_val = sig
            position = time

    peaks.append((position,max_val))
    return np.array(peaks)


csv_filename = 'sample_sensor_data.csv'
data = np.genfromtxt(csv_filename,delimiter=',').T
timestamps = (data[0] - data[0,0])/1000

accel_data = data[1:4]
gyro_data = data[4:-1]

max_peaks = peak_detection(timestamps, accel_data[0])
plt.scatter(max_peaks[:,0], max_peaks[:,1], color = 'red')


plt.plot(timestamps,accel_data[0])
plt.xlabel('Time')
plt.ylabel('Meters per second')
plt.title('sample sensor data')
plt.show()