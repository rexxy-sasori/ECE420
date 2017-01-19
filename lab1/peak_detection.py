import matplotlib.pyplot as plt
import numpy as np

'''
    t : time sequence iterable
    sigs: signal vector
    dt: neighboring data points
    thresh: threshold

    idea:
    check elements from index current-dt to current+dt
    if the current element is bigger than its neighbors and threshold, peak is detected

    dt and thresh are set to optimal combinations : dt = 25 threshold = 3

'''
def peak_detection(t,sigs,dt=25,thresh=3):
    peaks = []
    N = len(sigs)

    for i in range(dt,N-dt-1):
        left_neighbor = sigs[i-dt:i+1]
        right_neighbor = sigs[i:i+dt+1]
        if max(left_neighbor) == sigs[i] and max(right_neighbor) == sigs[i] and sigs[i] >= thresh:
            peaks.append((t[i],sigs[i]))

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